package com.example.coffee_shop_app.repository;

import com.example.coffee_shop_app.utils.HttpClient;
import com.squareup.okhttp.Callback;

import androidx.lifecycle.MutableLiveData;

import com.example.coffee_shop_app.models.MLocation;
import com.example.coffee_shop_app.utils.HttpClient;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;

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
