package com.example.coffee_shop_app.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.coffee_shop_app.R;
import com.example.coffee_shop_app.activities.order.OrderHistoryActivity;
import com.example.coffee_shop_app.adapters.OrderViewPagerAdapter;
import com.example.coffee_shop_app.databinding.FragmentMenuBinding;
import com.example.coffee_shop_app.databinding.FragmentOrderBinding;
import com.example.coffee_shop_app.viewmodels.OrderViewModel;
import com.google.android.material.tabs.TabLayout;

public class OrderFragment extends Fragment {
    FragmentOrderBinding fragmentOrderBinding;

    OrderViewPagerAdapter adapter;
    OrderViewModel orderViewModel;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentOrderBinding = FragmentOrderBinding.inflate(inflater, container, false);
        orderViewModel=OrderViewModel.getInstance();
        fragmentOrderBinding.setViewModel(orderViewModel);
        setToolBarTitle("Đơn hàng");
        // Inflate the layout for this fragment
        return fragmentOrderBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentManager fragmentManager=getChildFragmentManager();

        fragmentOrderBinding.tabLayoutOrder
                .addTab(fragmentOrderBinding
                        .tabLayoutOrder
                        .newTab()
                        .setText("Mang đi"));
        fragmentOrderBinding
                .tabLayoutOrder
                .addTab(fragmentOrderBinding
                        .tabLayoutOrder
                        .newTab()
                        .setText("Giao hàng"));

        adapter=new OrderViewPagerAdapter(fragmentManager, getLifecycle(), false);
        fragmentOrderBinding.vpOrderManage.setAdapter(adapter);
        fragmentOrderBinding.tabLayoutOrder
                .addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                fragmentOrderBinding.vpOrderManage.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        fragmentOrderBinding.vpOrderManage.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                fragmentOrderBinding.tabLayoutOrder.getTabAt(position).select();
            }
        });

        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.order_history_menu, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if(menuItem.getItemId()==R.id.showHistory){
                    Intent intent=new Intent(getContext(), OrderHistoryActivity.class);
                    getActivity().startActivity(intent);
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }
    public void setToolBarTitle(String title)
    {
        Toolbar toolbar = ((AppCompatActivity)requireActivity()).findViewById(R.id.my_toolbar);
        toolbar.setTitle(title);
    }
}