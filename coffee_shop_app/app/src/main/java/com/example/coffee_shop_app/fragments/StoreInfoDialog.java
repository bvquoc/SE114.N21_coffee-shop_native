package com.example.coffee_shop_app.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.coffee_shop_app.R;
import com.example.coffee_shop_app.models.Store;

public class StoreInfoDialog {
    public void showDialog(Activity activity, Store store){
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.store_info_dialog);

        ImageView imgView= (ImageView) dialog.findViewById(R.id.imgView);
        Glide.with(imgView.getContext())
                .load(store.getImages().get(0))
                .into(imgView);

        TextView txtSb= (TextView)dialog.findViewById(R.id.txtSb);
        txtSb.setText(store.getShortName());

        TextView txtPhone= (TextView)dialog.findViewById(R.id.txtPhone);
        txtPhone.setText(store.getPhone());

        TextView txtAddress= (TextView)dialog.findViewById(R.id.txtToAddress);
        txtAddress.setText(store.getAddress().getFormattedAddress());

        Button dialogButton = (Button) dialog.findViewById(R.id.btnCloseDialog);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }
}
