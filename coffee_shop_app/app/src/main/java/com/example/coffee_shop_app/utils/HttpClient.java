package com.example.coffee_shop_app.utils;


import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class HttpClient {

    public static void sendGetRequest(String url, Callback callback) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(callback);
    }
}