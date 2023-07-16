package com.example.coffee_shop_app.viewmodels;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.lifecycle.MutableLiveData;

import com.example.coffee_shop_app.BR;
import com.example.coffee_shop_app.models.MLocation;
import com.example.coffee_shop_app.models.Store;
import com.example.coffee_shop_app.repository.StoreRepository;
import com.example.coffee_shop_app.utils.LocationHelper;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class StoreViewModel extends BaseObservable {
    public StoreViewModel(){
        nearestStoreMutableLiveData = new MutableLiveData<>();
        favoriteStoresListMutableLiveData = new MutableLiveData<>();
        otherStoresListMutableLiveData = new MutableLiveData<>();

        allStores = StoreRepository.getInstance().getStoreListMutableLiveData();
        allStores.observeForever(stores -> {
            if(stores == null)
            {
                isLoading.setValue(true);
                return;
            }
            isLoading.setValue(true);
            List<Store> favoriteStores = new ArrayList<>();
            List<Store> otherStores = new ArrayList<>();

            if(stores.size() > 1)
            {
                int index = 0;
                if(LocationHelper.getInstance().getCurrentLocation() != null)
                {
                    LatLng currentLocation = LocationHelper.getInstance().getCurrentLocation();
                    MLocation storeLocation = stores.get(0).getAddress();
                    if(LocationHelper.calculateDistance(
                            storeLocation.getLat(),
                            storeLocation.getLng(),
                            currentLocation.latitude,
                            currentLocation.longitude) < 15)
                    {
                        nearestStoreMutableLiveData.postValue(stores.get(0));
                        index++;
                    }
                }
                for(; index < stores.size(); index++)
                {
                    Store store = stores.get(index);
                    if (store.isFavorite()) {
                        favoriteStores.add(store);
                    } else {
                        otherStores.add(store);
                    }
                }
            }
            else
            {
                nearestStoreMutableLiveData.postValue(null);
            }
            favoriteStoresListMutableLiveData.postValue(favoriteStores);
            otherStoresListMutableLiveData.postValue(otherStores);
            isLoading.postValue(false);
        });
    }
    @Bindable
    private final MutableLiveData<List<Store>> allStores;
    @Bindable
    private final MutableLiveData<Store> nearestStoreMutableLiveData;
    public MutableLiveData<Store> getNearestStoreMutableLiveData() {
        return nearestStoreMutableLiveData;
    }

    @Bindable
    private final MutableLiveData<List<Store>> otherStoresListMutableLiveData;
    public MutableLiveData<List<Store>> getOtherStores() {
        return otherStoresListMutableLiveData;
    }

    @Bindable
    private final MutableLiveData<List<Store>> favoriteStoresListMutableLiveData;
    public MutableLiveData<List<Store>> getFavoriteStores() {
        return favoriteStoresListMutableLiveData;
    }


    @Bindable
    private boolean hasNearestStore;
    public boolean isHasNearestStore() {
        return hasNearestStore;
    }
    public void setHasNearestStore(boolean hasNearestStore) {
        this.hasNearestStore = hasNearestStore;
        notifyPropertyChanged(BR.hasNearestStore);
        if(otherStoresListMutableLiveData.getValue()==null || otherStoresListMutableLiveData.getValue().size() == 0)
        {
            setOtherText("");
        }
        else if(hasNearestStore || hasFavoriteStores)
        {
            setOtherText("Khác");
        }
        else{
            setOtherText("Tất cả");
        }
    }

    @Bindable
    private boolean hasFavoriteStores;
    public boolean isHasFavoriteStores(){
        return  hasFavoriteStores;
    }
    public void setHasFavoriteStores(boolean hasFavoriteStores) {
        this.hasFavoriteStores = hasFavoriteStores;
        notifyPropertyChanged(BR.hasFavoriteStores);
        if(otherStoresListMutableLiveData.getValue()==null || otherStoresListMutableLiveData.getValue().size() == 0)
        {
            setOtherText("");
        }
        else if(hasNearestStore || hasFavoriteStores)
        {
            setOtherText("Khác");
        }
        else{
            setOtherText("Tất cả");
        }
    }
    @Bindable
    private String otherText = "Tất cả";

    public String getOtherText() {
        return otherText;
    }

    public void setOtherText(String otherText) {
        this.otherText = otherText;
        notifyPropertyChanged(BR.otherText);
    }

    @Bindable
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }
}
