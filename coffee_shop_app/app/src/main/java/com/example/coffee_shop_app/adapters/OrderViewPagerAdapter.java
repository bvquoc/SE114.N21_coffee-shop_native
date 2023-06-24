package com.example.coffee_shop_app.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.coffee_shop_app.fragments.OrderDeliveryListFragment;
import com.example.coffee_shop_app.fragments.OrderPickupListFragment;

public class OrderViewPagerAdapter extends FragmentStateAdapter {


    public OrderViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if(position==0){
            return new OrderPickupListFragment();
        } else{
            return  new OrderDeliveryListFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
