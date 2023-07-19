package com.example.coffee_shop_staff_admin.activities;

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

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.coffee_shop_staff_admin.R;
import com.example.coffee_shop_staff_admin.adapters.SuggestLocationAdapter;
import com.example.coffee_shop_staff_admin.databinding.ActivityMapsBinding;
import com.example.coffee_shop_staff_admin.models.MLocation;
import com.example.coffee_shop_staff_admin.utils.interfaces.OnSuggestLocationClickListener;
import com.example.coffee_shop_staff_admin.utils.keyboard.KeyboardHelper;
import com.example.coffee_shop_staff_admin.utils.keyboard.OnKeyboardVisibilityListener;
import com.example.coffee_shop_staff_admin.viewmodels.MapViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private ActivityMapsBinding activityMapsBinding;
    private SuggestLocationAdapter suggestLocationAdapter = new SuggestLocationAdapter(new ArrayList<MLocation>());
    MapViewModel mapViewModel = new MapViewModel();
    private Handler handler = new Handler();
    private Runnable runnable;
    AsyncTask<Void, Void, Void> searchingByLocationTask;
    private OnSuggestLocationClickListener onSuggestLocationClickListener = new OnSuggestLocationClickListener() {
        @Override
        public void onSuggestLocationClick(MLocation location) {
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

        activityMapsBinding.buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
    void init()
    {
        activityMapsBinding.suggesstLocationRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        suggestLocationAdapter.setOnSuggestLocationTouchListener(onSuggestLocationClickListener);
        activityMapsBinding.suggesstLocationRecyclerView.setAdapter(suggestLocationAdapter);

        mapViewModel.getSelectedLatLng().observe(this, new Observer<LatLng>() {
            @Override
            public void onChanged(LatLng latLng) {
                searchByLocation(latLng);
            }
        });
        mapViewModel.getSuggestLocationList().observe(this, suggessLocation->{
            suggestLocationAdapter.changeDataSet(suggessLocation);
        });
        activityMapsBinding.setViewModel(mapViewModel);

        KeyboardHelper.setKeyboardVisibilityListener(this, new OnKeyboardVisibilityListener() {
            @Override
            public void onVisibilityChanged(boolean visible) {
                if(visible){
                    mapViewModel.setKeyBoardShow(true);
                }
                else {
                    mapViewModel.setKeyBoardShow(false);
                }
            }
        });
        activityMapsBinding.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });

        activityMapsBinding.editTextFrame.setEndIconOnClickListener(v -> {
            activityMapsBinding.editText.setText("");
        });
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(false);

        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                mapViewModel.setIsLoading(true);
                if(runnable!=null)
                {
                    handler.removeCallbacks(runnable);
                }
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        LatLng currentLatLng = mMap.getCameraPosition().target;
                        mapViewModel.getSelectedLatLng().postValue(currentLatLng);
                    }
                };
                handler.postDelayed(runnable, 500);
            }
        });

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