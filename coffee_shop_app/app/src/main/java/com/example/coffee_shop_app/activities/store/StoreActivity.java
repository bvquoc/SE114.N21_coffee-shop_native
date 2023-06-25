package com.example.coffee_shop_app.activities.store;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.example.coffee_shop_app.R;
import com.example.coffee_shop_app.adapters.StoreAdapter;
import com.example.coffee_shop_app.databinding.ActivityStoreBinding;
import com.example.coffee_shop_app.fragments.StoresFragment;
import com.example.coffee_shop_app.models.Store;
import com.example.coffee_shop_app.viewmodels.StoreSearchViewModel;

import java.util.ArrayList;

public class StoreActivity extends AppCompatActivity {
    private static final String TAG = "StoreActivity";
    private ActivityStoreBinding activityStoreBinding;
    private StoreAdapter storeAdapter;
    private Handler handler = new Handler();
    private Runnable searchRunnable;
    private final int MILISECOND_DELAY_SEARCH  = 300;
    private StoresFragment.OnStoreTouchListener listener = new StoresFragment.OnStoreTouchListener() {
        @Override
        public void onStoreTouch(String storeId) {
            Intent intent = new Intent(getApplicationContext(), StoreDetailActivity.class);
            intent.putExtra("storeId", storeId);
            activitySeeStoreDetailResultLauncher.launch(intent);
        }
    } ;
    private ActivityResultLauncher<Intent> activitySeeStoreDetailResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        Intent intent = new Intent();
                        intent.putExtra("orderType", data.getSerializableExtra("orderType"));
                        intent.putExtra("storeId", data.getStringExtra("storeId"));
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                } else {
                    //User don't choose "Mang đi" or "Giao hàng"
                }
            }
    );
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        activityStoreBinding = DataBindingUtil.setContentView(this, R.layout.activity_store);

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        toolbar.setTitle("Tìm kiếm cửa hàng");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        init();
    }
    private void init()
    {
        StoreSearchViewModel storeViewModel = new StoreSearchViewModel();
        storeAdapter = new StoreAdapter(new ArrayList<Store>(), true);
        storeAdapter.setOnTouchListener(listener);
        activityStoreBinding.findStoresRecyclerView.setAdapter(storeAdapter);
        storeViewModel.getLiveStoreData().observe(this, storeList->{
            if(storeList == null)
            {
                storeViewModel.getIsLoading().setValue(true);
                return;
            }
            storeAdapter.changeDataSet(storeList);
            storeViewModel.getIsLoading().setValue(false);
        });
        storeViewModel.getIsLoading().observe(this, isLoading->{
            if(isLoading)
            {
                activityStoreBinding.findStoresRecyclerView.setVisibility(View.GONE);
                activityStoreBinding.shimmerLayout.setVisibility(View.VISIBLE);
                activityStoreBinding.shimmerLayout.startShimmer();
            }
            else
            {
                activityStoreBinding.findStoresRecyclerView.setVisibility(View.VISIBLE);
                activityStoreBinding.shimmerLayout.setVisibility(View.GONE);
                activityStoreBinding.shimmerLayout.stopShimmer();
            }
        });
        activityStoreBinding.setViewModel(storeViewModel);
        activityStoreBinding.findStoresRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        activityStoreBinding.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        activityStoreBinding.editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeCallbacks(searchRunnable);
                searchRunnable = new Runnable() {
                    @Override
                    public void run() {
                        storeAdapter.getFilter().filter(s);
                    }

                };
                handler.postDelayed(searchRunnable, MILISECOND_DELAY_SEARCH);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}