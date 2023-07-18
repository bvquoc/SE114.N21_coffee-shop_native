package com.example.coffee_shop_app.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.example.coffee_shop_app.models.AddressDelivery;
import com.example.coffee_shop_app.models.Promo;
import com.example.coffee_shop_app.models.Store;

import java.util.Date;

public class CartDeliveryViewModel extends ViewModel {
    private CartViewModel cartViewModel;
    private MutableLiveData<Store> fromStore=new MutableLiveData<>();
    private MutableLiveData<AddressDelivery> toAddress=new MutableLiveData<>();
    private MutableLiveData<Double> deliveryCost=new MutableLiveData<>();
    private MutableLiveData<Double> total=new MutableLiveData<>();

    public CartDeliveryViewModel() {
        cartViewModel=CartViewModel.getInstance();
        CartButtonViewModel.getInstance().getSelectedStore().observeForever(new Observer<Store>() {
            @Override
            public void onChanged(Store store) {
                fromStore.setValue(store);
            }
        });

        if(CartButtonViewModel.getInstance().getSelectedAddressDelivery().getValue()==null){
            toAddress.setValue(null);
        }
        CartButtonViewModel.getInstance().getSelectedAddressDelivery().observeForever(new Observer<AddressDelivery>() {
            @Override
            public void onChanged(AddressDelivery addressDelivery) {
                toAddress.setValue(addressDelivery);
            }
        });
        cartViewModel.getDiscount().observeForever(
                dis->calculateTotalPrice());
        deliveryCost.observeForever(ship->calculateTotalPrice());
    }

    public CartViewModel getCartViewModel() {
        return cartViewModel;
    }

    public void setCartViewModel(CartViewModel cartViewModel) {
        this.cartViewModel = cartViewModel;
    }

    public MutableLiveData<Store> getFromStore() {
        return fromStore;
    }

    public void setFromStore(MutableLiveData<Store> fromStore) {
        this.fromStore = fromStore;
    }

    public MutableLiveData<AddressDelivery> getToAddress() {
        return toAddress;
    }

    public void setToAddress(MutableLiveData<AddressDelivery> toAddress) {
        this.toAddress = toAddress;
    }

    public MutableLiveData<Double> getDeliveryCost() {
        return deliveryCost;
    }

    public MutableLiveData<Double> getTotal() {
        return total;
    }

    public void calculateTotalPrice(){
        double total=cartViewModel.getTotalFood().getValue();
        if(deliveryCost.getValue()!=null){
            total=total+deliveryCost.getValue();
        }
        if(cartViewModel.getDiscount().getValue()!=null){
            total=total-cartViewModel.getDiscount().getValue();
        }
        this.total.setValue(total);
    }
}
