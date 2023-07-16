package com.example.coffee_shop_app.activities.order;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.View;

import com.example.coffee_shop_app.R;
import com.example.coffee_shop_app.adapters.OrderViewPagerAdapter;
import com.example.coffee_shop_app.databinding.ActivityOrderHistoryBinding;
import com.example.coffee_shop_app.viewmodels.OrderViewModel;
import com.google.android.material.tabs.TabLayout;

public class OrderHistoryActivity extends AppCompatActivity {

    ActivityOrderHistoryBinding activityOrderHistoryBinding;
    OrderViewPagerAdapter adapter;
    OrderViewModel orderViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityOrderHistoryBinding = DataBindingUtil.setContentView(this, R.layout.activity_order_history);
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        toolbar.setTitle("Lịch sử mua hàng");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        orderViewModel=OrderViewModel.getInstance();
        activityOrderHistoryBinding.setViewModel(orderViewModel);
        init();
    }

    private void init(){
        FragmentManager fragmentManager=getSupportFragmentManager();

        activityOrderHistoryBinding.tabLayoutOrder
                .addTab(activityOrderHistoryBinding
                        .tabLayoutOrder
                        .newTab()
                        .setText("Mang đi"));
        activityOrderHistoryBinding
                .tabLayoutOrder
                .addTab(activityOrderHistoryBinding
                        .tabLayoutOrder
                        .newTab()
                        .setText("Giao hàng"));

        adapter=new OrderViewPagerAdapter(fragmentManager, getLifecycle(), true);
        activityOrderHistoryBinding.vpOrderManage.setAdapter(adapter);
        activityOrderHistoryBinding.tabLayoutOrder
                .addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        activityOrderHistoryBinding.vpOrderManage.setCurrentItem(tab.getPosition());
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {

                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {

                    }
                });

        activityOrderHistoryBinding.vpOrderManage.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                activityOrderHistoryBinding.tabLayoutOrder.getTabAt(position).select();
            }
        });

    }
}