package com.example.coffee_shop_staff_admin.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentContainerView;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavInflater;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;

import com.example.coffee_shop_staff_admin.R;
import com.example.coffee_shop_staff_admin.repositories.AuthRepository;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class StoreManageActivity extends AppCompatActivity {
    BottomNavigationView bottomNavView;
    FragmentContainerView fragmentContainerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_manage);
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        if(!AuthRepository.getInstance().getCurrentUser().isAdmin()){
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        bottomNavView= (BottomNavigationView) findViewById(R.id.bottomNavView);
        fragmentContainerView= (FragmentContainerView) findViewById(R.id.fragmentContainerView);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragmentContainerView);
        NavController navController = navHostFragment.getNavController();

        if(AuthRepository.getInstance().getCurrentUser().isAdmin()){
            bottomNavView.getMenu().clear(); //clear old inflated items.
            bottomNavView.inflateMenu(R.menu.admin_store_nav_menu);
            NavInflater inflater = navHostFragment.getNavController().getNavInflater();
            NavGraph graph = inflater.inflate(R.navigation.admin_store_nav);
            navHostFragment.getNavController().setGraph(graph);
        }

        NavigationUI.setupWithNavController(bottomNavView, navController);
    }
}