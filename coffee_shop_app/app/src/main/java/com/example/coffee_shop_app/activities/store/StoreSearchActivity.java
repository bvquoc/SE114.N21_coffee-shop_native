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
import com.example.coffee_shop_app.databinding.ActivityStoreSearchBinding;
import com.example.coffee_shop_app.fragments.StoresFragment;
import com.example.coffee_shop_app.models.Store;
import com.example.coffee_shop_app.utils.interfaces.OnStoreClickListener;
import com.example.coffee_shop_app.viewmodels.StoreSearchViewModel;

import java.util.ArrayList;

public class StoreSearchActivity extends AppCompatActivity {
    private static final String TAG = "StoreSearchActivity";
    private ActivityStoreSearchBinding activityStoreSearchBinding;
    private StoreAdapter storeAdapter;
    private Handler handler = new Handler();
    private Runnable searchRunnable;
    private final int MILISECOND_DELAY_SEARCH  = 300;
    private OnStoreClickListener listener;
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
        setContentView(R.layout.activity_store_search);

        activityStoreSearchBinding = DataBindingUtil.setContentView(this, R.layout.activity_store_search);

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        toolbar.setTitle("Tìm kiếm cửa hàng");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        boolean isPurposeForShowingDetail = getIntent().getBooleanExtra("isPurposeForShowingDetail", true);
        if (isPurposeForShowingDetail) {
            listener = new OnStoreClickListener() {
                @Override
                public void onStoreClick(String storeId) {
                    Intent intent = new Intent(getApplicationContext(), StoreDetailActivity.class);
                    intent.putExtra("storeId", storeId);
                    activitySeeStoreDetailResultLauncher.launch(intent);
                }
            };
        } else {
            listener = new OnStoreClickListener() {
                @Override
                public void onStoreClick(String storeId) {
                    Intent intent = new Intent(getApplicationContext(), StoreDetailActivity.class);
                    intent.putExtra("storeId", storeId);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            };
        }
        init();
    }
    private void init()
    {
        StoreSearchViewModel storeViewModel = new StoreSearchViewModel();
        storeAdapter = new StoreAdapter(new ArrayList<Store>(), true);
        storeAdapter.setOnClickListener(listener);
        activityStoreSearchBinding.findStoresRecyclerView.setAdapter(storeAdapter);
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
                activityStoreSearchBinding.findStoresRecyclerView.setVisibility(View.GONE);
                activityStoreSearchBinding.shimmerLayout.setVisibility(View.VISIBLE);
                activityStoreSearchBinding.shimmerLayout.startShimmer();
            }
            else
            {
                activityStoreSearchBinding.findStoresRecyclerView.setVisibility(View.VISIBLE);
                activityStoreSearchBinding.shimmerLayout.setVisibility(View.GONE);
                activityStoreSearchBinding.shimmerLayout.stopShimmer();
            }
        });
        activityStoreSearchBinding.setViewModel(storeViewModel);
        activityStoreSearchBinding.findStoresRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        activityStoreSearchBinding.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        activityStoreSearchBinding.editText.addTextChangedListener(new TextWatcher() {
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