package com.example.coffee_shop_app.models;

import java.util.Date;
import java.util.List;

public class Product {
    private String id;
    private  String name;
    private double price;
    private String image;
    private boolean isAvailable;
    private Date dateRegister;
    private String description;
    private List<Size> sizes;
    private List<Topping> toppings;

    public List<Size> getSizes() {
        return sizes;
    }

    public void setSizes(List<Size> sizes) {
        this.sizes = sizes;
    }

    public List<Topping> getToppings() {
        return toppings;
    }

    public void setToppings(List<Topping> toppings) {
        this.toppings = toppings;
    }

    public Product(String id, String name, double price, String image, boolean isAvailable, Date dateRegister) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.image = image;
        this.isAvailable = isAvailable;
        this.dateRegister = dateRegister;
        description="Dark, rich espresso lies in wait under a smoothed and stretched layer of thick milk foam. An alchemy of barista artistry and craft.";
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
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
