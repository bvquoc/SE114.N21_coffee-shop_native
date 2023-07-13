package com.example.coffee_shop_staff_admin.viewmodels;

import android.net.Uri;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.lifecycle.MutableLiveData;

import com.example.coffee_shop_staff_admin.BR;
import com.example.coffee_shop_staff_admin.models.Topping;

import java.text.DecimalFormat;

public class ToppingAdminEditViewModel extends BaseObservable {
    public void updateParameter(Topping topping)
    {
        setName(topping.getName());
        DecimalFormat formatter = new DecimalFormat("###0.##");
        String formattedPrice = formatter.format(topping.getPrice());
        setPrice(formattedPrice);
        getImageSource().postValue(Uri.parse(topping.getImage()));
        setHasImage(true);
        setButtonText("Lưu");
    }
    @Bindable
    private boolean keyBoardShow = false;

    public boolean isKeyBoardShow() {
        return keyBoardShow;
    }

    public void setKeyBoardShow(boolean keyBoardShow) {
        this.keyBoardShow = keyBoardShow;
        notifyPropertyChanged(BR.keyBoardShow);
    }

    @Bindable
    private String buttonText = "Tạo topping";

    public String getButtonText() {
        return buttonText;
    }

    public void setButtonText(String buttonText) {
        this.buttonText = buttonText;
        notifyPropertyChanged(BR.buttonText);
    }

    @Bindable
    private boolean hasImage = false;

    public boolean isHasImage() {
        return hasImage;
    }

    public void setHasImage(boolean hasImage) {
        this.hasImage = hasImage;
        notifyPropertyChanged(BR.hasImage);
    }

    @Bindable
    private boolean loading = false;

    public boolean isLoading() {
        return loading;
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
        notifyPropertyChanged(BR.loading);
    }

    @Bindable
    private String name = "";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        notifyPropertyChanged(BR.name);
    }

    public void onNameChanged(CharSequence s, int start, int before, int count)
    {
        name = s.toString();
    }

    @Bindable
    private String price = "";

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
        notifyPropertyChanged(BR.price);
    }

    public void onPriceChanged(CharSequence s, int start, int before, int count)
    {
        price = s.toString();
    }

    private MutableLiveData<Uri> imageSource = new MutableLiveData<Uri>();

    public MutableLiveData<Uri> getImageSource() {
        return imageSource;
    }
}
