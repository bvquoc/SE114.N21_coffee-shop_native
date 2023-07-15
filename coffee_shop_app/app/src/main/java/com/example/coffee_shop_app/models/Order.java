package com.example.coffee_shop_app.models;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Order {
    String id;
    Store store;
    Double total;
    Double deliveryCost;
    Double discount;

    Double totalProduct;
    Date dateOrder;
    List<OrderFood> products;
    AddressDelivery address;
    String status;
    Date pickupTime;

    public Order(Date dateOrder, List<OrderFood> products, String status) {
        this.dateOrder = dateOrder;
        this.products = products;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public Double getDeliveryCost() {
        return deliveryCost;
    }

    public void setDeliveryCost(Double deliveryCost) {
        this.deliveryCost = deliveryCost;
    }

    public Double getTotalProduct() {
        return totalProduct;
    }

    public void setTotalProduct(Double totalProduct) {
        this.totalProduct = totalProduct;
    }

    public Date getDateOrder() {
        return dateOrder;
    }

    public void setDateOrder(Date dateOrder) {
        this.dateOrder = dateOrder;
    }

    public List<OrderFood> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<OrderFood> products) {
        this.products = products;
    }

    public AddressDelivery getAddress() {
        return address;
    }

    public void setAddress(AddressDelivery address) {
        this.address = address;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getPickupTime() {
        return pickupTime;
    }

    public void setPickupTime(Date pickupTime) {
        this.pickupTime = pickupTime;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }
}
