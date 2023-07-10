package com.example.coffee_shop_app.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.coffee_shop_app.Data;
import com.example.coffee_shop_app.R;
import com.example.coffee_shop_app.utils.styles.RecyclerViewGapDecoration;
import com.example.coffee_shop_app.adapters.ProductAdapter;
import com.example.coffee_shop_app.models.Product;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private NestedScrollView scrollView;
    private ImageView orderTypeImageView;
    private TextView orderTypeTextView;
    private TextView addressTextView;
    private TextView recentSeeTextView;
    private Toolbar toolbar;
    private MotionLayout addressInfoMotionLayout;
    private MotionLayout priceAndAmountMotionLayout;
    private MotionLayout toolBarMotionLayout;
    private RecyclerView mayLikeProductRecyclerView;
    private RecyclerView recentProductRecyclerView;
    private LinearLayout storePickUpPicker;
    private LinearLayout deliveryPicker;
    private boolean isExcutingCartButtonAnimation1 = false;
    private boolean isExcutingCartButtonAnimation2 = false;
    private boolean isExcutingToolBarAnimation = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        scrollView = findViewById(R.id.scroll_view);
        orderTypeImageView = findViewById(R.id.image_order_type);
        orderTypeTextView = findViewById(R.id.text_order_type);
        addressTextView = findViewById(R.id.address_text_view);
        recentSeeTextView = findViewById(R.id.recent_see_text_view);
        addressInfoMotionLayout = findViewById(R.id.address_info_motion_layout);
        priceAndAmountMotionLayout = findViewById(R.id.cart_button_price_and_amount_motion_layout);
        toolBarMotionLayout = findViewById(R.id.tool_bar_motion_layout);
        toolbar = findViewById(R.id.my_toolbar);
        mayLikeProductRecyclerView = findViewById(R.id.product_item_may_like_recycler_view);
        recentProductRecyclerView = findViewById(R.id.product_item_recent_recycler_view);
        storePickUpPicker = findViewById(R.id.store_pickup_picker);
        deliveryPicker = findViewById(R.id.delivery_picker);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);



        float dp = TypedValue
                .applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        1,
                        getResources().getDisplayMetrics()
                );

        mayLikeProductRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mayLikeProductRecyclerView.addItemDecoration(new RecyclerViewGapDecoration(0, (int) (12*dp)));
        ProductAdapter mayLikeProductAdapter = new ProductAdapter(Data.instance.products.subList(0, 5), true);
        mayLikeProductRecyclerView.setAdapter(mayLikeProductAdapter);

        recentProductRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        recentProductRecyclerView.addItemDecoration(new RecyclerViewGapDecoration((int) (12*dp)));
        loadData();

        addressInfoMotionLayout.setTransitionListener(new MotionLayout.TransitionListener() {
            @Override
            public void onTransitionStarted(MotionLayout motionLayout, int startId, int endId) {
                isExcutingCartButtonAnimation1 = true;
            }

            @Override
            public void onTransitionChange(MotionLayout motionLayout, int startId, int endId, float progress) {

            }

            @Override
            public void onTransitionCompleted(MotionLayout motionLayout, int currentId) {
                isExcutingCartButtonAnimation1 = false;
            }

            @Override
            public void onTransitionTrigger(MotionLayout motionLayout, int triggerId, boolean positive, float progress) {

            }
        });

        priceAndAmountMotionLayout.setTransitionListener(new MotionLayout.TransitionListener() {
            @Override
            public void onTransitionStarted(MotionLayout motionLayout, int startId, int endId) {
                isExcutingCartButtonAnimation2 = true;
            }

            @Override
            public void onTransitionChange(MotionLayout motionLayout, int startId, int endId, float progress) {

            }

            @Override
            public void onTransitionCompleted(MotionLayout motionLayout, int currentId) {
                isExcutingCartButtonAnimation2 = false;
            }

            @Override
            public void onTransitionTrigger(MotionLayout motionLayout, int triggerId, boolean positive, float progress) {

            }
        });
        toolBarMotionLayout.setTransitionListener(new MotionLayout.TransitionListener() {
            @Override
            public void onTransitionStarted(MotionLayout motionLayout, int startId, int endId) {
                isExcutingToolBarAnimation = true;
            }

            @Override
            public void onTransitionChange(MotionLayout motionLayout, int startId, int endId, float progress) {

            }

            @Override
            public void onTransitionCompleted(MotionLayout motionLayout, int currentId) {
                isExcutingToolBarAnimation = false;
            }

            @Override
            public void onTransitionTrigger(MotionLayout motionLayout, int triggerId, boolean positive, float progress) {

            }
        });

        storePickUpPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PickupMenuActivity.class);
                startActivity(intent);
            }
        });
        deliveryPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DeliveryMenuActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }
    private void loadData()
    {
        SharedPreferences prefs =
                getApplicationContext().getSharedPreferences(
                        "recentProducts",
                        MODE_PRIVATE);

        Gson gson = new Gson();

        String json = prefs.getString(
                "recentProducts", null);

        List<String> recentProducts;

        if(json == null)
        {
            recentProducts = new ArrayList<String>();
        }
        else
        {
            Type type = new TypeToken<ArrayList<String>>() {}.getType();
            recentProducts = gson.fromJson(json, type);
        }

        List<Product> products = new ArrayList<Product>();
        for(int i = recentProducts.size() - 1; i >=0; i--)
        {
            for(int j = 0; j < Data.instance.products.size(); j++)
            {
                Product product = Data.instance.products.get(j);
                if(product.getId().equals(recentProducts.get(i)))
                {
                    products.add(product);
                }
            }

        }

        if(recentProducts.size() == 0){
            recentSeeTextView.setAlpha(0);
        } else {
            recentSeeTextView.setAlpha(1);
        }
        ProductAdapter recentProductAdapter = new ProductAdapter(products);
        recentProductRecyclerView.setAdapter(recentProductAdapter);
    }
    @Override
    protected void onStart() {
        super.onStart();

        float dp = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                1,
                getResources().getDisplayMetrics()
        );

        scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                if(Math.abs(scrollY - oldScrollY)> 5)
                {
                    if(scrollY > 10) {
                        if(!isExcutingToolBarAnimation) {
                            toolBarMotionLayout.transitionToEnd();
                        }
                    }
                    else {
                        if(!isExcutingToolBarAnimation) {
                            toolBarMotionLayout.transitionToStart();
                        }
                    }
                    if (scrollY > oldScrollY) {
                        if(!isExcutingCartButtonAnimation1 && !isExcutingCartButtonAnimation2)
                        {
                            addressInfoMotionLayout.transitionToEnd();
                            priceAndAmountMotionLayout.transitionToEnd();
                        }
                    } else {
                        if(!isExcutingCartButtonAnimation1 && !isExcutingCartButtonAnimation2)
                        {
                            addressInfoMotionLayout.transitionToStart();
                            priceAndAmountMotionLayout.transitionToStart();
                        }
                    }
                }
            }
        });
    }
}