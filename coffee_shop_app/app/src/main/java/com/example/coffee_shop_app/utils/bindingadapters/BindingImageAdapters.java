package com.example.coffee_shop_app.utils.bindingadapters;

import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.example.coffee_shop_app.R;

public class BindingImageAdapters {
    @BindingAdapter("android:imageURL")
    public static void setImageURL(ImageView imageView, String URL){
        Glide.with(imageView.getContext())
                .load(URL)
                .placeholder(R.drawable.img_placeholder)
                .error(R.drawable.img_placeholder)
                .fitCenter()
                .into(imageView);

    }
}
