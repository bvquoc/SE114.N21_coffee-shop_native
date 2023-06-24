package com.example.coffee_shop_app.viewmodels;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.coffee_shop_app.BR;
import com.example.coffee_shop_app.models.Store;
import com.example.coffee_shop_app.repository.StoreRepository;
import com.example.coffee_shop_app.utils.interfaces.UpdateDataListener;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

public class StoreDetailViewModel extends BaseObservable {
    public StoreDetailViewModel(String storeId)
    {
        MutableLiveData<List<Store>> stores = StoreRepository.getInstance().getStoreListMutableLiveData();
        stores.observeForever(new Observer<List<Store>>() {
            @Override
            public void onChanged(List<Store> stores) {
                if(isUpdatedFavorite)
                {
                    isUpdatedFavorite = false;
                    return;
                }
                isLoading.setValue(true);
                for (Store store : stores) {
                    if (store.getId().equals(storeId)) {
                        storeMutableLiveData.postValue(store);
                        isLoading.postValue(false);
                        return;
                    }
                }
                storeMutableLiveData.postValue(null);
            }
        });
    }
    @Bindable
    private MutableLiveData<Store> storeMutableLiveData = new MutableLiveData<Store>();

    public MutableLiveData<Store> getStoreMutableLiveData() {
        return storeMutableLiveData;
    }
    @Bindable
    private Store store;

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
        notifyPropertyChanged(BR.store);
        //set TimeActive
        LocalDateTime localDateTimeOpen = LocalDateTime.ofInstant(
                store.getTimeOpen().toInstant(),
                ZoneId.systemDefault()
        );
        LocalDateTime localDateTimeClose = LocalDateTime.ofInstant(
                store.getTimeClose().toInstant(),
                ZoneId.systemDefault()
        );
        int hourOpen = localDateTimeOpen.getHour();
        int minuteOpen = localDateTimeOpen.getMinute();
        int hourClose = localDateTimeClose.getHour();
        int minuteClose = localDateTimeClose.getMinute();
        setTimeActive("Open: " + String.format("%02d", hourOpen) + ":" + String.format("%02d", minuteOpen) +
                " - " + String.format("%02d", hourClose) + ":" + String.format("%02d", minuteClose));
    }
    @Bindable
    private String timeActive;

    public String getTimeActive() {
        return timeActive;
    }

    public void setTimeActive(String timeActive) {
        this.timeActive = timeActive;
        notifyPropertyChanged(BR.timeActive);
    }

    @Bindable
    private int index;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
        notifyPropertyChanged(BR.index);
    }

    @Bindable
    private int imageCount;
    public int getImageCount() {
        return imageCount;
    }
    public void setImageCount(int imageCount) {
        this.imageCount = imageCount;
        notifyPropertyChanged(BR.imageCount);
    }

    @Bindable
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    private boolean isUpdatedFavorite = false;
    public void flipIsFavorite(){
        store.setFavorite(!store.isFavorite());
        notifyPropertyChanged(BR.store);
        isUpdatedFavorite = true;
        StoreRepository.getInstance().updateFavorite(store.getId(), store.isFavorite(), new UpdateDataListener() {
            @Override
            public void onUpdateData(boolean success) {
                if(!success)
                {
                    store.setFavorite(!store.isFavorite());
                    notifyPropertyChanged(BR.store);
                }
            }
        });
    }
}

