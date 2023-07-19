package com.example.coffee_shop_staff_admin.fragments.product;

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
import com.example.coffee_shop_staff_admin.adapters.product.ProductViewPagerAdapter;
import com.example.coffee_shop_staff_admin.databinding.FragmentProductManageBinding;
import com.example.coffee_shop_staff_admin.viewmodels.product.ProductOfStoreViewModel;
import com.google.android.material.tabs.TabLayout;

public class ProductManageFragment extends Fragment {

    FragmentProductManageBinding fragmentProductManageBinding;
    ProductOfStoreViewModel productOfStoreViewModel;
    ProductViewPagerAdapter adapter;

    public ProductManageFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentProductManageBinding = fragmentProductManageBinding.inflate(inflater, container, false);
        productOfStoreViewModel = ProductOfStoreViewModel.getInstance();
        fragmentProductManageBinding.setViewModel(productOfStoreViewModel);
        setToolBarTitle("Sản phẩm");
        // Inflate the layout for this fragment
        return fragmentProductManageBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentManager fragmentManager = getChildFragmentManager();

        fragmentProductManageBinding.tabLayoutProduct
                .addTab(fragmentProductManageBinding
                        .tabLayoutProduct
                        .newTab()
                        .setText("Đồ uống"));

        fragmentProductManageBinding
                .tabLayoutProduct
                .addTab(fragmentProductManageBinding
                        .tabLayoutProduct
                        .newTab()
                        .setText("Topping"));

        adapter = new ProductViewPagerAdapter(fragmentManager, getLifecycle(), false);

        fragmentProductManageBinding.vpProductManage.setAdapter(adapter);

        fragmentProductManageBinding.tabLayoutProduct
                .addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        fragmentProductManageBinding.vpProductManage.setCurrentItem(tab.getPosition());
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {

                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {

                    }
                });

        fragmentProductManageBinding.vpProductManage.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                fragmentProductManageBinding.tabLayoutProduct.getTabAt(position).select();
            }
        });
    }

    public void setToolBarTitle(String title) {
        Toolbar toolbar = ((AppCompatActivity) requireActivity()).findViewById(R.id.my_toolbar);
        toolbar.setTitle(title);
    }
}