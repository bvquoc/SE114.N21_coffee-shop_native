package com.example.coffee_shop_staff_admin.viewmodels.product;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.lifecycle.MutableLiveData;

import com.example.coffee_shop_staff_admin.BR;
import com.example.coffee_shop_staff_admin.models.Food;
import com.example.coffee_shop_staff_admin.models.FoodChecker;
import com.example.coffee_shop_staff_admin.models.Store;
import com.example.coffee_shop_staff_admin.models.StoreProduct;
import com.example.coffee_shop_staff_admin.models.Topping;
import com.example.coffee_shop_staff_admin.repositories.FoodRepository;
import com.example.coffee_shop_staff_admin.repositories.StoreRepository;
import com.example.coffee_shop_staff_admin.repositories.ToppingRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StaffProductDetailViewModel extends BaseObservable {

    private MutableLiveData<Store> currentStore;
    StoreProduct product;
    StoreRepository storeRepository = StoreRepository.getInstance();

    public StaffProductDetailViewModel(StoreProduct product) {
        currentStore = StoreRepository.getInstance().getCurrentStore();
        this.product = product;
    }

    public void onUpdateProduct(StoreProduct product) {
        Store storeRef = currentStore.getValue();
        if(product instanceof FoodChecker){

            Map<String, List<String>> stateFood = storeRef.getStateFood();

            if(stateFood == null){
                stateFood = new HashMap<>();
            }

            FoodChecker productTemp = (FoodChecker) product;

            Food food = (Food) product.getProduct();

            if(productTemp.getBlockSize().isEmpty()){
                productTemp.setStocking(true);
                productTemp.setBlockSize(null);
            }
            else if(productTemp.getBlockSize().size() == food.getSizes().size()){
                productTemp.setStocking(false);
                stateFood.put(food.getId(), new ArrayList<>());
            }
            else {
                productTemp.setStocking(true);
                stateFood.put(productTemp.getId(), productTemp.getBlockSize());
            }

            if(productTemp.getBlockSize() == null){
                stateFood.remove(food.getId());
            }

            storeRef.setStateFood(stateFood);
        }

        ProductOfStoreViewModel.getInstance().onUpdateProductByPass(storeRef, product);
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
