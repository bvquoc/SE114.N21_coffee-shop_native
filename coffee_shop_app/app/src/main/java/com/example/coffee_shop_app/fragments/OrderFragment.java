package com.example.coffee_shop_app.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.coffee_shop_app.R;
import com.example.coffee_shop_app.adapters.OrderViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;

public class OrderFragment extends Fragment {

    TabLayout orderTab;
    ViewPager2 orderViewPager;
    OrderViewPagerAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        orderTab= (TabLayout)view.findViewById(R.id.tabLayoutOrder);
        orderViewPager= (ViewPager2) view.findViewById(R.id.vpOrderManage);
        FragmentManager fragmentManager=getChildFragmentManager();

        orderTab.addTab(orderTab.newTab().setText("Store pickup"));
        orderTab.addTab(orderTab.newTab().setText("Delivery"));

        adapter=new OrderViewPagerAdapter(fragmentManager, getLifecycle());
        orderViewPager.setAdapter(adapter);
        orderTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                orderViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        orderViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                orderTab.getTabAt(position).select();
            }
        });
    }
}