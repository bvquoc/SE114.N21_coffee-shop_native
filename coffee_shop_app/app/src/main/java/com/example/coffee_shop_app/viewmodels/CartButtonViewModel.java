package com.example.coffee_shop_app.viewmodels;

import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.coffee_shop_app.BR;
import com.example.coffee_shop_app.models.Address;
import com.example.coffee_shop_app.models.AddressDelivery;
import com.example.coffee_shop_app.models.Store;
import com.example.coffee_shop_app.repository.ProductRepository;
import com.example.coffee_shop_app.utils.LocationHelper;

import java.text.DecimalFormat;

public class CartButtonViewModel extends BaseObservable {
    //Singleton
    private static CartButtonViewModel instance;
    public static CartButtonViewModel getInstance()
    {
        if(instance == null)
        {
            instance = new CartButtonViewModel();
        }
        return instance;
    }
    private CartButtonViewModel(){
        selectedStore.observeForever(new Observer<Store>() {
            @Override
            public void onChanged(Store store) {
                ProductRepository.getInstance().registerSnapshotListener(store.getStateFood());
                if(store!=null)
                {
                    setStoreAddress(store.getShortName() + ", " + store.getAddress().getFormattedAddress());
                }
                else
                {
                    setStoreAddress("Chọn cửa hàng");
                }
                changeDistance();
            }
        });
        selectedAddressDelivery.observeForever(new Observer<AddressDelivery>() {
            @Override
            public void onChanged(AddressDelivery addressDelivery) {
                if(addressDelivery!=null)
                {
                    setUserAddress(addressDelivery.getAddress().getFormattedAddress());
                    setNameReceiver(addressDelivery.getNameReceiver());
                    setPhone(addressDelivery.getPhone());
                }
                else
                {
                    setUserAddress("Chọn địa chỉ");
                    setNameReceiver("Nick");
                    setPhone("0123456789");
                }
                changeDistance();
            }
        });
        selectedOrderType.observeForever(new Observer<OrderType>() {
            @Override
            public void onChanged(OrderType orderType) {
                if(orderType == OrderType.Delivery)
                {
                    setDelivering(true);
                }
                else
                {
                    setDelivering(false);
                }
            }
        });
    }

    private MutableLiveData<Store> selectedStore = new MutableLiveData<Store>();
    private MutableLiveData<AddressDelivery> selectedAddressDelivery = new MutableLiveData<AddressDelivery>();
    private MutableLiveData<OrderType> selectedOrderType = new MutableLiveData<OrderType>();

    public MutableLiveData<Store> getSelectedStore() {
        return selectedStore;
    }

    public MutableLiveData<AddressDelivery> getSelectedAddressDelivery() {
        return selectedAddressDelivery;
    }

    public MutableLiveData<OrderType> getSelectedOrderType() {
        return selectedOrderType;
    }

    @Bindable
    private String storeAddress = "Chọn cửa hàng";
    public String getStoreAddress() {
        return storeAddress;
    }

    public void setStoreAddress(String storeAddress) {
        this.storeAddress = storeAddress;
        notifyPropertyChanged(BR.storeAddress);
    }

    @Bindable
    private String userAddress = "Chọn địa chỉ";
    public String getUserAddress()
    {
        return userAddress;
    }
    public void setUserAddress(String userAddress)
    {
        this.userAddress = userAddress;
        notifyPropertyChanged(BR.userAddress);
    }


    @Bindable
    private String nameReceiver = "Nick";

    public String getNameReceiver() {
        return nameReceiver;
    }

    public void setNameReceiver(String nameReceiver) {
        this.nameReceiver = nameReceiver;
        notifyPropertyChanged(BR.nameReceiver);
    }

    @Bindable
    private String phone = "0123456789";

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
        notifyPropertyChanged(BR.phone);
    }

    @Bindable
    private boolean delivering = true;

    public boolean isDelivering() {
        return delivering;
    }

    public void setDelivering(boolean delivering) {
        this.delivering = delivering;
        notifyPropertyChanged(BR.delivering);
    }

    @Bindable
    private double distance = -1;
    public double getDistance()
    {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
        DecimalFormat formatter = new DecimalFormat("##0.##");
        String formattedDistance = formatter.format(distance);
        setDistanceString(formattedDistance + "km");
        notifyPropertyChanged(BR.distance);
    }

    @Bindable
    private String distanceString = "";

    public String getDistanceString() {
        return distanceString;
    }

    public void setDistanceString(String distanceString) {
        this.distanceString = distanceString;
    }

    public void changeDistance()
    {
        if(selectedStore.getValue() == null || selectedAddressDelivery.getValue() == null)
        {
            setDistance(-1);
        }
        else
        {
            setDistance(LocationHelper.calculateDistance(
                    selectedStore.getValue().getAddress().getLat(),
                    selectedStore.getValue().getAddress().getLng(),
                    selectedAddressDelivery.getValue().getAddress().getLat(),
                    selectedAddressDelivery.getValue().getAddress().getLng()));
        }
    }
}

