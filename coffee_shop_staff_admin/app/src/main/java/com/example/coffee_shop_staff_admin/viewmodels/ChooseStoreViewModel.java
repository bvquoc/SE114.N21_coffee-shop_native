package com.example.coffee_shop_staff_admin.viewmodels;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.example.coffee_shop_staff_admin.BR;

public class ChooseStoreViewModel extends BaseObservable {
    public ChooseStoreViewModel()
    {

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
