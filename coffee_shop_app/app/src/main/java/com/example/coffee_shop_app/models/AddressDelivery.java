package com.example.coffee_shop_app.models;

import androidx.lifecycle.MutableLiveData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddressDelivery {
    private  MLocation address;
    private String nameReceiver;
    private String phone;
    private String addressNote;

    public AddressDelivery(MLocation address, String nameReceiver, String phone, String addressNote) {
        this.address = address;
        this.nameReceiver = nameReceiver;
        this.phone = phone;
        this.addressNote = addressNote;
    }

    public MLocation getAddress() {
        return address;
    }

    public String getNameReceiver() {
        return nameReceiver;
    }

    public String getPhone() {
        return phone;
    }
    public String getAddressNote(){return addressNote;}

    public static AddressDelivery fromFireBase(Map<String, Object> map)
    {
        String formattedAddress = map.get("formattedAddress").toString();
        double lat = (double)map.get("lat");
        double lng = (double)map.get("lng");
        MLocation location = new MLocation(formattedAddress, lat, lng);
        String nameReceiver =  map.get("nameReceiver").toString();
        String phone = map.get("phone").toString();
        String addressNote  = map.get("addressNote").toString();
        return new AddressDelivery(location, nameReceiver, phone, addressNote);
    }
    public static Map<String, Object> toFireBase(AddressDelivery addressDelivery)
    {
        String formattedAddress = addressDelivery.getAddress().getFormattedAddress();
        double lat = addressDelivery.getAddress().getLat();
        double lng = addressDelivery.getAddress().getLng();
        String nameReceiver =  addressDelivery.getNameReceiver();
        String phone = addressDelivery.getPhone();
        String addressNote = addressDelivery.getAddressNote();
        Map<String, Object> addressMap = new HashMap<>();
        addressMap.put("formattedAddress", formattedAddress);
        addressMap.put("lat", lat);
        addressMap.put("lng", lng);
        addressMap.put("nameReceiver", nameReceiver);
        addressMap.put("phone", phone);
        addressMap.put("addressNote", addressNote);
        return addressMap;
    }
}
