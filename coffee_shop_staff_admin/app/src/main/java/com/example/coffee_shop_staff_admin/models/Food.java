package com.example.coffee_shop_staff_admin.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class Food {
    private String id;
    private String name;
    private double price;
    private String description;
    private List<String> images;
    private List<String> sizes;
    private List<String> toppings;

    public Food(String id, String name, double price, String description, List<String> images, List<String> sizes, List<String> toppings) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.images = images;
        this.sizes = sizes;
        this.toppings = toppings;
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

    public String getDescription() {
        return description;
    }

    public List<String> getImages() {
        return images;
    }

    public List<String> getSizes() {
        return sizes;
    }

    public List<String> getToppings() {
        return toppings;
    }

    public static Food fromFireBase(QueryDocumentSnapshot doc)
    {
        String id = doc.getId();
        Map<String, Object> map = doc.getData();
        String name = map.get("name").toString();
        double price = ((Number) map.get("price")).doubleValue();
        String description = map.get("description").toString();
        List<String> images = (List<String>)map.get("images");
        List<String> sizes = (List<String>)map.get("sizes");
        List<String> toppings = (List<String>)map.get("toppings");
        return new Food(id, name, price, description, images, sizes,toppings);
    }
}
