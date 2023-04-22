package com.example.coffee_shop_app.models;

import androidx.annotation.NonNull;

public class Address {
    private String subAddress;
    private String city;
    private String ward;
    private String district;

    public Address(String subAddress, String city, String ward, String district) {
        this.subAddress = subAddress;
        this.city = city;
        this.ward = ward;
        this.district = district;
    }

    @NonNull
    @Override
    public String toString() {
        return subAddress + ", " + district + ", " + ward + ", " + city;
    }

    public String getSubAddress() {
        return subAddress;
    }

    public String getCity() {
        return city;
    }

    public String getWard() {
        return ward;
    }

    public String getDistrict() {
        return district;
    }
}
