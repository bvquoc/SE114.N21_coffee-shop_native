package com.example.coffee_shop_app.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class Size {
    private String id;
    private String name;
    private double price;
    private String image;

    public Size(String id, String name, double price, String image) {
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

    public static Size fromFireBase(QueryDocumentSnapshot doc)
    {
        String id = doc.getId();
        Map<String, Object> map = doc.getData();
        String name = map.get("name").toString();
        double price = ((Number) map.get("price")).doubleValue();
        String image = map.get("image").toString();
        return new Size(id, name, price, image);
    }
}
