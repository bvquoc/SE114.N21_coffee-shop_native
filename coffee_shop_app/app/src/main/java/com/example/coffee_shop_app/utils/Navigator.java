package com.example.coffee_shop_app.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public class Navigator {
    private Context context;

    private Navigator(Context context) {
        this.context = context;
    }

    public static Navigator of(Context context) {
        return new Navigator(context);
    }

    public void push(Class<? extends Activity> targetActivity) {
        Intent intent = new Intent(context, targetActivity);
        context.startActivity(intent);
    }

    public void pushForResult(Class<? extends Activity> targetActivity, int requestCode, ResultCallback callback) {
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            Intent intent = new Intent(activity, targetActivity);
            activity.startActivityForResult(intent, requestCode);
            ResultHandler.getInstance().registerCallback(requestCode, callback);
        } else {
            throw new RuntimeException("Context is not an instance of Activity");
        }
    }

    public interface ResultCallback {
        void onResult(Intent data);
    }
}
