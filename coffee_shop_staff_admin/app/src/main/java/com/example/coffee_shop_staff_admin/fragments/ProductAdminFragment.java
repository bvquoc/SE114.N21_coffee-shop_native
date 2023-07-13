package com.example.coffee_shop_staff_admin.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.coffee_shop_staff_admin.R;
import com.example.coffee_shop_staff_admin.adapters.ProductAdminViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;
public class ProductAdminFragment extends Fragment {
    private TabLayout productsTab;
    private ViewPager2 productViewPager;
    private ProductAdminViewPagerAdapter adapter;

    public ProductAdminFragment() {}
    public static ProductAdminFragment newInstance() {
        ProductAdminFragment fragment = new ProductAdminFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product_admin, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar toolbar = ((AppCompatActivity)requireActivity()).findViewById(R.id.my_toolbar);
        toolbar.setTitle("Sản phẩm");

        productsTab= (TabLayout)view.findViewById(R.id.tabLayoutProduct);
        productViewPager= (ViewPager2) view.findViewById(R.id.vpProductManage);
        FragmentManager fragmentManager=getChildFragmentManager();

        productsTab.addTab(productsTab.newTab().setText("Đồ uống"));
        productsTab.addTab(productsTab.newTab().setText("Topping"));
        productsTab.addTab(productsTab.newTab().setText("Size"));


        adapter = new ProductAdminViewPagerAdapter(fragmentManager, getLifecycle());
        productViewPager.setAdapter(adapter);
        productsTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                productViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        productViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                productsTab.getTabAt(position).select();
            }
        });
    }
}