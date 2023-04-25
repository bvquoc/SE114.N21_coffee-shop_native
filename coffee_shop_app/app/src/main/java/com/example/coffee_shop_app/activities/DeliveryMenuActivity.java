package com.example.coffee_shop_app.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.coffee_shop_app.Data;
import com.example.coffee_shop_app.adapters.ProductItemAdapter;
import com.example.coffee_shop_app.R;
import com.example.coffee_shop_app.utils.styles.RecyclerViewGapDecoration;

public class DeliveryMenuActivity extends AppCompatActivity {
    private RecyclerView productRecyclerView;
    private RecyclerView favoriteProductRecyclerView;
    private LinearLayout productsLayout;
    private ConstraintLayout storeDeliveryPickerView;
    private ConstraintLayout storeDeliveryPickerFrame;
    private ConstraintLayout addressPickerView;
    private ProductItemAdapter productsAdapter;
    private ProductItemAdapter favoriteProductsAdapter;
    private Button scrollButton;
    private NestedScrollView nestedScrollView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_menu_page, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.action_search:
            {
                Intent intent = new Intent(getApplicationContext(), SearchFoodActivity.class);
                startActivity(intent);
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_menu);

        float dp = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics());


        storeDeliveryPickerView = findViewById(R.id.store_picker);
        storeDeliveryPickerFrame = findViewById(R.id.store_picker_frame);
        addressPickerView = findViewById(R.id.address_picker_frame);
        productRecyclerView = findViewById(R.id.product_item_recycler_view);
        favoriteProductRecyclerView = findViewById(R.id.product_item_favorites_recycler_view);
        productsLayout = findViewById(R.id.products_linear_layout);
        scrollButton = findViewById(R.id.scroll_button);
        nestedScrollView = findViewById(R.id.nested_scroll_view);

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        toolbar.setTitle("Delivery");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        productRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        favoriteProductRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        productRecyclerView.addItemDecoration(new RecyclerViewGapDecoration((int) (8*dp)));
        favoriteProductRecyclerView.addItemDecoration(new RecyclerViewGapDecoration((int) (8*dp)));

        productsAdapter = new ProductItemAdapter(Data.instance.products);
        favoriteProductsAdapter = new ProductItemAdapter(Data.instance.favoriteProducts);

        productRecyclerView.setAdapter(productsAdapter);
        favoriteProductRecyclerView.setAdapter(favoriteProductsAdapter);

        storeDeliveryPickerFrame.post(new Runnable() {
            @Override
            public void run() {
                ConstraintLayout.LayoutParams layoutParamsRecyclerView = (ConstraintLayout.LayoutParams) productsLayout.getLayoutParams();
                layoutParamsRecyclerView.setMargins((int) (16*(dp)), storeDeliveryPickerFrame.getHeight(), (int) (16*(dp)), (int) (16*(dp)));
                productsLayout.setLayoutParams(layoutParamsRecyclerView);
                nestedScrollView.scrollTo(0,0);
            }
        });

        scrollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nestedScrollView.smoothScrollTo(0,0);
            }
        });
        nestedScrollView.getViewTreeObserver().addOnScrollChangedListener(() -> {
            int scrollViewY = nestedScrollView.getScrollY();

            if (scrollViewY >= addressPickerView.getHeight()) {
                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) storeDeliveryPickerFrame.getLayoutParams();
                layoutParams.setMargins(0, 0, 0, 0);

                storeDeliveryPickerFrame.
                        setLayoutParams(layoutParams);
                scrollButton.setVisibility(View.VISIBLE);
            } else {
                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) storeDeliveryPickerFrame.getLayoutParams();
                layoutParams.setMargins(0, addressPickerView.getHeight() - scrollViewY, 0, 0);

                storeDeliveryPickerFrame.
                        setLayoutParams(layoutParams);
                scrollButton.setVisibility(View.GONE);
            }
        });

        addressPickerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddressListingActivity.class);
                startActivity(intent);
            }
        });
    }
}