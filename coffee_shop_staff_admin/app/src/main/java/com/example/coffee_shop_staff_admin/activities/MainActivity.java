package com.example.coffee_shop_staff_admin.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import com.example.coffee_shop_staff_admin.R;
import com.example.coffee_shop_staff_admin.repositories.FoodRepository;
import com.example.coffee_shop_staff_admin.repositories.SizeRepository;
import com.example.coffee_shop_staff_admin.repositories.StoreRepository;
import com.example.coffee_shop_staff_admin.repositories.ToppingRepository;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

public class MainActivity extends AppCompatActivity {
//    private FusedLocationProviderClient fusedLocationClient;
//    private final static int LOCATION_REQUEST_CODE = 261;
//    boolean isPermissionGranted() {
//        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_REQUEST_CODE);
//            return false;
//        }
//        return true;
//    }
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == LOCATION_REQUEST_CODE) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                getCurrentLocation();
//            }
//        }
//    }
//    @SuppressLint("MissingPermission")
//    private void getCurrentLocation() {
//        if (isPermissionGranted()) {
//            fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
//                @Override
//                public void onSuccess(Location location) {
//                    if (location != null) {
//                        LocationHelper.getInstance().setCurrentLocation(
//                                new LatLng(location.getLatitude(), location.getLongitude())
//                        );
//                    }
//                }
//            });
//        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StoreRepository.getInstance().registerSnapshotListener();
        FoodRepository.getInstance().registerSnapshotListener();
        ToppingRepository.getInstance().registerSnapshotListener();
        SizeRepository.getInstance().registerSnapshotListener();
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//        if (isPermissionGranted()) {
//            getCurrentLocation();
//        } else {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
//        }
    }
}