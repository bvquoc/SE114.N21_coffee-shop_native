package com.example.coffee_shop_staff_admin.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.coffee_shop_staff_admin.R;
import com.example.coffee_shop_staff_admin.databinding.ActivitySizeAdminDetailBinding;
import com.example.coffee_shop_staff_admin.models.Size;
import com.example.coffee_shop_staff_admin.repositories.SizeRepository;
import com.example.coffee_shop_staff_admin.utils.interfaces.UpdateDataListener;
import com.example.coffee_shop_staff_admin.viewmodels.SizeAdminDetailViewModel;

import java.text.DecimalFormat;
import java.util.List;

public class SizeAdminDetailActivity extends AppCompatActivity {
    private String TAG = "SizeAdminDetailActivity";
    private ActivitySizeAdminDetailBinding activitySizeAdminDetailBinding;
    private String sizeId;
    private Size selectedSize;
    private AsyncTask<Void, Void, Void> deleteSizeTask;
    private final SizeAdminDetailViewModel sizeAdminDetailViewModel = new SizeAdminDetailViewModel();
    private final ActivityResultLauncher<Intent> activityEditSizeResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    finish();
                } else {
                    //User do nothing
                }
            }
    );
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activitySizeAdminDetailBinding = DataBindingUtil.setContentView(this,R.layout.activity_size_admin_detail);

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        toolbar.setTitle("Chi tiết size");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Intent intent = getIntent();
        sizeId = intent.getStringExtra("sizeId");

        init(sizeId);
    }
    void init(String sizeId){
        SizeRepository.getInstance().getSizeListMutableLiveData().observe(this, new Observer<List<Size>>() {
            @Override
            public void onChanged(List<Size> sizes) {
                sizeAdminDetailViewModel.setLoading(true);
                selectedSize = null;
                for (Size size: sizes) {
                    if(size.getId().equals(sizeId))
                    {
                        selectedSize = size;
                        break;
                    }
                }
                if(selectedSize != null)
                {
                    sizeAdminDetailViewModel.setName(selectedSize.getName());
                    DecimalFormat formatter = new DecimalFormat("###0.##");
                    String formattedPrice = formatter.format(selectedSize.getPrice());
                    sizeAdminDetailViewModel.setPrice(formattedPrice);

                    Glide.with(getApplicationContext())
                            .load(selectedSize.getImage())
                            .into(activitySizeAdminDetailBinding.imageView);

                    sizeAdminDetailViewModel.setLoading(false);
                }
            }
        });

        activitySizeAdminDetailBinding.setViewModel(sizeAdminDetailViewModel);

        activitySizeAdminDetailBinding.loading.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //Prevent any action when loading is visible
                return true;
            }
        });

        //Set the edittext can't be editable
        activitySizeAdminDetailBinding.nameEditText.setInputType(InputType.TYPE_NULL);
        activitySizeAdminDetailBinding.priceEditText.setInputType(InputType.TYPE_NULL);

        activitySizeAdminDetailBinding.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SizeAdminDetailActivity.this, SizeAdminEditActivity.class);
                intent.putExtra("sizeId", sizeId);
                intent.putExtra("sizeImage", selectedSize.getImage().toString());
                intent.putExtra("sizeName", selectedSize.getName());
                intent.putExtra("sizePrice", selectedSize.getPrice());
                activityEditSizeResultLauncher.launch(intent);
            }
        });

        activitySizeAdminDetailBinding.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(deleteSizeTask!=null)
                {
                    deleteSizeTask.cancel(true);
                }
                deleteSizeTask = new DeleteSizeTask();
                deleteSizeTask.execute();
            }
        });
    }
    private final class DeleteSizeTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            sizeAdminDetailViewModel.setUpdating(true);
        }
        @Override
        protected Void doInBackground(Void... params) {
            SizeRepository.getInstance().deleteSize(sizeId, new UpdateDataListener() {
                @Override
                public void onUpdateData(boolean success) {
                    if(success)
                    {
                        sizeAdminDetailViewModel.setUpdating(false);
                        Log.e(TAG, "delete size successfully.");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(
                                        SizeAdminDetailActivity.this,
                                        "Bạn đã xóa size thành công",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        });
                        finish();
                    }
                    else
                    {
                        sizeAdminDetailViewModel.setUpdating(false);
                        Log.e(TAG, "delete address failed.");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(
                                        SizeAdminDetailActivity.this,
                                        "Đã có lỗi xảy ra. Xin hãy thử lại sau.",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        });
                    }
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
        }
    }
}