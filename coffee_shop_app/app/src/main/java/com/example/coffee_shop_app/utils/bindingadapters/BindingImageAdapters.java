package com.example.coffee_shop_app.utils.bindingadapters;

import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;

public class BindingImageAdapters {
    @BindingAdapter("android:imageURL")
    public static void setImageURL(ImageView imageView, String URL){
        Glide.with(imageView.getContext()).load(URL).into(imageView);

    }
}
