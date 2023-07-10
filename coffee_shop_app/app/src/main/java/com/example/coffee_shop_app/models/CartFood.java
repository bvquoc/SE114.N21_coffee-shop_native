package com.example.coffee_shop_app.models;

import android.util.Log;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.example.coffee_shop_app.BR;
import com.example.coffee_shop_app.Data;

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
    public String getSizeName(){
        //TODO: fix this
//        return product.getSizes()
//                .stream()
//                .filter(s->s.getId().equals(this.size))
//                .findFirst()
//                .orElse(null)
//                .getName();
        return "";
    }
    public String getTopping() {
        return topping;
    }
    public String getToppingName(){
        List<String> toppings=Arrays.asList(this.topping.split(", "));
        List<String> toppingNames=new ArrayList<>();
        //TODO: fix this
//        for (Topping topping:
//                product.getToppings()) {
//            if(toppings.contains(topping.getId().trim())){
//                toppingNames.add(topping.getName());
//            }
//        }
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
        List<String> toppingsName=Arrays.asList(this.topping.split(", "));
        //TODO: fix this
//        for (Topping topping:
//             product.getToppings()) {
//            if(toppingsName.contains(topping.getId().trim())){
//                unitPrice+=topping.getPrice();
//            }
//        }
        //TODO: fix this
//        for (Size size :
//                product.getSizes()) {
//            if(size.getId().equals(this.getSize())){
//                unitPrice+=size.getPrice();
//            }
//        }
        setUnitPrice(unitPrice);

        Log.d("UNIT PRICE", "countUnitPrice: "+unitPrice);
    }
}
