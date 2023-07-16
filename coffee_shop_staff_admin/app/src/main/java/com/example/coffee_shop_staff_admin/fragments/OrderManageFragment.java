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
import com.example.coffee_shop_staff_admin.adapters.OrderViewPagerAdapter;
import com.example.coffee_shop_staff_admin.databinding.FragmentOrderManageBinding;
import com.example.coffee_shop_staff_admin.viewmodels.OrderOfStoreViewModel;
import com.google.android.material.tabs.TabLayout;

public class OrderManageFragment extends Fragment {

    FragmentOrderManageBinding fragmentOrderManageBinding;
    OrderOfStoreViewModel orderOfStoreViewModel;
    OrderViewPagerAdapter adapter;
    public OrderManageFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentOrderManageBinding = fragmentOrderManageBinding.inflate(inflater, container, false);
        orderOfStoreViewModel=OrderOfStoreViewModel.getInstance();
        fragmentOrderManageBinding.setViewModel(orderOfStoreViewModel);
        setToolBarTitle("Đơn hàng");
        // Inflate the layout for this fragment
        return fragmentOrderManageBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentManager fragmentManager=getChildFragmentManager();

        fragmentOrderManageBinding.tabLayoutOrder
                .addTab(fragmentOrderManageBinding
                        .tabLayoutOrder
                        .newTab()
                        .setText("Mang đi"));
        fragmentOrderManageBinding
                .tabLayoutOrder
                .addTab(fragmentOrderManageBinding
                        .tabLayoutOrder
                        .newTab()
                        .setText("Giao hàng"));

        adapter=new OrderViewPagerAdapter(fragmentManager, getLifecycle(), false);
        fragmentOrderManageBinding.vpOrderManage.setAdapter(adapter);
        fragmentOrderManageBinding.tabLayoutOrder
                .addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        fragmentOrderManageBinding.vpOrderManage.setCurrentItem(tab.getPosition());
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {

                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {

                    }
                });

        fragmentOrderManageBinding.vpOrderManage.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                fragmentOrderManageBinding.tabLayoutOrder.getTabAt(position).select();
            }
        });
    }

    public void setToolBarTitle(String title)
    {
        Toolbar toolbar = ((AppCompatActivity)requireActivity()).findViewById(R.id.my_toolbar);
        toolbar.setTitle(title);
    }
}