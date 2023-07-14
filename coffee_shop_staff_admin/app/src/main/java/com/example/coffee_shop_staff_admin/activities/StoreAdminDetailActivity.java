package com.example.coffee_shop_staff_admin.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.coffee_shop_staff_admin.R;
import com.example.coffee_shop_staff_admin.adapters.FoodAdminSizeAdapter;
import com.example.coffee_shop_staff_admin.adapters.FoodAdminToppingAdapter;
import com.example.coffee_shop_staff_admin.adapters.ImageViewPagerAdapter;
import com.example.coffee_shop_staff_admin.databinding.ActivityFoodAdminDetailBinding;
import com.example.coffee_shop_staff_admin.databinding.ActivityStoreAdminDetailBinding;
import com.example.coffee_shop_staff_admin.models.Food;
import com.example.coffee_shop_staff_admin.models.Size;
import com.example.coffee_shop_staff_admin.models.Store;
import com.example.coffee_shop_staff_admin.models.Topping;
import com.example.coffee_shop_staff_admin.repositories.FoodRepository;
import com.example.coffee_shop_staff_admin.repositories.SizeRepository;
import com.example.coffee_shop_staff_admin.repositories.StoreRepository;
import com.example.coffee_shop_staff_admin.repositories.ToppingRepository;
import com.example.coffee_shop_staff_admin.utils.interfaces.UpdateDataListener;
import com.example.coffee_shop_staff_admin.viewmodels.FoodAdminDetailViewModel;
import com.example.coffee_shop_staff_admin.viewmodels.StoreAdminDetailViewModel;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StoreAdminDetailActivity extends AppCompatActivity {
    private final String TAG = "StoreAdminDetailActivity";
    private ActivityStoreAdminDetailBinding activityStoreAdminDetailBinding;
    private final StoreAdminDetailViewModel storeAdminDetailViewModel = new StoreAdminDetailViewModel();
    private String storeId;
    private Store selectedStore = null;
    private AsyncTask<Void, Void, Void> deleteStoreTask;

    private final ActivityResultLauncher<Intent> activityEditStoreResultLauncher = registerForActivityResult(
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

        activityStoreAdminDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_store_admin_detail);

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        toolbar.setTitle("Chi tiết cửa hàng");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        Intent intent = getIntent();
        storeId = intent.getStringExtra("storeId");
        init(storeId);
    }
    private void init(String storeId)
    {
        StoreRepository.getInstance().getStoreListMutableLiveData().observe(this, stores -> {
            storeAdminDetailViewModel.setLoading(true);
            selectedStore = null;
            if(stores == null)
            {
                return;
            }
            for (Store store : stores) {
                if(store.getId().equals(storeId))
                {
                    selectedStore = store;
                    break;
                }
            }
            if(selectedStore!=null)
            {
                storeAdminDetailViewModel.setIndex(1);
                storeAdminDetailViewModel.setImageCount(selectedStore.getImages().size());
                activityStoreAdminDetailBinding.viewpagerImage.setAdapter(
                        new ImageViewPagerAdapter(selectedStore.getImages())
                );
                storeAdminDetailViewModel.setName(selectedStore.getShortName());

                LocalDateTime localDateTimeOpen = LocalDateTime.ofInstant(
                        selectedStore.getTimeOpen().toInstant(),
                        ZoneId.systemDefault()
                );
                LocalDateTime localDateTimeClose = LocalDateTime.ofInstant(
                        selectedStore.getTimeClose().toInstant(),
                        ZoneId.systemDefault()
                );
                int hourOpen = localDateTimeOpen.getHour();
                int minuteOpen = localDateTimeOpen.getMinute();
                int hourClose = localDateTimeClose.getHour();
                int minuteClose = localDateTimeClose.getMinute();
                storeAdminDetailViewModel.setTimeActive("Open: " + String.format("%02d", hourOpen) + ":" + String.format("%02d", minuteOpen) +
                        " - " + String.format("%02d", hourClose) + ":" + String.format("%02d", minuteClose));

                storeAdminDetailViewModel.setPhone(selectedStore.getPhone());
                storeAdminDetailViewModel.setAddress(selectedStore.getAddress().getFormattedAddress());

                storeAdminDetailViewModel.setLoading(false);
            }
        });

        activityStoreAdminDetailBinding.setViewModel(storeAdminDetailViewModel);

        activityStoreAdminDetailBinding.viewpagerImage.setOffscreenPageLimit(1);
        activityStoreAdminDetailBinding.viewpagerImage.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                storeAdminDetailViewModel.setIndex(position + 1);
            }
        });

        activityStoreAdminDetailBinding.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedStore!=null)
                {
                    Intent intent = new Intent(StoreAdminDetailActivity.this, StoreAdminEditActivity.class);
                    intent.putExtra("storeId", storeId);
                    intent.putExtra("shortName", selectedStore.getShortName());
                    intent.putExtra("formattedAddress", selectedStore.getAddress().getFormattedAddress());
                    intent.putExtra("lat", selectedStore.getAddress().getLat());
                    intent.putExtra("lng", selectedStore.getAddress().getLng());
                    intent.putExtra("phone", selectedStore.getPhone());
                    intent.putExtra("timeOpen", selectedStore.getTimeOpen());
                    intent.putExtra("timeClose", selectedStore.getTimeClose());
                    intent.putStringArrayListExtra("images",  new ArrayList<>(selectedStore.getImages()));
                    activityEditStoreResultLauncher.launch(intent);
                }
            }
        });

        activityStoreAdminDetailBinding.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(deleteStoreTask!=null)
                {
                    deleteStoreTask.cancel(true);
                }
                deleteStoreTask = new DeleteStoreTask();
                deleteStoreTask.execute();
            }
        });
    }
    private final class DeleteStoreTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            storeAdminDetailViewModel.setUpdating(true);
        }
        @Override
        protected Void doInBackground(Void... params) {
            StoreRepository.getInstance().deleteStore(storeId, new UpdateDataListener() {
                @Override
                public void onUpdateData(boolean success) {
                    if(success)
                    {
                        storeAdminDetailViewModel.setUpdating(false);
                        Log.e(TAG, "delete store successfully.");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(
                                        StoreAdminDetailActivity.this,
                                        "Bạn đã xóa cửa hàng thành công",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        });
                        finish();
                    }
                    else
                    {
                        storeAdminDetailViewModel.setUpdating(false);
                        Log.e(TAG, "delete address failed.");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(
                                        StoreAdminDetailActivity.this,
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