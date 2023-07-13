package com.example.coffee_shop_app.viewmodels;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.lifecycle.MutableLiveData;

import com.example.coffee_shop_app.models.Product;
import com.example.coffee_shop_app.repository.ProductRepository;

import java.util.List;

public class ProductSearchViewModel extends BaseObservable{
    public ProductSearchViewModel(){
        productListMutableLiveData = ProductRepository.getInstance().getProductListMutableLiveData();
    }
    @Bindable
    private final MutableLiveData<List<Product>> productListMutableLiveData;

    public MutableLiveData<List<Product>> getLiveProductData() {
        return productListMutableLiveData;
    }

    @Bindable
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }
}
