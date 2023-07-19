package com.example.coffee_shop_staff_admin.models;

import java.io.Serializable;

public class StoreProduct<T> implements Serializable {
    public T getProduct() {
        return product;
    }

    public void setProduct(T product) {
        this.product = product;
    }

    public Boolean getStocking() {
        return isStocking;
    }

    public void setStocking(Boolean stocking) {
        isStocking = stocking;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    T product;
    Boolean isStocking;
    String store;

    public StoreProduct(T product, Boolean isStocking, String store){
        this.product = product;
        this.isStocking = isStocking;
        this.store = store;
    }
}
