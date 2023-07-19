package com.example.coffee_shop_staff_admin.adapters.product;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.coffee_shop_staff_admin.fragments.OrderDeliveryListFragment;
import com.example.coffee_shop_staff_admin.fragments.OrderPickupListFragment;
import com.example.coffee_shop_staff_admin.fragments.product.DrinkListFragment;
import com.example.coffee_shop_staff_admin.fragments.product.ToppingListFragment;

public class ProductViewPagerAdapter extends FragmentStateAdapter {
    public ProductViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, boolean isHistory) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if(position==0){
            return new DrinkListFragment();
        } else{
            return new ToppingListFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
