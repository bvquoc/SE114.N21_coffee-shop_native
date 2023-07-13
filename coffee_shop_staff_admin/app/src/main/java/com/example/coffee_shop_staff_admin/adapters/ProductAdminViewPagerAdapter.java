package com.example.coffee_shop_staff_admin.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.coffee_shop_staff_admin.fragments.FoodAdminFragment;
import com.example.coffee_shop_staff_admin.fragments.SizeAdminFragment;
import com.example.coffee_shop_staff_admin.fragments.ToppingAdminFragment;

public class ProductAdminViewPagerAdapter extends FragmentStateAdapter {
    public ProductAdminViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if(position==0){
            return new FoodAdminFragment();
        } else if(position == 1){
            return  new ToppingAdminFragment();
        }
        else
        {
            return new SizeAdminFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
