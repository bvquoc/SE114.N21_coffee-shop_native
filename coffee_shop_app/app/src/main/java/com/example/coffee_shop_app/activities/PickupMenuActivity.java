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
import com.example.coffee_shop_app.R;
import com.example.coffee_shop_app.utils.styles.RecyclerViewGapDecoration;
import com.example.coffee_shop_app.adapters.ProductItemAdapter;

public class PickupMenuActivity extends AppCompatActivity {
    private RecyclerView productRecyclerView;
    private RecyclerView favoriteProductRecyclerView;
    private LinearLayout productsLayout;
    private ConstraintLayout storesPickerView;
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
        setContentView(R.layout.activity_pickup_menu);

        float dp = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics());

        storesPickerView = findViewById(R.id.store_pickup_picker);
        productRecyclerView = findViewById(R.id.product_item_recycler_view);
        favoriteProductRecyclerView = findViewById(R.id.product_item_favorites_recycler_view);
        productsLayout = findViewById(R.id.products_linear_layout);
        scrollButton = findViewById(R.id.scroll_button);
        nestedScrollView = findViewById(R.id.nested_scroll_view);

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        toolbar.setTitle("Store pickup");
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

        nestedScrollView.post(new Runnable() {
            @Override
            public void run() {
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

            if (scrollViewY >= storesPickerView.getHeight()) {
                scrollButton.setVisibility(View.VISIBLE);
            } else {
                scrollButton.setVisibility(View.GONE);
            }
        });
    }
}