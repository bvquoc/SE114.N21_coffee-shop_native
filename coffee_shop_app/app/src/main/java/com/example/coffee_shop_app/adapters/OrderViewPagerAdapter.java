package com.example.coffee_shop_app.adapters;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.coffee_shop_app.fragments.OrderDeliveryListFragment;
import com.example.coffee_shop_app.fragments.OrderPickupListFragment;
import com.example.coffee_shop_app.viewmodels.OrderViewModel;

public class OrderViewPagerAdapter extends FragmentStateAdapter {

    private OrderViewModel orderViewModel;
    private boolean isHistory;

    public OrderViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, boolean isHistory) {
        super(fragmentManager, lifecycle);
        this.isHistory=isHistory;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if(position==0){
            return OrderPickupListFragment.newInstance(isHistory);
        } else{
            return OrderDeliveryListFragment.newInstance(isHistory);
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
