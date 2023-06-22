package com.example.coffee_shop_app.models;

public class MLocation {
    private String formattedAddress;
    private double lat;
    private double lng;

    public MLocation(String formattedAddress, double lat, double lng) {
        this.formattedAddress = formattedAddress;
        this.lat = lat;
        this.lng = lng;
    }

    public String getFormattedAddress() {
        return formattedAddress;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }
}
