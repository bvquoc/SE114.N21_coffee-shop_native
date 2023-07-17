package com.example.coffee_shop_app.viewmodels;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.lifecycle.MutableLiveData;

import com.example.coffee_shop_app.BR;
import com.example.coffee_shop_app.models.AddressDelivery;
import com.example.coffee_shop_app.models.CartFood;
import com.example.coffee_shop_app.models.Store;
import com.example.coffee_shop_app.repository.AuthRepository;
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
        selectedStore.observeForever(store -> {
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
        });
        selectedAddressDelivery.observeForever(addressDelivery -> {
            if(addressDelivery!=null)
            {
                setUserAddress(addressDelivery.getAddress().getFormattedAddress());
                setNameReceiver(addressDelivery.getNameReceiver());
                setPhone(addressDelivery.getPhone());
            }
            else
            {
                setUserAddress("Chọn địa chỉ");
                String nameReceiver = "Name";
                String phone = "Phone";
                if(AuthRepository.getInstance().getCurrentUser()!=null)
                {
                    nameReceiver = AuthRepository.getInstance().getCurrentUser().getName();
                    phone = AuthRepository.getInstance().getCurrentUser().getPhoneNumber();
                }
                setNameReceiver(nameReceiver);
                setPhone(phone);
            }
            changeDistance();
        });
        selectedOrderType.observeForever(orderType -> setDelivering(orderType == OrderType.Delivery));
        distance.observeForever(aDouble -> {
            DecimalFormat formatter = new DecimalFormat("##0.##");
            String formattedDistance = formatter.format(aDouble);
            setDistanceString(formattedDistance + "km");
        });
        CartViewModel.getInstance().getTotalFood().observeForever(aDouble -> {
            DecimalFormat formatter = new DecimalFormat("#,##0.##");
            setTotalFoodString(formatter.format(aDouble)+"đ");
        });
        CartViewModel.getInstance().getCartFoods().observeForever(cartFoods -> {
            int numberFoods = 0;
            for (CartFood cartFood : cartFoods) {
                numberFoods += cartFood.getQuantity();
            }
            setNumberFoodString(String.valueOf(numberFoods));
            setHasFoodInCart(cartFoods.size() != 0);
        });
    }

    private final MutableLiveData<Store> selectedStore = new MutableLiveData<>();
    private final MutableLiveData<AddressDelivery> selectedAddressDelivery = new MutableLiveData<>();
    private final MutableLiveData<OrderType> selectedOrderType = new MutableLiveData<>(OrderType.Delivery);
    private final MutableLiveData<Double> distance = new MutableLiveData<>(0.0);
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
    private String nameReceiver = null;

    public String getNameReceiver() {
        if(nameReceiver == null)
        {
            if(AuthRepository.getInstance().getCurrentUser()!=null)
            {
                nameReceiver = AuthRepository.getInstance().getCurrentUser().getName();
            }
            else
            {
                nameReceiver = "Name";
            }
        }
        return nameReceiver;
    }

    public void setNameReceiver(String nameReceiver) {
        this.nameReceiver = nameReceiver;
        notifyPropertyChanged(BR.nameReceiver);
    }

    @Bindable
    private String phone = null;

    public String getPhone() {
        if(phone == null)
        {
            if(AuthRepository.getInstance().getCurrentUser()!=null)
            {
                phone = AuthRepository.getInstance().getCurrentUser().getPhoneNumber();
            }
            else
            {
                phone = "Phone";
            }
        }
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

    private void setDelivering(boolean delivering) {
        this.delivering = delivering;
        notifyPropertyChanged(BR.delivering);
    }

    @Bindable String totalFoodString = "0đ";

    public String getTotalFoodString() {
        return totalFoodString;
    }

    public void setTotalFoodString(String totalFoodString) {
        this.totalFoodString = totalFoodString;
        notifyPropertyChanged(BR.totalFoodString);
    }

    @Bindable
    private boolean hasFoodInCart = false;

    public boolean isHasFoodInCart() {
        return hasFoodInCart;
    }

    public void setHasFoodInCart(boolean hasFoodInCart) {
        this.hasFoodInCart = hasFoodInCart;
        notifyPropertyChanged(BR.hasFoodInCart);
    }

    @Bindable String numberFoodString = "0";

    public String getNumberFoodString() {
        return numberFoodString;
    }

    public void setNumberFoodString(String numberFoodString) {
        this.numberFoodString = numberFoodString;
        notifyPropertyChanged(BR.numberFoodString);
    }
    @Bindable
    private boolean needToShowDistance = false;

    public boolean isNeedToShowDistance() {
        return needToShowDistance;
    }

    public void setNeedToShowDistance(boolean needToShowDistance) {
        this.needToShowDistance = needToShowDistance;
        notifyPropertyChanged(BR.needToShowDistance);
    }

    @Bindable
    private String distanceString = "";

    public String getDistanceString() {
        return distanceString;
    }

    public void setDistanceString(String distanceString) {
        this.distanceString = distanceString;
        notifyPropertyChanged(BR.distanceString);
    }

    public void changeDistance()
    {
        if(selectedStore.getValue() == null || selectedAddressDelivery.getValue() == null)
        {
            distance.postValue(-1.0);
            setNeedToShowDistance(false);
        }
        else
        {
            distance.postValue(LocationHelper.calculateDistance(
                    selectedStore.getValue().getAddress().getLat(),
                    selectedStore.getValue().getAddress().getLng(),
                    selectedAddressDelivery.getValue().getAddress().getLat(),
                    selectedAddressDelivery.getValue().getAddress().getLng()));
            setNeedToShowDistance(true);
        }
    }
}

