package com.example.coffee_shop_app.viewmodels;

import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.coffee_shop_app.BR;
import com.example.coffee_shop_app.models.Product;
import com.example.coffee_shop_app.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;

public class MenuViewModel extends BaseObservable {
    private final MutableLiveData<List<Product>> favoriteProducts = new MutableLiveData<>(new ArrayList<Product>());
    private final MutableLiveData<List<Product>> otherProducts = new MutableLiveData<>(new ArrayList<Product>());

    public MutableLiveData<List<Product>> getFavoriteProducts() {
        return favoriteProducts;
    }

    public MutableLiveData<List<Product>> getOtherProducts() {
        return otherProducts;
    }

    public MenuViewModel()
    {
        ProductRepository.getInstance().getProductListMutableLiveData().observeForever(new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> products) {
                setLoading(true);
                List<Product> favoriteList = new ArrayList<>();
                List<Product> otherList = new ArrayList<>();
                for (Product product: products
                     ) {
                    if(product.isFavorite())
                    {
                        favoriteList.add(product);
                    }
                    else
                    {
                        otherList.add(product);
                    }
                }
                favoriteProducts.postValue(favoriteList);
                otherProducts.postValue(otherList);
                if(favoriteList.size() != 0)
                {
                    setHasFavoriteFood(true);
                }
                else
                {
                    setHasFavoriteFood(false);
                }

                if(favoriteList.size() != 0 && otherList.size()!=0)
                {
                    setHasOtherFood(true);
                }
                else
                {
                    setHasOtherFood(false);
                }
                setLoading(false);
            }
        });
    }

    @Bindable
    private boolean hasFavoriteFood = false;

    public boolean isHasFavoriteFood() {
        return hasFavoriteFood;
    }

    public void setHasFavoriteFood(boolean hasFavoriteFood) {
        this.hasFavoriteFood = hasFavoriteFood;
        notifyPropertyChanged(BR.hasFavoriteFood);
    }

    @Bindable
    private boolean hasOtherFood = false;

    public boolean isHasOtherFood() {
        return hasOtherFood;
    }

    public void setHasOtherFood(boolean hasOtherFood) {
        this.hasOtherFood = hasOtherFood;
        notifyPropertyChanged(BR.hasOtherFood);
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
}
