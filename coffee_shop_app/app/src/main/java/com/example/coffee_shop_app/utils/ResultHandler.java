package com.example.coffee_shop_app.utils;

import android.content.Intent;
import android.util.SparseArray;

public class ResultHandler {
    private static ResultHandler instance;
    private SparseArray<Navigator.ResultCallback> callbackMap;

    private ResultHandler() {
        callbackMap = new SparseArray<>();
    }

    public static ResultHandler getInstance() {
        if (instance == null) {
            instance = new ResultHandler();
        }
        return instance;
    }

    public void registerCallback(int requestCode, Navigator.ResultCallback callback) {
        callbackMap.put(requestCode, callback);
    }

    public void unregisterCallback(int requestCode) {
        callbackMap.remove(requestCode);
    }

    public void handleResult(int requestCode, int resultCode, Intent data) {
        Navigator.ResultCallback callback = callbackMap.get(requestCode);
        if (callback != null) {
            callback.onResult(data);
            unregisterCallback(requestCode);
        }
    }
}
