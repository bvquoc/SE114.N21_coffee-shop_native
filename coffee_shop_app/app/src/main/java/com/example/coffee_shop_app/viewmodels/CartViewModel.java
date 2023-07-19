package com.example.coffee_shop_app.viewmodels;


import androidx.databinding.BaseObservable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.example.coffee_shop_app.models.AddressDelivery;
import com.example.coffee_shop_app.models.CartFood;
import com.example.coffee_shop_app.models.Order;
import com.example.coffee_shop_app.models.OrderFood;
import com.example.coffee_shop_app.models.Promo;
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
    private MutableLiveData<Promo> promo=new MutableLiveData<>();
    private MutableLiveData<List<Integer>> notAvailableCartFoods=new MutableLiveData<>(new ArrayList<>());
    public CartViewModel() {
//        cartFoods.observeForever(cartFoods -> calculateTotalFood());

        cartFoods.observeForever(new Observer<List<CartFood>>() {
            @Override
            public void onChanged(List<CartFood> cartFoods) {
                calculateTotalFood();
                List<Integer> notAvaiTempList=notAvailableCartFoods.getValue();
                List<Integer> toRemove=new ArrayList<>();
                boolean needPost=false;
                for (Integer cfId :
                     notAvailableCartFoods.getValue()) {
                    if(!cartFoods.stream().anyMatch(cf->cf.getId()==cfId)){
                        toRemove.add((Integer) cfId);
                        needPost=true;
                    }
                }
                if(needPost){
                    notAvaiTempList.removeAll(toRemove);
                    notAvailableCartFoods.postValue(notAvaiTempList);
                }
            }
        });
        promo.observeForever(new Observer<Promo>() {
            @Override
            public void onChanged(Promo promo) {
                calculatePromo();
            }
        });
//        notAvailableCartFoods.observeForever(new Observer<List<Integer>>() {
//            @Override
//            public void onChanged(List<Integer> integers) {
//                String temp="";
//            }
//        });
    }
    public void calculatePromo(){
        if(promo!=null && promo.getValue()!=null){
            Double discount=totalFood.getValue()*promo.getValue().getPercent();
            if(discount>promo.getValue().getMaxPrice()){
                discount=promo.getValue().getMaxPrice();
            }
            getDiscount().postValue(discount);
        } else{
            getDiscount().postValue(0.0);
        }
    }
    public MutableLiveData<Promo> getPromo() {
        return promo;
    }

    public MutableLiveData<List<CartFood>> getCartFoods() {
        return cartFoods;
    }

    public void setCartFoods(MutableLiveData<List<CartFood>> cartFoods) {
        this.cartFoods = cartFoods;
    }

    public MutableLiveData<List<Integer>> getNotAvailableCartFoods() {
        return notAvailableCartFoods;
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
        calculatePromo();
    }
}
