package com.example.coffee_shop_staff_admin.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.coffee_shop_staff_admin.R;
import com.example.coffee_shop_staff_admin.databinding.ActivitySizeAdminEditBinding;
import com.example.coffee_shop_staff_admin.models.Size;
import com.example.coffee_shop_staff_admin.repositories.SizeRepository;
import com.example.coffee_shop_staff_admin.utils.ValidateHelper;
import com.example.coffee_shop_staff_admin.utils.keyboard.KeyboardHelper;
import com.example.coffee_shop_staff_admin.viewmodels.SizeAdminEditViewModel;

import java.io.IOException;

public class SizeAdminEditActivity extends AppCompatActivity {
    private final String TAG = "SizeAdminEditActivity";
    private ActivitySizeAdminEditBinding activitySizeAdminEditBinding;
    private final SizeAdminEditViewModel sizeAdminEditViewModel = new SizeAdminEditViewModel();
    private AsyncTask<Void, Void, Void> updateSizeTask;
    private String sizeId = null;
    private Toolbar toolbar;
    private final ActivityResultLauncher<Intent> chooseImageResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null && data.getData() != null) {
                        Uri selectedImageUri = data.getData();
                        sizeAdminEditViewModel.getImageSource().postValue(selectedImageUri);
                    }
                }
            });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activitySizeAdminEditBinding = DataBindingUtil.setContentView(this,R.layout.activity_size_admin_edit);

        toolbar = findViewById(R.id.my_toolbar);
        toolbar.setTitle("Tạo size");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        Intent intent = getIntent();
        sizeId = intent.getStringExtra("sizeId");
        String sizeImage = intent.getStringExtra("sizeImage");
        String sizeName = intent.getStringExtra("sizeName");
        double sizePrice = intent.getDoubleExtra("sizePrice",0);

        Size size = null;
        if(sizeId != null)
        {
            size = new Size(sizeId, sizeName, sizePrice, sizeImage);
        }
        init(size);
    }
    void init(Size size){
        if(size != null)
        {
            sizeAdminEditViewModel.updateParameter(size);
            toolbar.setTitle("Thay đổi size");
        }
        sizeAdminEditViewModel.getImageSource().observe(this, s -> {
            String scheme = s.getScheme();
            if (scheme != null) {
                if (scheme.equals("https") || scheme.equals("gs")) {
                    //The image is from FireBase
                    try {
                        Glide.with(getApplicationContext())
                                .load(s)
                                .into(activitySizeAdminEditBinding.imageView);
                    }
                    catch (Exception e)
                    {
                        Log.e(TAG, e.getMessage());
                    }
                } else  {
                    //The image is from phone
                    Bitmap selectedImageBitmap;
                    try {
                        selectedImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), s);
                        activitySizeAdminEditBinding.imageView.setImageBitmap(selectedImageBitmap);
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                sizeAdminEditViewModel.setHasImage(true);
            } else {
                Log.e(TAG, "The URI does not have a scheme or is invalid");
            }

        });
        activitySizeAdminEditBinding.setViewModel(sizeAdminEditViewModel);

        activitySizeAdminEditBinding.loading.setOnTouchListener((v, event) -> {
            //Prevent any action when loading is visible
            return true;
        });

        KeyboardHelper.setKeyboardVisibilityListener(this, sizeAdminEditViewModel::setKeyBoardShow);

        activitySizeAdminEditBinding.button.setOnClickListener(v -> {
            if(updateSizeTask!=null)
            {
                updateSizeTask.cancel(true);
            }
            updateSizeTask = new UpdateSizeTask();
            updateSizeTask.execute();
        });

        activitySizeAdminEditBinding.chooseImageButton.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);

            chooseImageResultLauncher.launch(intent);
        });
    }
    private final class UpdateSizeTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            sizeAdminEditViewModel.setLoading(true);
        }
        @Override
        protected Void doInBackground(Void... params) {
            boolean isValid = true;
            if(!ValidateHelper.validateText(sizeAdminEditViewModel.getName()))
            {
                runOnUiThread(() -> activitySizeAdminEditBinding.nameEditTextFrame.setError("Vui lòng nhập tên"));
                isValid = false;
            }else
            {
                runOnUiThread(() -> activitySizeAdminEditBinding.nameEditTextFrame.setError(null));
            }
            if(!ValidateHelper.validateDouble(sizeAdminEditViewModel.getPrice()))
            {
                runOnUiThread(() -> activitySizeAdminEditBinding.priceEditTextFrame.setError("Vui lòng nhập số"));
                isValid = false;
            }else
            {
                runOnUiThread(() -> activitySizeAdminEditBinding.priceEditTextFrame.setError(null));
            }
            if(!isValid)
            {
                sizeAdminEditViewModel.setLoading(false);
                return null;
            }
            if(sizeAdminEditViewModel.getImageSource().getValue()==null)
            {
                sizeAdminEditViewModel.setLoading(false);
                runOnUiThread(() -> Toast.makeText(
                        SizeAdminEditActivity.this,
                        "Chưa chọn hình ảnh.",
                        Toast.LENGTH_SHORT
                ).show());
                return null;
            }
            if(sizeId != null)
            {
                Size size = new Size(
                        sizeId,
                        sizeAdminEditViewModel.getName(),
                        Double.parseDouble(sizeAdminEditViewModel.getPrice()),
                        sizeAdminEditViewModel.getImageSource().getValue().toString()
                );
                SizeRepository.getInstance().updateSize(size, (success, message) -> {
                    if(success)
                    {
                        Log.e(TAG, "update topping successfully.");
                        sizeAdminEditViewModel.setLoading(false);
                        runOnUiThread(() -> Toast.makeText(
                                SizeAdminEditActivity.this,
                                "Đã chỉnh size thành công.",
                                Toast.LENGTH_SHORT
                        ).show());
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                    else
                    {
                        Log.e(TAG, "update size failed.");
                        sizeAdminEditViewModel.setLoading(false);
                        runOnUiThread(() -> Toast.makeText(
                                SizeAdminEditActivity.this,
                                "Đã có lỗi xảy ra. Xin hãy thử lại sau.",
                                Toast.LENGTH_SHORT
                        ).show());
                    }
                });
            }
            else
            {
                Size size = new Size(
                        null,
                        sizeAdminEditViewModel.getName(),
                        Double.parseDouble(sizeAdminEditViewModel.getPrice()),
                        sizeAdminEditViewModel.getImageSource().getValue().toString()
                );
                SizeRepository.getInstance().insertSize(size, (success, message) -> {
                    if(success)
                    {
                        Log.e(TAG, "insert size successfully.");
                        sizeAdminEditViewModel.setLoading(false);
                        runOnUiThread(() -> Toast.makeText(
                                SizeAdminEditActivity.this,
                                "Đã thêm size thành công.",
                                Toast.LENGTH_SHORT
                        ).show());
                        finish();
                    }
                    else
                    {
                        Log.e(TAG, "insert size failed.");
                        sizeAdminEditViewModel.setLoading(false);
                        runOnUiThread(() -> Toast.makeText(
                                SizeAdminEditActivity.this,
                                "Đã có lỗi xảy ra. Xin hãy thử lại sau.",
                                Toast.LENGTH_SHORT
                        ).show());
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {

        }
    }
}