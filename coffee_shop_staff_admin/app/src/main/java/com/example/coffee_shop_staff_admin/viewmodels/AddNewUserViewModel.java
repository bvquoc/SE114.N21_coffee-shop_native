package com.example.coffee_shop_staff_admin.viewmodels;

import android.widget.CompoundButton;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.example.coffee_shop_staff_admin.BR;

public class AddNewUserViewModel extends BaseObservable {
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
    private boolean loading = false;

    public boolean isLoading() {
        return loading;
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
        notifyPropertyChanged(BR.loading);
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
    private String email = "";

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        notifyPropertyChanged(BR.email);
    }

    public void onEmailChanged(CharSequence s, int start, int before, int count)
    {
        email = s.toString();
    }
    @Bindable
    private String storeName = "";

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
        notifyPropertyChanged(BR.storeName);
    }
    @Bindable
    private String storeID = "";

    public String getStoreID() {
        return storeID;
    }

    public void setStoreID(String storeID) {
        this.storeID = storeID;
        notifyPropertyChanged(BR.storeID);
    }
    @Bindable
    private boolean addStaff = true;

    public boolean isAddStaff() {
        return addStaff;
    }

    public void setAddStaff(boolean addStaff) {
        this.addStaff = addStaff;
        notifyPropertyChanged(BR.addStaff);
    }

    public void onIsAddStaffChanged(CompoundButton buttonView, boolean isChecked) {
        setAddStaff(isChecked);
    }
}
