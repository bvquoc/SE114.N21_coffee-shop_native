package com.example.coffee_shop_app.repository;

import com.example.coffee_shop_app.utils.HttpClient;

import okhttp3.Callback;

public class LocationAPI {
    private static final String MAP_BOX_API_KEY = "pk.eyJ1IjoiemVyb3JlaSIsImEiOiJjbGlidXpyYTQwOXNmM2Zxb3BpdWQwaDFqIn0.xrYRn0Fyr85ddyR5DoHXEw";
    public static void getLocationResponse(String text, Callback callback) {
        String uri = "https://api.mapbox.com/geocoding/v5/mapbox.places/" +
                text +
                ".json?autocomplete=true&country=VN&language=vi&access_token=" +
                MAP_BOX_API_KEY;
        HttpClient.sendGetRequest(uri, callback);
    }
}
