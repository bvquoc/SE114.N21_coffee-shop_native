package com.example.coffee_shop_app.models;

import android.util.Log;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.lifecycle.Observer;

import com.example.coffee_shop_app.BR;
import com.example.coffee_shop_app.Data;
import com.example.coffee_shop_app.repository.SizeRepository;
import com.example.coffee_shop_app.repository.ToppingRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CartFood extends BaseObservable {
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private Product product;
    @Bindable
    private int quantity;
    //size id
    private String size;
    //topping id join by ', '
    private String topping;
    private String note;
    @Bindable
    private double unitPrice;
    @Bindable
    private double totalPrice;

    public CartFood(Product product, String size, double unitPrice) {
        this.product = product;
        this.quantity = 1;
        this.size = size;
        this.unitPrice = unitPrice;
        this.note="";
        this.topping="";
    }
    public CartFood(CartFood cartFood){
        setProduct(cartFood.product);
        setQuantity(cartFood.getQuantity());
        setTopping(cartFood.getTopping());
        setSize(cartFood.getSize());
        setId(cartFood.getId());
        setUnitPrice(cartFood.getUnitPrice());
        setNote(cartFood.getNote());
    }
    public double getTotalPrice() {
        return unitPrice*quantity;
    }

    public void setTotalPrice(double totalPrice) {
        if(this.totalPrice!=totalPrice){
            this.totalPrice = totalPrice;
            notifyPropertyChanged(BR.totalPrice);
        }
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getSize() {
        return size;
    }
    private String sizeName;
    public String getSizeName(){
        SizeRepository.getInstance().getSizeListMutableLiveData().observeForever(new Observer<List<Size>>() {
            @Override
            public void onChanged(List<Size> sizes) {
                sizeName = sizes.stream()
                        .filter(s->s.getId().equals(size))
                        .findFirst()
                        .orElse(null)
                        .getName();

            }
        });
        return sizeName;
    }
    public String getTopping() {
        return topping;
    }

    private String toppingName;
    public String getToppingName(){
        List<String> toppings=Arrays.asList(this.topping.split(", "));
        List<String> toppingNames=new ArrayList<>();

        //TODO: fix this
        if(ToppingRepository.getInstance().getToppingListMutableLiveData().getValue()==null){
            return "hi topping";
        }
        for (Topping topping:
                ToppingRepository.getInstance().getToppingListMutableLiveData().getValue()) {
            if(toppings.contains(topping.getId().trim())){
                toppingNames.add(topping.getName());
            }
        }
        return String.join(", ", toppingNames);
    }
    public String getNote() {
        return note;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        notifyPropertyChanged(BR.quantity);
        notifyPropertyChanged(BR.totalPrice);
    }

    public void setSize(String size) {
        this.size = size;
        countUnitPrice();
        notifyPropertyChanged(BR.totalPrice);
    }

    public void setTopping(String topping) {
        this.topping = topping;
        countUnitPrice();
        notifyPropertyChanged(BR.totalPrice);
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public void countUnitPrice(){
        double unitPrice=product.getPrice();
        if(this.topping!=null && !this.topping.equals("")){
            List<String> toppings=Arrays.asList(this.topping.split(", "));
            if(ToppingRepository.getInstance().getToppingListMutableLiveData().getValue()!=null){
                for (Topping all :
                        ToppingRepository.getInstance().getToppingListMutableLiveData().getValue()) {
                    if(toppings.contains(all.getId().trim())){
                        unitPrice+=all.getPrice();
                    }
                }
            }

        }
        if(SizeRepository.getInstance().getSizeListMutableLiveData().getValue()!=null){
            for (Size s :
                    SizeRepository.getInstance().getSizeListMutableLiveData().getValue()) {
                if(s.getId().equals(getSize())){
                    unitPrice+=s.getPrice();
                }
            }
        }
        setUnitPrice(unitPrice);

        Log.d("UNIT PRICE", "countUnitPrice: "+unitPrice);
    }
}
