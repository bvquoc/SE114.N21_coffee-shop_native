package com.example.coffee_shop_app.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.coffee_shop_app.Data;
import com.example.coffee_shop_app.R;
import com.example.coffee_shop_app.activities.store.StoreDetailActivity;
import com.example.coffee_shop_app.adapters.StoreAdapter;
import com.example.coffee_shop_app.databinding.ActivitySearchFoodBinding;
import com.example.coffee_shop_app.databinding.ActivityStoreSearchBinding;
import com.example.coffee_shop_app.models.Store;
import com.example.coffee_shop_app.utils.interfaces.OnProductClickListener;
import com.example.coffee_shop_app.utils.interfaces.OnStoreClickListener;
import com.example.coffee_shop_app.utils.styles.RecyclerViewGapDecoration;
import com.example.coffee_shop_app.adapters.ProductAdapter;
import com.example.coffee_shop_app.models.Product;
import com.example.coffee_shop_app.viewmodels.ProductSearchViewModel;
import com.example.coffee_shop_app.viewmodels.StoreSearchViewModel;

import java.util.ArrayList;
import java.util.List;

public class SearchFoodActivity extends AppCompatActivity {
    private static final String TAG = "SearchFoodActivity";
    private ActivitySearchFoodBinding activitySearchFoodBinding;
    private ProductAdapter productAdapter;
    private final Handler handler = new Handler();
    private Runnable searchRunnable;
    private final int MILISECOND_DELAY_SEARCH  = 300;
    private OnProductClickListener listener;
    private final ActivityResultLauncher<Intent> activitySeeProductDetailResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        Intent intent = new Intent();
                        intent.putExtra("productId", data.getStringExtra("productId"));
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                } else {
                    //User do nothing
                }
            }
    );
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_food);

        activitySearchFoodBinding = DataBindingUtil.setContentView(this, R.layout.activity_search_food);

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        toolbar.setTitle("Tìm kiếm cửa hàng");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        listener = new OnProductClickListener() {
            @Override
            public void onProductClick(String productId) {
                Intent intent = new Intent(getApplicationContext(), ProductDetailActivity.class);
                intent.putExtra("productId", productId);
                activitySeeProductDetailResultLauncher.launch(intent);
            }
        };

        init();
    }
    private void init() {
        ProductSearchViewModel productSearchViewModel = new ProductSearchViewModel();
        productAdapter = new ProductAdapter(new ArrayList<Product>(), true);
        productAdapter.setOnProductClickListener(listener);
        activitySearchFoodBinding.searchProductRecyclerview.setAdapter(productAdapter);
        productSearchViewModel.getLiveProductData().observe(this, productList -> {
            if (productList == null) {
                productSearchViewModel.getIsLoading().setValue(true);
                return;
            }
            productAdapter.changeDataSet(productList);
            productSearchViewModel.getIsLoading().setValue(false);
        });
        productSearchViewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading) {
                activitySearchFoodBinding.main.setVisibility(View.GONE);
                activitySearchFoodBinding.shimmerLayout.setVisibility(View.VISIBLE);
                activitySearchFoodBinding.shimmerLayout.startShimmer();
            } else {
                activitySearchFoodBinding.main.setVisibility(View.VISIBLE);
                activitySearchFoodBinding.shimmerLayout.setVisibility(View.GONE);
                activitySearchFoodBinding.shimmerLayout.stopShimmer();
            }
        });
        activitySearchFoodBinding.setViewModel(productSearchViewModel);
        activitySearchFoodBinding.searchProductRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        activitySearchFoodBinding.searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeCallbacks(searchRunnable);
                searchRunnable = new Runnable() {
                    @Override
                    public void run() {
                        productAdapter.getFilter().filter(s);
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