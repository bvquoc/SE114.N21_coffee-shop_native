package com.example.coffee_shop_app.viewmodels;


import androidx.databinding.BaseObservable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.coffee_shop_app.models.AddressDelivery;
import com.example.coffee_shop_app.models.CartFood;
import com.example.coffee_shop_app.models.Order;
import com.example.coffee_shop_app.models.OrderFood;
import com.example.coffee_shop_app.models.Store;
import com.example.coffee_shop_app.repository.CartRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public  class CartViewModel extends BaseObservable {
    //Singleton
    private static CartViewModel instance;
    public static CartViewModel getInstance()
    {
        if(instance == null)
        {
            instance = new CartViewModel();
        }
        return instance;
    }
    private MutableLiveData<List<CartFood>> cartFoods=new MutableLiveData<>();
    private CartRepository repository=new CartRepository();
    private MutableLiveData<Double> totalFood=new MutableLiveData<>();
    private MutableLiveData<Double> discount=new MutableLiveData<>();
    public CartViewModel() {

    }

    public MutableLiveData<List<CartFood>> getCartFoods() {
        return cartFoods;
    }

    public void setCartFoods(MutableLiveData<List<CartFood>> cartFoods) {
        this.cartFoods = cartFoods;
    }

    public MutableLiveData<Double> getTotalFood() {
        return totalFood;
    }

    public MutableLiveData<Double> getDiscount() {
        return discount;
    }

    public void calculateTotalFood(){
        double totalFood=0;
        for (CartFood prd :
                cartFoods.getValue()) {
            totalFood=totalFood+prd.getUnitPrice()*prd.getQuantity();
        }
        this.totalFood.setValue(totalFood);
    }

//    public void placeOrder(Store store, AddressDelivery address, Date pickupTime, String status){
//        List<OrderFood> orderFoods=new ArrayList<>();
//        for (CartFood cartFood :
//                this.cartFoods.getValue()) {
//
//            //TODO: get the size and topping properly
//            //TODO: fix this
////            OrderFood ord= new OrderFood(
////                    cartFood.getProduct().getImage(),
////                    cartFood.getProduct().getName(),
////                    cartFood.getQuantity(),
////                    cartFood.getSize(),
////                    cartFood.getUnitPrice());
////            ord.setNote(cartFood.getNote());
////            ord.setTopping(cartFood.getTopping());
////            orderFoods.add(ord);
//        }
//        Order order=new Order(new Date(), orderFoods, status);
//        order.setTotal(this.total.getValue());
//        order.setTotalProduct(this.totalFood.getValue());
//        order.setAddress(address);
//        order.setPickupTime(pickupTime);
//        repository.addOrder(order);
//    }
}
