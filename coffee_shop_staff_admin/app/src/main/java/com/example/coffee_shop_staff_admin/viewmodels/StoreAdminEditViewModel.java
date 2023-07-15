package com.example.coffee_shop_staff_admin.viewmodels;

import android.net.Uri;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.coffee_shop_staff_admin.BR;
import com.example.coffee_shop_staff_admin.models.MLocation;
import com.example.coffee_shop_staff_admin.models.Store;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;

public class StoreAdminEditViewModel extends BaseObservable {
    public StoreAdminEditViewModel()
    {
        openTime.observeForever(date -> setOpenTimeString(getTimeString(date)));
        closeTime.observeForever(date -> setCloseTimeString(getTimeString(date)));
        selectedLocation.observeForever(mLocation -> {
            setLatLng(mLocation.getLat() + ", " + mLocation.getLng());
            setAddress(mLocation.getFormattedAddress());
        });
    }
    private String getTimeString(Date date)
    {
        LocalDateTime localDateTimeOpen = LocalDateTime.ofInstant(
                date.toInstant(),
                ZoneId.systemDefault()
        );
        int hour = localDateTimeOpen.getHour();
        int minute = localDateTimeOpen.getMinute();
        return String.format("%02d", hour) + ":" + String.format("%02d", minute);
    }
    public void updateParameter(Store store)
    {
        setName(store.getShortName());
        setPhone(store.getPhone());
        openTime.postValue(store.getTimeOpen());
        closeTime.postValue(store.getTimeClose());
        selectedLocation.postValue(store.getAddress());
        setButtonText("Lưu");
    }
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
    private String buttonText = "Tạo cửa hàng";

    public String getButtonText() {
        return buttonText;
    }

    public void setButtonText(String buttonText) {
        this.buttonText = buttonText;
        notifyPropertyChanged(BR.buttonText);
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


    private MutableLiveData<Date> closeTime = new MutableLiveData<Date>();

    public MutableLiveData<Date> getCloseTime() {
        return closeTime;
    }


    @Bindable
    private String closeTimeString = "";

    public String getCloseTimeString() {
        return closeTimeString;
    }

    public void setCloseTimeString(String closeTimeString) {
        this.closeTimeString = closeTimeString;
        notifyPropertyChanged(BR.closeTimeString);
    }


    private MutableLiveData<Date> openTime = new MutableLiveData<Date>();

    public MutableLiveData<Date> getOpenTime() {
        return openTime;
    }


    @Bindable
    private String openTimeString = "";

    public String getOpenTimeString() {
        return openTimeString;
    }

    public void setOpenTimeString(String openTimeString) {
        this.openTimeString = openTimeString;
        notifyPropertyChanged(BR.openTimeString);
    }


    @Bindable
    private String phone = "";

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
        notifyPropertyChanged(BR.phone);
    }

    public void onPhoneChanged(CharSequence s, int start, int before, int count)
    {
        phone = s.toString();
    }


    @Bindable
    private String address = "";

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
        notifyPropertyChanged(BR.address);
    }

    public void onAddressChanged(CharSequence s, int start, int before, int count)
    {
        address = s.toString();
    }



    private MutableLiveData<MLocation> selectedLocation = new MutableLiveData<MLocation>();

    public MutableLiveData<MLocation> getSelectedLocation() {
        return selectedLocation;
    }


    @Bindable
    private String latLng = "";

    public String getLatLng() {
        return latLng;
    }

    public void setLatLng(String latLng) {
        this.latLng = latLng;
        notifyPropertyChanged(BR.latLng);
    }
}
