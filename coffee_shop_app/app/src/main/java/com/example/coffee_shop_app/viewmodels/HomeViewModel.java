package com.example.coffee_shop_app.viewmodels;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.lifecycle.MutableLiveData;

import com.example.coffee_shop_app.BR;
import com.example.coffee_shop_app.models.Product;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends BaseObservable {
    private final MutableLiveData<List<Product>> recentProducts = new MutableLiveData<>(new ArrayList<>());

    public final MutableLiveData<List<Product>> getRecentProducts() {
        return recentProducts;
    }

    @Bindable
    private boolean loading = true;
    public boolean isLoading() {
        return loading;
    }
    public void setLoading(boolean loading) {
        this.loading = loading;
        notifyPropertyChanged(BR.loading);
    }

    @Bindable
    private boolean hasRecentFoods = false;

    public boolean isHasRecentFoods() {
        return hasRecentFoods;
    }

    public void setHasRecentFoods(boolean hasRecentFoods) {
        this.hasRecentFoods = hasRecentFoods;
        notifyPropertyChanged(BR.hasRecentFoods);
    }
}
