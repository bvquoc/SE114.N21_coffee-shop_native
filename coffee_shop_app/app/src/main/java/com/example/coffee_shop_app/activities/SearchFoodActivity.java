package com.example.coffee_shop_app.activities;

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
import com.example.coffee_shop_app.activities.store.StoreActivity;
import com.example.coffee_shop_app.databinding.ActivitySearchFoodBinding;
import com.example.coffee_shop_app.utils.interfaces.OnProductClickListener;
import com.example.coffee_shop_app.adapters.ProductAdapter;
import com.example.coffee_shop_app.viewmodels.CartButtonViewModel;
import com.example.coffee_shop_app.viewmodels.ProductSearchViewModel;

import java.util.ArrayList;

public class SearchFoodActivity extends AppCompatActivity {
    private ActivitySearchFoodBinding activitySearchFoodBinding;
    private ProductAdapter productAdapter;
    private final Handler handler = new Handler();
    private Runnable searchRunnable;
    private final int MILLISECOND_DELAY_SEARCH = 300;
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
                }
            }
    );
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_food);

        activitySearchFoodBinding = DataBindingUtil.setContentView(this, R.layout.activity_search_food);

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        toolbar.setTitle("Tìm kiếm món ăn");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        listener = productId -> {
            Intent intent = new Intent(getApplicationContext(), ProductDetailActivity.class);
            intent.putExtra("productId", productId);
            activitySeeProductDetailResultLauncher.launch(intent);
        };

        init();
    }
    private void init() {
        ProductSearchViewModel productSearchViewModel = new ProductSearchViewModel();
        productAdapter = new ProductAdapter(new ArrayList<>(), true);
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
        activitySearchFoodBinding.setCartButtonViewModel(CartButtonViewModel.getInstance());
        activitySearchFoodBinding.setViewModel(productSearchViewModel);
        activitySearchFoodBinding.searchProductRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        activitySearchFoodBinding.searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeCallbacks(searchRunnable);
                searchRunnable = () -> productAdapter.getFilter().filter(s);
                handler.postDelayed(searchRunnable, MILLISECOND_DELAY_SEARCH);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        activitySearchFoodBinding.storeSelectLinearLayout.setOnClickListener(v -> {
            Intent intent = new Intent(SearchFoodActivity.this, StoreActivity.class);
            startActivity(intent);
        });
    }
}