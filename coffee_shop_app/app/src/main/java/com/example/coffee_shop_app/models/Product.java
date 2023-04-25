package com.example.coffee_shop_app.models;

import java.util.Date;

public class Product {
    private String id;
    private  String name;
    private double price;
    private String image;
    private boolean isAvailable;
    private Date dateRegister;

    public Product(String id, String name, double price, String image, boolean isAvailable, Date dateRegister) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.image = image;
        this.isAvailable = isAvailable;
        this.dateRegister = dateRegister;
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

    public boolean isAvailable() {
        return isAvailable;
    }

    public Date getDateRegister() {
        return dateRegister;
    }
}
