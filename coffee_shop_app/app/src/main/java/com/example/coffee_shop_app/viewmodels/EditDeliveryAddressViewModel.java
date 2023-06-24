package com.example.coffee_shop_app.viewmodels;

import android.text.Editable;
import android.text.TextWatcher;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.lifecycle.MutableLiveData;

import com.example.coffee_shop_app.BR;
import com.example.coffee_shop_app.models.AddressDelivery;
import com.example.coffee_shop_app.models.MLocation;
import com.example.coffee_shop_app.repository.AddressRepository;

public class EditDeliveryAddressViewModel extends BaseObservable {
    public EditDeliveryAddressViewModel()
    {
        setAddress(null);
        setAddressNote("");
        setNameReceiver("");
        setPhone("");
    }
    public EditDeliveryAddressViewModel(AddressDelivery addressDelivery)
    {
        setAddress(addressDelivery.getAddress());
        setAddressNote(addressDelivery.getAddressNote());
        setNameReceiver(addressDelivery.getNameReceiver());
        setPhone(addressDelivery.getPhone());
    }
    public void updateParameter(AddressDelivery addressDelivery)
    {
        setAddress(addressDelivery.getAddress());
        setAddressNote(addressDelivery.getAddressNote());
        setNameReceiver(addressDelivery.getNameReceiver());
        setPhone(addressDelivery.getPhone());
    }
    @Bindable
    private MLocation address;

    public MLocation getAddress() {
        return address;
    }

    public void setAddress(MLocation address) {
        this.address = address;
        notifyPropertyChanged(BR.address);
        if(address == null)
        {
            setFormattedAddress("Chọn địa chỉ");
        }
        else
        {
            setFormattedAddress(address.getFormattedAddress());
        }
    }
    @Bindable
    private String addressNote;

    public String getAddressNote() {
        return addressNote;
    }

    public void setAddressNote(String addressNote) {
        this.addressNote = addressNote;
        notifyPropertyChanged(BR.addressNote);
    }
    public void onAddressNoteChanged(CharSequence s, int start, int before, int count)
    {
        addressNote = s.toString();
    }
    @Bindable
    private String nameReceiver;

    public String getNameReceiver() {
        return nameReceiver;
    }

    public void setNameReceiver(String nameReceiver) {
        this.nameReceiver = nameReceiver;
        notifyPropertyChanged(BR.nameReceiver);
    }
    public void onNameReceiverChanged(CharSequence s, int start, int before, int count)
    {
        nameReceiver = s.toString();
    }
    @Bindable
    private String phone;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
    public void onPhoneChanged(CharSequence s, int start, int before, int count)
    {
        phone = s.toString();
    }

    @Bindable
    private String formattedAddress;

    public void setFormattedAddress(String formattedAddress) {
        this.formattedAddress = formattedAddress;
        notifyPropertyChanged(BR.formattedAddress);
    }

    public String getFormattedAddress()
    {
        return formattedAddress;
    }

    @Bindable
    private boolean isAddressValid;

    public boolean isAddressValid() {
        return isAddressValid;
    }

    public void setAddressValid(boolean addressValid) {
        isAddressValid = addressValid;
        notifyPropertyChanged(BR.isAddressValid);
    }

    @Bindable
    private boolean updatingAddress;

    public boolean isUpdatingAddress() {
        return updatingAddress;
    }

    public void setUpdatingAddress(boolean updatingAddress) {
        this.updatingAddress = updatingAddress;
        notifyPropertyChanged(BR.updatingAddress);
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
}
