package com.example.coffee_shop_staff_admin.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.view.Window;

import com.example.coffee_shop_staff_admin.R;

public class LoadingDialog {
    private Dialog dialog;
    public void showDialog(Activity activity){
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.order_waiting_dialog);

        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }
    public void dismiss(){
        dialog.dismiss();
    }
}
