package com.example.coffee_shop_staff_admin.viewmodels;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.coffee_shop_staff_admin.BR;
import com.example.coffee_shop_staff_admin.R;
import com.example.coffee_shop_staff_admin.models.Order;
import com.example.coffee_shop_staff_admin.repositories.OrderRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OrderOfStoreViewModel extends BaseObservable {
    private static OrderOfStoreViewModel instance;
    public static OrderOfStoreViewModel getInstance()
    {
        if(instance == null)
        {
            instance = new OrderOfStoreViewModel();
        }
        return instance;
    }
    private final MutableLiveData<List<Order>> listOrders = new MutableLiveData<>(new ArrayList<>());
    private final  MutableLiveData<List<Order>> orderDeliListMutableLiveData=new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<Order>> orderPickupListMutableLiveData=new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<String> statusDeli=new MutableLiveData<>("Tất cả");
    private final MutableLiveData<String> statusPickup=new MutableLiveData<>("Tất cả");

    public OrderOfStoreViewModel() {
        OrderRepository.getInstance().getOrderListMutableLiveData().observeForever(new Observer<List<Order>>() {
            @Override
            public void onChanged(List<Order> orders) {
                setLoading(true);
                listOrders.postValue(orders);
                orderDeliListMutableLiveData
                        .postValue(orders.stream()
                                .filter(order -> order.getAddress()!=null)
                                .collect(Collectors.toList()));
                orderPickupListMutableLiveData
                        .postValue(orders.stream()
                                .filter(order -> order.getPickupTime()!=null)
                                .collect(Collectors.toList()));
                setLoading(false);
            }
        });
    }



    public MutableLiveData<List<Order>> getListOrders() {
        return listOrders;
    }

    public MutableLiveData<List<Order>> getOrderDeliListMutableLiveData() {
        return orderDeliListMutableLiveData;
    }

    public MutableLiveData<List<Order>> getOrderPickupListMutableLiveData() {
        return orderPickupListMutableLiveData;
    }

    public MutableLiveData<String> getStatusDeli() {
        return statusDeli;
    }

    public MutableLiveData<String> getStatusPickup() {
        return statusPickup;
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
