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
import com.example.coffee_shop_staff_admin.databinding.ActivityToppingAdminEditBinding;
import com.example.coffee_shop_staff_admin.models.Topping;
import com.example.coffee_shop_staff_admin.repositories.ToppingRepository;
import com.example.coffee_shop_staff_admin.utils.ValidateHelper;
import com.example.coffee_shop_staff_admin.utils.keyboard.KeyboardHelper;
import com.example.coffee_shop_staff_admin.viewmodels.ToppingAdminEditViewModel;

import java.io.IOException;

public class ToppingAdminEditActivity extends AppCompatActivity {
    private final String TAG = "ToppingAdminEditActivity";
    private ActivityToppingAdminEditBinding activityToppingAdminEditBinding;
    private final ToppingAdminEditViewModel toppingAdminEditViewModel = new ToppingAdminEditViewModel();
    private AsyncTask<Void, Void, Void> updateToppingTask;
    private String toppingId = null;
    private Toolbar toolbar;
    private final ActivityResultLauncher<Intent> chooseImageResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null && data.getData() != null) {
                        Uri selectedImageUri = data.getData();
                        toppingAdminEditViewModel.getImageSource().postValue(selectedImageUri);
                    }
                }
            });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityToppingAdminEditBinding = DataBindingUtil.setContentView(this,R.layout.activity_topping_admin_edit);

        toolbar = findViewById(R.id.my_toolbar);
        toolbar.setTitle("Tạo topping");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        Intent intent = getIntent();
        toppingId = intent.getStringExtra("toppingId");
        String toppingImage = intent.getStringExtra("toppingImage");
        String toppingName = intent.getStringExtra("toppingName");
        double toppingPrice = intent.getDoubleExtra("toppingPrice",0);

        Topping topping = null;
        if(toppingId != null)
        {
            topping = new Topping(toppingId, toppingName, toppingPrice, toppingImage);
        }
        init(topping);
    }
    void init(Topping topping){
        if(topping != null)
        {
            toppingAdminEditViewModel.updateParameter(topping);
            toolbar.setTitle("Thay đổi topping");
        }
        toppingAdminEditViewModel.getImageSource().observe(this, s -> {
            String scheme = s.getScheme();
            if (scheme != null) {
                if (scheme.equals("https") || scheme.equals("gs")) {
                    //The image is from FireBase
                    try {
                        Glide.with(getApplicationContext())
                                .load(s)
                                .into(activityToppingAdminEditBinding.imageView);
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
                        Glide.with(ToppingAdminEditActivity.this)
                                .load(selectedImageBitmap)
                                .placeholder(R.drawable.img_placeholder)
                                .error(R.drawable.img_placeholder)
                                .into(activityToppingAdminEditBinding.imageView);
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                toppingAdminEditViewModel.setHasImage(true);
            } else {
                Log.e(TAG, "The URI does not have a scheme or is invalid");
            }

        });
        activityToppingAdminEditBinding.setViewModel(toppingAdminEditViewModel);

        activityToppingAdminEditBinding.loading.setOnTouchListener((v, event) -> {
            //Prevent any action when loading is visible
            return true;
        });

        KeyboardHelper.setKeyboardVisibilityListener(this, toppingAdminEditViewModel::setKeyBoardShow);

        activityToppingAdminEditBinding.button.setOnClickListener(v -> {
            if(updateToppingTask!=null)
            {
                updateToppingTask.cancel(true);
            }
            updateToppingTask = new UpdateToppingTask();
            updateToppingTask.execute();
        });

        activityToppingAdminEditBinding.chooseImageButton.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);

            chooseImageResultLauncher.launch(intent);
        });
    }
    private final class UpdateToppingTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            toppingAdminEditViewModel.setLoading(true);
        }
        @Override
        protected Void doInBackground(Void... params) {
            boolean isValid = true;
            if(!ValidateHelper.validateText(toppingAdminEditViewModel.getName()))
            {
                runOnUiThread(() -> activityToppingAdminEditBinding.nameEditTextFrame.setError("Vui lòng nhập tên"));
                isValid = false;
            }
            else
            {
                runOnUiThread(() -> activityToppingAdminEditBinding.nameEditTextFrame.setError(null));
            }
            if(!ValidateHelper.validateDouble(toppingAdminEditViewModel.getPrice()))
            {
                runOnUiThread(() -> activityToppingAdminEditBinding.priceEditTextFrame.setError("Vui lòng nhập số"));
                isValid = false;
            }
            else
            {
                runOnUiThread(() -> activityToppingAdminEditBinding.priceEditTextFrame.setError(null));
            }
            if(!isValid)
            {
                toppingAdminEditViewModel.setLoading(false);
                runOnUiThread(() -> Toast.makeText(
                        ToppingAdminEditActivity.this,
                        "Vui lòng điền đầy đủ thông tin.",
                        Toast.LENGTH_SHORT
                ).show());
                return null;
            }
            if(toppingAdminEditViewModel.getImageSource().getValue()==null)
            {
                toppingAdminEditViewModel.setLoading(false);
                runOnUiThread(() -> Toast.makeText(
                        ToppingAdminEditActivity.this,
                        "Chưa chọn hình ảnh.",
                        Toast.LENGTH_SHORT
                ).show());
                return null;
            }
            if(toppingId != null)
            {
                Topping topping = new Topping(
                        toppingId,
                        toppingAdminEditViewModel.getName(),
                        Double.parseDouble(toppingAdminEditViewModel.getPrice()),
                        toppingAdminEditViewModel.getImageSource().getValue().toString()
                );
                ToppingRepository.getInstance().updateTopping(topping, (success, message) -> {
                    if(success)
                    {
                        Log.e(TAG, "update topping successfully.");
                        toppingAdminEditViewModel.setLoading(false);
                        runOnUiThread(() -> Toast.makeText(
                                ToppingAdminEditActivity.this,
                                "Đã chỉnh topping thành công.",
                                Toast.LENGTH_SHORT
                        ).show());
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                    else
                    {
                        Log.e(TAG, "update topping failed.");
                        toppingAdminEditViewModel.setLoading(false);
                        runOnUiThread(() -> Toast.makeText(
                                ToppingAdminEditActivity.this,
                                "Đã có lỗi xảy ra. Xin hãy thử lại sau.",
                                Toast.LENGTH_SHORT
                        ).show());
                    }
                });
            }
            else
            {
                Topping topping = new Topping(
                        null,
                        toppingAdminEditViewModel.getName(),
                        Double.parseDouble(toppingAdminEditViewModel.getPrice()),
                        toppingAdminEditViewModel.getImageSource().getValue().toString()
                );
                ToppingRepository.getInstance().insertTopping(topping, (success, message) -> {
                    if(success)
                    {
                        Log.e(TAG, "insert topping successfully.");
                        toppingAdminEditViewModel.setLoading(false);
                        runOnUiThread(() -> Toast.makeText(
                                ToppingAdminEditActivity.this,
                                "Đã thêm topping thành công.",
                                Toast.LENGTH_SHORT
                        ).show());
                        finish();
                    }
                    else
                    {
                        Log.e(TAG, "insert topping failed.");
                        toppingAdminEditViewModel.setLoading(false);
                        runOnUiThread(() -> Toast.makeText(
                                ToppingAdminEditActivity.this,
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