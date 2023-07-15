package com.example.coffee_shop_staff_admin.viewmodels;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.example.coffee_shop_staff_admin.BR;
import com.example.coffee_shop_staff_admin.models.Food;

import java.text.DecimalFormat;

public class FoodAdminEditViewModel extends BaseObservable {
    public void updateParameter(Food food)
    {
        setName(food.getName());

        setDescription(food.getDescription());

        DecimalFormat formatter = new DecimalFormat("##0.##");
        String price = formatter.format(food.getPrice());
        setPrice(price);

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
    private String buttonText = "Tạo đồ uống";

    public String getButtonText() {
        return buttonText;
    }

    public void setButtonText(String buttonText) {
        this.buttonText = buttonText;
        notifyPropertyChanged(BR.buttonText);
    }


    @Bindable
    private boolean loadingTopping = true;

    public boolean isLoadingTopping() {
        return loadingTopping;
    }

    public void setLoadingTopping(boolean loadingTopping) {
        this.loadingTopping = loadingTopping;
        notifyPropertyChanged(BR.loadingTopping);
    }


    @Bindable
    private boolean loadingSize= true;

    public boolean isLoadingSize() {
        return loadingSize;
    }

    public void setLoadingSize(boolean loadingSize) {
        this.loadingSize = loadingSize;
        notifyPropertyChanged(BR.loadingSize);
    }


    @Bindable
    private boolean updating = false;

    public boolean isUpdating() {
        return updating;
    }

    public void setUpdating(boolean updating) {
        this.updating = updating;
        notifyPropertyChanged(BR.updating);
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


    @Bindable
    private String description = "";

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        notifyPropertyChanged(BR.description);
    }

    public void onDescriptionChanged(CharSequence s, int start, int before, int count)
    {
        description = s.toString();
    }
}
