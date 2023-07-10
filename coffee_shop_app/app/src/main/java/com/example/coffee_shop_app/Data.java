package com.example.coffee_shop_app;

import com.example.coffee_shop_app.models.Address;
import com.example.coffee_shop_app.models.AddressDelivery;
import com.example.coffee_shop_app.models.Product;
import com.example.coffee_shop_app.models.Size;
import com.example.coffee_shop_app.models.Topping;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Data {
    public static Data instance = new Data();
    public String userId = "5BPFiFycnWOJFv9OutOX11gXqHw2";
    public List<Product> products = new ArrayList<Product>();
    public List<Product> favoriteProducts =new ArrayList<Product>();
    public List<Size> sizes = new ArrayList<>();

    public List<Topping> toppings = new ArrayList<>();

    private Data() {
    }
}
