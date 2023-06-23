package com.example.coffee_shop_app.viewmodels;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.lifecycle.MutableLiveData;

import com.example.coffee_shop_app.models.Store;
import com.example.coffee_shop_app.repository.StoreRepository;

import java.util.List;

public class StoreSearchViewModel extends BaseObservable {
    public StoreSearchViewModel(){
        storeListMutableLiveData = StoreRepository.getInstance().getStoreListMutableLiveData();
    }
    @Bindable
    private MutableLiveData<List<Store>> storeListMutableLiveData;

    public MutableLiveData<List<Store>> getLiveStoreData() {
        return storeListMutableLiveData;
    }

    @Bindable
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }
}
