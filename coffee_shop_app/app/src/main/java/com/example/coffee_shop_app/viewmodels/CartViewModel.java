package com.example.coffee_shop_app.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.coffee_shop_app.models.AddressDelivery;
import com.example.coffee_shop_app.models.CartFood;
import com.example.coffee_shop_app.models.Order;
import com.example.coffee_shop_app.models.OrderFood;
import com.example.coffee_shop_app.models.Product;
import com.example.coffee_shop_app.models.Store;
import com.example.coffee_shop_app.repository.CartRepository;
import com.google.type.DateTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CartViewModel extends ViewModel {
    private MutableLiveData<List<CartFood>> cartFoods=new MutableLiveData<>();
    private CartRepository repository=new CartRepository();
    private MutableLiveData<Double> total=new MutableLiveData<>();
    private MutableLiveData<Double> totalFood=new MutableLiveData<>();
    private MutableLiveData<Double> deliveryCost=new MutableLiveData<>();

    public CartViewModel() {

    }

    public MutableLiveData<List<CartFood>> getCartFoods() {
        return cartFoods;
    }

    public void setCartFoods(MutableLiveData<List<CartFood>> cartFoods) {
        this.cartFoods = cartFoods;
    }

    public MutableLiveData<Double> getTotal() {
        return total;
    }

    public MutableLiveData<Double> getTotalFood() {
        return totalFood;
    }

    public MutableLiveData<Double> getDeliveryCost() {
        return deliveryCost;
    }
    public void calculateTotalPrice(){
        double totalFood=0;
        double total=0;
        for (CartFood prd :
                cartFoods.getValue()) {
            totalFood=totalFood+prd.getUnitPrice()*prd.getQuantity();
        }
        if(deliveryCost.getValue()!=null){
            total=totalFood+deliveryCost.getValue();
        } else{
            total=totalFood;
        }
        this.totalFood.setValue(totalFood);
        this.total.setValue(total);
    }

    public void placeOrder(Store store, AddressDelivery address, Date pickupTime, String status){
        List<OrderFood> orderFoods=new ArrayList<>();
        for (CartFood cartFood :
                this.cartFoods.getValue()) {

            //TODO: get the size and topping properly
            //TODO: fix this
//            OrderFood ord= new OrderFood(
//                    cartFood.getProduct().getImage(),
//                    cartFood.getProduct().getName(),
//                    cartFood.getQuantity(),
//                    cartFood.getSize(),
//                    cartFood.getUnitPrice());
//            ord.setNote(cartFood.getNote());
//            ord.setTopping(cartFood.getTopping());
//            orderFoods.add(ord);
        }
        Order order=new Order(new Date(), orderFoods, status);
        order.setTotal(this.total.getValue());
        order.setTotalProduct(this.totalFood.getValue());
        order.setAddress(address);
        order.setPickupTime(pickupTime);
        repository.addOrder(order);
    }
}
