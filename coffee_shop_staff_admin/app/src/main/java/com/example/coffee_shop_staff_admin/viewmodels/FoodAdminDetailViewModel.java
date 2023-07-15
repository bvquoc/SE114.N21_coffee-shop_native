package com.example.coffee_shop_staff_admin.viewmodels;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.example.coffee_shop_staff_admin.BR;

public class FoodAdminDetailViewModel extends BaseObservable {
    @Bindable
    private boolean loadingFood = true;

    public boolean isLoadingFood() {
        return loadingFood;
    }

    public void setLoadingFood(boolean loadingFood) {
        this.loadingFood = loadingFood;
        notifyPropertyChanged(BR.loadingFood);
    }

    @Bindable
    private boolean loadingSize = true;

    public boolean isLoadingSize() {
        return loadingSize;
    }

    public void setLoadingSize(boolean loadingSize) {
        this.loadingSize = loadingSize;
        notifyPropertyChanged(BR.loadingSize);
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

    @Bindable
    private String price = "";

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
        notifyPropertyChanged(BR.price);
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

    @Bindable
    private int index = 0;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
        notifyPropertyChanged(BR.index);
    }

    @Bindable
    private int imageCount = 0;
    public int getImageCount() {
        return imageCount;
    }
    public void setImageCount(int imageCount) {
        this.imageCount = imageCount;
        notifyPropertyChanged(BR.imageCount);
    }
}
