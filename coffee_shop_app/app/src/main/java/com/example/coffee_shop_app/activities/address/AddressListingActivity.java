package com.example.coffee_shop_app.activities.address;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.example.coffee_shop_app.R;
import com.example.coffee_shop_app.databinding.ActivityAddressListingBinding;
import com.example.coffee_shop_app.models.AddressDelivery;
import com.example.coffee_shop_app.models.MLocation;
import com.example.coffee_shop_app.repository.AddressRepository;
import com.example.coffee_shop_app.repository.AuthRepository;
import com.example.coffee_shop_app.utils.interfaces.OnAddressClickListener;
import com.example.coffee_shop_app.utils.interfaces.OnEditAddressClickListener;
import com.example.coffee_shop_app.utils.styles.RecyclerViewGapDecoration;
import com.example.coffee_shop_app.adapters.AddressListingItemAdapter;
import com.example.coffee_shop_app.viewmodels.AddressListingViewModel;
import com.example.coffee_shop_app.viewmodels.CartButtonViewModel;

import java.util.ArrayList;

public class AddressListingActivity extends AppCompatActivity {
    private ActivityAddressListingBinding activityAddressListingBinding;
    private final AddressListingItemAdapter addressListingItemAdapter = new AddressListingItemAdapter(new ArrayList<>());
    private final OnEditAddressClickListener editAddressTouchListener = new OnEditAddressClickListener() {
        @Override
        public void onEditAddressClick(int index, AddressDelivery addressDelivery) {
            Intent intent = new Intent(getApplicationContext(), EditDeliveryAddressActivity.class);
            intent.putExtra("index", index);
            intent.putExtra("formattedAddress", addressDelivery.getAddress().getFormattedAddress());
            intent.putExtra("lat", addressDelivery.getAddress().getLat());
            intent.putExtra("lng", addressDelivery.getAddress().getLng());
            intent.putExtra("addressNote", addressDelivery.getAddressNote());
            intent.putExtra("nameReceiver", addressDelivery.getNameReceiver());
            intent.putExtra("phone", addressDelivery.getPhone());
            activityEditAddressResultLauncher.launch(intent);
        }
    };
    private final OnAddressClickListener addressTouchListener = addressDelivery -> {
        CartButtonViewModel.getInstance().getSelectedAddressDelivery().postValue(addressDelivery);
        finish();
    };
    private final ActivityResultLauncher<Intent> activityEditAddressResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent intent = result.getData();
                    if (intent != null) {
                        EditDeliveryAddressActivity.EditAddressResult resultType = (EditDeliveryAddressActivity.EditAddressResult)intent.getSerializableExtra("orderType");
                        int index = intent.getIntExtra("index", -1);
                        if(resultType == EditDeliveryAddressActivity.EditAddressResult.Delete)
                        {
                            addressListingItemAdapter.delete(index);
                        }
                        else
                        {
                            String formattedAddress = intent.getStringExtra("formattedAddress");
                            double lat = intent.getDoubleExtra("lat", 0);
                            double lng = intent.getDoubleExtra("lng", 0);
                            String addressNote = intent.getStringExtra("addressNote");
                            String nameReceiver = intent.getStringExtra("nameReceiver");
                            String phone = intent.getStringExtra("phone");
                            AddressDelivery addressDelivery = new AddressDelivery(
                                    new MLocation(formattedAddress, lat, lng),
                                    nameReceiver,
                                    phone,
                                    addressNote
                            );
                            if(resultType == EditDeliveryAddressActivity.EditAddressResult.Insert)
                            {
                                addressListingItemAdapter.insert(addressDelivery);
                            }
                            else if(index != -1)
                            {
                                addressListingItemAdapter.update(index, addressDelivery);
                            }
                        }
                    }
                }
            }
    );
    private final ActivityResultLauncher<Intent> activityGoogleMapResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent intent = result.getData();
                    if (intent != null) {
                        String formattedAddress = intent.getStringExtra("formattedAddress");
                        double lat = intent.getDoubleExtra("lat", 0);
                        double lng = intent.getDoubleExtra("lng",  0);
                        MLocation mLocation = new MLocation(formattedAddress, lat, lng);
                        String nameReceiver = "Name";
                        String phone = "Phone";
                        if(AuthRepository.getInstance().getCurrentUser()!=null)
                        {
                            nameReceiver = AuthRepository.getInstance().getCurrentUser().getName();
                            phone = AuthRepository.getInstance().getCurrentUser().getPhoneNumber();
                        }
                        AddressDelivery addressDelivery = new AddressDelivery(
                                mLocation,
                                nameReceiver,
                                phone,
                                "");
                        CartButtonViewModel.getInstance().getSelectedAddressDelivery().postValue(addressDelivery);
                        finish();
                    }
                }
            }
    );
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityAddressListingBinding = DataBindingUtil.setContentView(this, R.layout.activity_address_listing);

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        toolbar.setTitle("Giao hàng đến");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        init();
    }
    private void init()
    {
        addressListingItemAdapter.setOnEditAddressTouchListener(editAddressTouchListener);
        addressListingItemAdapter.setOnAddressTouchListener(addressTouchListener);
        activityAddressListingBinding.addressListingRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        int distance = (int)getResources().getDimension(com.intuit.sdp.R.dimen._12sdp);
        activityAddressListingBinding.addressListingRecyclerview.addItemDecoration(new RecyclerViewGapDecoration(distance));
        activityAddressListingBinding.addressListingRecyclerview.setAdapter(addressListingItemAdapter);

        activityAddressListingBinding.refreshLayout.setOnRefreshListener(() -> {
            AddressRepository.getInstance().registerSnapshotListener();
            activityAddressListingBinding.refreshLayout.setRefreshing(false);
        });

        AddressListingViewModel addressListingViewModel = new AddressListingViewModel();
        AddressRepository.getInstance().getAddressListMutableLiveData().observe(this, addressDeliveries ->
        {
            addressListingViewModel.setLoading(true);
            if(addressDeliveries!=null)
            {
                addressListingViewModel.setLoading(false);
                addressListingItemAdapter.changeDataSet(addressDeliveries);
            }
            else
            {
                addressListingItemAdapter.changeDataSet(new ArrayList<>());
            }
        });
        activityAddressListingBinding.setViewModel(addressListingViewModel);
        activityAddressListingBinding.googleMapButton.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
            activityGoogleMapResultLauncher.launch(intent);
        });
        activityAddressListingBinding.newAddressButton.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), EditDeliveryAddressActivity.class);
            intent.putExtra("index", -1);
            activityEditAddressResultLauncher.launch(intent);
        });
    }
}