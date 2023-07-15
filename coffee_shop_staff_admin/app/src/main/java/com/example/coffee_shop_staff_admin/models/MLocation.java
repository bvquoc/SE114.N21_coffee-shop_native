package com.example.coffee_shop_staff_admin.models;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

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
    public static MLocation fromJson(JsonObject jsonObject)
    {
        String formattedAddress = jsonObject.get("place_name_vi").getAsString();
        JsonArray coordinatesJsonArray = jsonObject.get("geometry").getAsJsonObject().get("coordinates").getAsJsonArray();
        double lat = coordinatesJsonArray.get(1).getAsDouble();
        double lng = coordinatesJsonArray.get(0).getAsDouble();
        return new MLocation(formattedAddress, lat, lng);
    }
}
