package com.example.coffee_shop_app.activities.store;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.example.coffee_shop_app.R;
import com.example.coffee_shop_app.adapters.ImageViewPagerAdapter;
import com.example.coffee_shop_app.databinding.ActivityStoreDetailBinding;
import com.example.coffee_shop_app.models.Store;
import com.example.coffee_shop_app.viewmodels.OrderType;
import com.example.coffee_shop_app.viewmodels.StoreDetailViewModel;

import java.util.ArrayList;


public class StoreDetailActivity extends AppCompatActivity {
    private ActivityStoreDetailBinding activityStoreDetailBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_detail);

        activityStoreDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_store_detail);

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Intent intent = getIntent();
        String storeId = intent.getStringExtra("storeId");
        init(storeId);
    }
    private void init(String storeId)
    {
        StoreDetailViewModel storeDetailViewModel = new StoreDetailViewModel(storeId);
        storeDetailViewModel.setIndex(1);
        storeDetailViewModel.getStoreMutableLiveData().observe(this, store->{
            if(store!=null)
            {
                activityStoreDetailBinding.viewpagerImage.setAdapter(
                        new ImageViewPagerAdapter(
                                store.getImages().toArray(
                                        new String[store.getImages().size()]
                                )
                        )
                );
                storeDetailViewModel.setImageCount(store.getImages().size());
                storeDetailViewModel.setStore(store);
            }
            else
            {
                activityStoreDetailBinding.viewpagerImage.setAdapter(
                        new ImageViewPagerAdapter(
                                new String[]{}
                        )
                );
                storeDetailViewModel.setImageCount(0);
                storeDetailViewModel.setStore(null);
            }
        });
        storeDetailViewModel.getIsLoading().observe(this, isLoading->{
            if(isLoading)
            {
                activityStoreDetailBinding.nestedScrollView.setVisibility(View.GONE);
                activityStoreDetailBinding.shimmerLayout.setVisibility(View.VISIBLE);
                activityStoreDetailBinding.shimmerLayout.startShimmer();
            }
            else
            {
                activityStoreDetailBinding.nestedScrollView.setVisibility(View.VISIBLE);
                activityStoreDetailBinding.shimmerLayout.setVisibility(View.GONE);
                activityStoreDetailBinding.shimmerLayout.stopShimmer();
            }
        });
        activityStoreDetailBinding.setViewModel(storeDetailViewModel);
        activityStoreDetailBinding.viewpagerImage.setOffscreenPageLimit(1);
        activityStoreDetailBinding.viewpagerImage.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                storeDetailViewModel.setIndex(position + 1);
            }
        });
        activityStoreDetailBinding.storePickupPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("orderType", OrderType.StorePickUp);
                intent.putExtra("storeId", storeDetailViewModel.getStore().getId());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        activityStoreDetailBinding.deliveryPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("orderType", OrderType.Delivery);
                intent.putExtra("storeId", storeDetailViewModel.getStore().getId());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}