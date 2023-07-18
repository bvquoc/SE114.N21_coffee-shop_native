package com.example.coffee_shop_app.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;

import com.example.coffee_shop_app.Data;
import com.example.coffee_shop_app.R;
import com.example.coffee_shop_app.models.CartFood;
import com.example.coffee_shop_app.models.Product;
import com.example.coffee_shop_app.repository.AuthRepository;
import com.example.coffee_shop_app.repository.ProductRepository;
import com.example.coffee_shop_app.repository.StoreRepository;
import com.example.coffee_shop_app.utils.LocationHelper;
import com.example.coffee_shop_app.utils.SqliteHelper;
import com.example.coffee_shop_app.viewmodels.CartViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class MainPageActivity extends AppCompatActivity {
    BottomNavigationView bottomNavView;
    private FusedLocationProviderClient fusedLocationClient;
    private final static int LOCATION_REQUEST_CODE = 261;
    boolean isPermissionGranted() {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_REQUEST_CODE);
            return false;
        }
        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            }
        }
    }
    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        if (isPermissionGranted()) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        LocationHelper.getInstance().setCurrentLocation(
                                new LatLng(location.getLatitude(), location.getLongitude())
                        );
                    }
                }
            });
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (isPermissionGranted()) {
            getCurrentLocation();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        }

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        bottomNavView= (BottomNavigationView) findViewById(R.id.bottomNavView);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragmentContainerView);
        NavController navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(bottomNavView, navController);

        StoreRepository.getInstance().registerSnapshotListener();

        SqliteHelper repo=new SqliteHelper(MainPageActivity.this);
        ProductRepository.getInstance().getProductListMutableLiveData().observe(MainPageActivity.this, new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> products) {
                ArrayList<CartFood> items= repo.getCartFood(AuthRepository.getInstance().getCurrentUser().getId());
                CartViewModel.getInstance().getCartFoods().setValue(items);
            }
        });

    }
}