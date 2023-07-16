package com.example.coffee_shop_app.activities.address;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.coffee_shop_app.R;
import com.example.coffee_shop_app.adapters.SuggestLocationAdapter;
import com.example.coffee_shop_app.databinding.ActivityMapsBinding;
import com.example.coffee_shop_app.utils.LocationHelper;
import com.example.coffee_shop_app.utils.interfaces.OnSuggestLocationClickListener;
import com.example.coffee_shop_app.viewmodels.MapViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Objects;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private ActivityMapsBinding activityMapsBinding;
    private final SuggestLocationAdapter suggestLocationAdapter = new SuggestLocationAdapter(new ArrayList<>());
    MapViewModel mapViewModel = new MapViewModel();
    private final Handler handler = new Handler();
    private Runnable runnable;
    AsyncTask<Void, Void, Void> searchingByLocationTask;
    private final OnSuggestLocationClickListener onSuggestLocationClickListener = location -> {
        mapViewModel.setSearchText("");
        animateCamera(new LatLng(location.getLat(), location.getLng()));

        // Get the input method manager
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        // Check if the keyboard is currently open
        View view = getCurrentFocus();
        if (view != null) {
            // Close the keyboard
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    };
    private void animateCamera(LatLng latLng){
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        mapViewModel.getSelectedLatLng().postValue(latLng);
    }
    private LatLng latLng = new LatLng(10.870085288414241, 106.80308100344831);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMapsBinding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(activityMapsBinding.getRoot());

        activityMapsBinding.buttonBack.setOnClickListener(v -> onBackPressed());
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if(mapFragment!=null)
        {
            mapFragment.getMapAsync(this);
        }
    }
    void init()
    {
        activityMapsBinding.suggestLocationRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        suggestLocationAdapter.setOnSuggestLocationTouchListener(onSuggestLocationClickListener);
        activityMapsBinding.suggestLocationRecyclerView.setAdapter(suggestLocationAdapter);

        mapViewModel.getSelectedLatLng().observe(this, this::searchByLocation);
        mapViewModel.getSuggestLocationList().observe(this, suggestLocationAdapter::changeDataSet);
        activityMapsBinding.setViewModel(mapViewModel);

        activityMapsBinding.saveButton.setOnClickListener(v -> {
            LatLng latLng = mapViewModel.getSelectedLatLng().getValue();
            if(latLng == null)
            {
                Toast.makeText(
                        getApplicationContext(),
                        "Vị trí không hợp lệ",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }
            Intent intent = new Intent();
            intent.putExtra("formattedAddress", mapViewModel.getFormattedAddress());
            intent.putExtra("lat", latLng.latitude);
            intent.putExtra("lng", latLng.longitude);
            setResult(RESULT_OK, intent);
            finish();
        });
    }
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = Objects.requireNonNull(googleMap);
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(false);

        mMap.setOnCameraMoveListener(() -> {
            mapViewModel.setIsLoading(true);
            if(runnable!=null)
            {
                handler.removeCallbacks(runnable);
            }
            runnable = () -> {
                LatLng currentLatLng = mMap.getCameraPosition().target;
                mapViewModel.getSelectedLatLng().postValue(currentLatLng);
            };
            handler.postDelayed(runnable, 500);
        });

        if(LocationHelper.getInstance().getCurrentLocation() != null)
        {
            latLng = LocationHelper.getInstance().getCurrentLocation();
        }

        Intent intent = getIntent();
        if(intent.getDoubleExtra("lat", -1) != -1)
        {
            double lat = intent.getDoubleExtra("lat", 0);
            double lng = intent.getDoubleExtra("lng", 0);
            latLng = new LatLng(lat, lng);
        }

        init();

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mapViewModel.getSelectedLatLng().postValue(latLng);
    }
    private void searchByLocation(LatLng latLng)
    {
        if(searchingByLocationTask != null)
        {
            searchingByLocationTask.cancel(true);
        }
        searchingByLocationTask = new SearchAddressByLocationTask(latLng);
        searchingByLocationTask.execute();
    }
    private final class SearchAddressByLocationTask extends AsyncTask<Void, Void, Void> {
        LatLng latLng;
        public SearchAddressByLocationTask(LatLng latLng)
        {
            this.latLng = latLng;
        }
        @Override
        protected void onPreExecute() {
            mapViewModel.setIsLoading(true);
        }
        @Override
        protected Void doInBackground(Void... params) {
            Geocoder geocoder = new Geocoder(MapsActivity.this);
            try {
                ArrayList<Address> addresses = (ArrayList<Address>) geocoder.getFromLocation(
                        latLng.latitude,
                        latLng.longitude,
                        1
                );
                Address address = addresses.get(0);
                String formattedAddress = address.getAddressLine(0);
                mapViewModel.setFormattedAddress(formattedAddress);
                mapViewModel.setIsExistLocation(true);
            } catch (Exception e) {
                mapViewModel.setFormattedAddress("Không thể tìm thấy địa chỉ");
                mapViewModel.setIsExistLocation(false);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            mapViewModel.setIsLoading(false);
        }
    }
}