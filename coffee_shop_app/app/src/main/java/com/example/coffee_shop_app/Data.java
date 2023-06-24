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
    public String userId = "aCbVUBVvGxQOTJChPDIJbQnPk4u1";
    public List<Product> products = new ArrayList<Product>();
    public List<Product> favoriteProducts =new ArrayList<Product>();
    public List<Size> sizes = new ArrayList<>();

    public List<Topping> toppings = new ArrayList<>();

    private Data() {
        products.add(new Product("product1", "Pure Black 1", 59000, "https://haycafe.vn/wp-content/uploads/2022/03/Hinh-anh-do-an-anime-dep-cute.jpg", true, new Date(2023 - 1900, 3 - 1, 20)));
        products.add(new Product("product2", "Pure Black 2", 59000, "https://haycafe.vn/wp-content/uploads/2022/03/Hinh-anh-do-an-anime-dep-cute.jpg", true, new Date(2023 - 1900, 4 - 1, 10)));
        products.add(new Product("product3", "Pure Black 3", 59000, "https://haycafe.vn/wp-content/uploads/2022/03/Hinh-anh-do-an-anime-dep-cute.jpg", false, new Date(2023 - 1900, 3 - 1, 20)));
        products.add(new Product("product4", "Pure Black 4", 59000, "https://haycafe.vn/wp-content/uploads/2022/03/Hinh-anh-do-an-anime-dep-cute.jpg", false, new Date(2023 - 1900, 4 - 1, 10)));
        products.add(new Product("product5", "Pure Black 5", 59000, "https://haycafe.vn/wp-content/uploads/2022/03/Hinh-anh-do-an-anime-dep-cute.jpg", true, new Date(2023 - 1900, 4 - 1, 1)));
        products.add(new Product("product6", "Pure Black 6", 59000, "https://haycafe.vn/wp-content/uploads/2022/03/Hinh-anh-do-an-anime-dep-cute.jpg", true, new Date(2023 - 1900, 4 - 1, 1)));
        products.add(new Product("product7", "Pure Black 7", 59000, "https://haycafe.vn/wp-content/uploads/2022/03/Hinh-anh-do-an-anime-dep-cute.jpg", true, new Date(2023 - 1900, 4 - 1, 1)));
        products.add(new Product("product8", "Pure Black 8", 59000, "https://haycafe.vn/wp-content/uploads/2022/03/Hinh-anh-do-an-anime-dep-cute.jpg", true, new Date(2023 - 1900, 4 - 1, 1)));

        favoriteProducts.add(new Product("product1", "Pure Black 1", 59000, "https://haycafe.vn/wp-content/uploads/2022/03/Hinh-anh-do-an-anime-dep-cute.jpg", true, new Date(2023 - 1900, 3 - 1, 20)));
        favoriteProducts.add(new Product("product2", "Pure Black 2", 59000, "https://haycafe.vn/wp-content/uploads/2022/03/Hinh-anh-do-an-anime-dep-cute.jpg", true, new Date(2023 - 1900, 4 - 1, 10)));
        favoriteProducts.add(new Product("product3", "Pure Black 3", 59000, "https://haycafe.vn/wp-content/uploads/2022/03/Hinh-anh-do-an-anime-dep-cute.jpg", false, new Date(2023 - 1900, 3 - 1, 20)));
        favoriteProducts.add(new Product("product4", "Pure Black 4", 59000, "https://haycafe.vn/wp-content/uploads/2022/03/Hinh-anh-do-an-anime-dep-cute.jpg", false, new Date(2023 - 1900, 4 - 1, 10)));

        sizes.add(new Size("1", "Small", 0, "https://product.hstatic.net/1000075078/product/chocolatenong_949029_c1932e1298a841e18537713220be2333_large.jpg"));
        sizes.add(new Size("2", "Large", 10000, "https://product.hstatic.net/1000075078/product/chocolatenong_949029_c1932e1298a841e18537713220be2333_large.jpg"));
        toppings.add(new Topping("1", "Cheese", 5000, "https://product.hstatic.net/1000075078/product/chocolatenong_949029_c1932e1298a841e18537713220be2333_large.jpg"));
        toppings.add(new Topping("2", "Espresso (1 shot)", 10000, "https://product.hstatic.net/1000075078/product/chocolatenong_949029_c1932e1298a841e18537713220be2333_large.jpg"));

        for (Product prd :
                products) {
            prd.setToppings(toppings);
            prd.setSizes(sizes);
        }
    }
}
