package com.example.coffee_shop_app.models;

import android.util.Log;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.example.coffee_shop_app.BR;

import java.util.Arrays;
import java.util.List;

public class CartFood extends BaseObservable {
    private Product product;
    @Bindable
    private int quantity;
    private String size;
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

    public String getTopping() {
        return topping;
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
        for (Topping topping:
             product.getToppings()) {
            if(toppingsName.contains(topping.getName().trim())){
                unitPrice+=topping.getPrice();
            }
        }

        for (Size size :
                product.getSizes()) {
            if(size.getId().equals(this.getSize())){
                unitPrice+=size.getPrice();
            }
        }
        setUnitPrice(unitPrice);

        Log.d("UNIT PRICE", "countUnitPrice: "+unitPrice);
    }
}
