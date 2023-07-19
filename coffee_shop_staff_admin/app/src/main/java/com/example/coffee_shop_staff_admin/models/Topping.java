package com.example.coffee_shop_staff_admin.models;

import android.net.Uri;

import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.Serializable;
import java.util.Map;

public class Topping implements Serializable {
    private String id;
    private String name;
    private double price;
    private String image;

    public Topping(String id, String name, double price, String image) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public String getImage() {
        return image;
    }
    public static Topping fromFireBase(QueryDocumentSnapshot doc)
    {
        String id = doc.getId();
        Map<String, Object> map = doc.getData();
        String name = map.get("name").toString();
        double price = ((Number) map.get("price")).doubleValue();
        String image = map.get("image").toString();
        return new Topping(id, name, price, image);
    }
}