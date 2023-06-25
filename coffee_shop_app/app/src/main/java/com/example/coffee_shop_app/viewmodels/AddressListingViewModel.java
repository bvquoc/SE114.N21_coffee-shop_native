package com.example.coffee_shop_app.viewmodels;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.lifecycle.MutableLiveData;

import com.example.coffee_shop_app.models.AddressDelivery;
import com.example.coffee_shop_app.repository.AddressRepository;

import java.util.List;

public class AddressListingViewModel extends BaseObservable {
    public AddressListingViewModel(){
        addressListMutableLiveData = AddressRepository.getInstance().getAddressListMutableLiveData();
    }
    @Bindable
    private MutableLiveData<List<AddressDelivery>> addressListMutableLiveData;

    public MutableLiveData<List<AddressDelivery>> getAddressListMutableLiveData() {
        return addressListMutableLiveData;
    }
}
