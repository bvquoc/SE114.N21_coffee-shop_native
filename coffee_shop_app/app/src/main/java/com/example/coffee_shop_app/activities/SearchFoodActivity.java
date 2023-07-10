package com.example.coffee_shop_app.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.example.coffee_shop_app.utils.styles.RecyclerViewGapDecoration;
import com.example.coffee_shop_app.adapters.ProductAdapter;
import com.example.coffee_shop_app.models.Product;

import java.util.ArrayList;
import java.util.List;

public class SearchFoodActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private EditText searchEditText;
    private TextView clearButton;
    private TextView introduceTextView;
    private RecyclerView searchProductRecycleView;
    private ProductAdapter searchProductAdapter;

    private Handler handler = new Handler();
    private Runnable searchRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_food);

        toolbar = findViewById(R.id.my_toolbar);
        searchEditText = findViewById(R.id.search_edit_text);
        clearButton = findViewById(R.id.clear_button);
        searchProductRecycleView = findViewById(R.id.search_product_recyclerview);
        introduceTextView = findViewById(R.id.introduce_text_view);

        float dp = TypedValue
                .applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        1,
                        getResources().getDisplayMetrics()
                );

        toolbar.setTitle("Search");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        List<Product> products = new ArrayList<Product>(Data.instance.products);
        for(int i = Data.instance.favoriteProducts.size() - 1; i >=0; i--)
        {
            Product favoriteProduct = Data.instance.favoriteProducts.get(i);
            for(int j = 0; j < products.size() - 1; j++)
            {
                Product product = products.get(j);
                Log.e(product.getId(), favoriteProduct.getId());
                if (product.getId().equals(favoriteProduct.getId())) {
                    products.remove(j);
                    products.add(0,favoriteProduct);
                    break;
                }
            }
        }

        searchProductRecycleView.setLayoutManager(new LinearLayoutManager(this));
        searchProductRecycleView.addItemDecoration(new RecyclerViewGapDecoration((int) (8*dp)));
        searchProductAdapter = new ProductAdapter(products, false, true);
        searchProductRecycleView.setAdapter(searchProductAdapter);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    clearButton.setVisibility(View.VISIBLE);
                } else {
                    clearButton.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                handler.removeCallbacks(searchRunnable);
                searchRunnable = new Runnable() {
                    @Override
                    public void run() {
                        searchProductAdapter.getFilter().filter(s);
                        if ( s!=null && s.length() > 0) {
                            introduceTextView.setVisibility(View.GONE);
                        } else {
                            introduceTextView.setVisibility(View.VISIBLE);
                        }
                    }

                };
                handler.postDelayed(searchRunnable, 300);
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchEditText.setText("");
            }
        });

        searchEditText.setText("");
    }
}