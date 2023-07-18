package com.example.coffee_shop_staff_admin.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.example.coffee_shop_staff_admin.fragments.OrderDeliveryListFragment;
import com.example.coffee_shop_staff_admin.fragments.OrderPickupListFragment;

public class OrderViewPagerAdapter extends FragmentStateAdapter {

//    private OrderViewModel orderViewModel;
//    private boolean isHistory;

    public OrderViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, boolean isHistory) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if(position==0){
            return new OrderPickupListFragment();
//            return OrderPickupListFragment.newInstance(isHistory);
        } else{
            return new OrderDeliveryListFragment();
//            return OrderDeliveryListFragment.newInstance(isHistory);
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
