package com.example.coffee_shop_app;

import com.example.coffee_shop_app.models.Address;
import com.example.coffee_shop_app.models.AddressDelivery;
import com.example.coffee_shop_app.models.Product;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Data {
    public static Data instance = new Data();
    public String userId = "aCbVUBVvGxQOTJChPDIJbQnPk4u1";
    public List<Product> products = new ArrayList<Product>();
    public List<Product> favoriteProducts =new ArrayList<Product>();
    public List<AddressDelivery> addressDeliveries =new ArrayList<AddressDelivery>();
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

        addressDeliveries.add(
                new AddressDelivery(
                        new Address(
                                "123 Xa lô Hà nội",
                                "Thủ Đức",
                                "Linh Trung",
                                "khong biet")
                        , "Nguyen Van A", "01234567890"));
        addressDeliveries.add(
                new AddressDelivery(
                        new Address(
                                "123 Xô Viết Nghệ Tỉnh",
                                "HCM",
                                "Bình Thạnh",
                                "phường 25")
                        , "Nguyen Van B", "01234567890"));
        addressDeliveries.add(
                new AddressDelivery(
                        new Address(
                                "321 Xô Viết Nghệ Tỉnh",
                                "HCM",
                                "Bình Thạnh",
                                "phường 25")
                        , "Nguyen Van C", "01234567890"));
        addressDeliveries.add(
                new AddressDelivery(
                        new Address(
                                "321 Xô Viết Nghệ Tỉnh",
                                "HCM",
                                "Bình Thạnh",
                                "phường 25")
                        , "Nguyen Van C", "01234567890"));
        addressDeliveries.add(
                new AddressDelivery(
                        new Address(
                                "321 Xô Viết Nghệ Tỉnh",
                                "HCM",
                                "Bình Thạnh",
                                "phường 25")
                        , "Nguyen Van C", "01234567890"));
        addressDeliveries.add(
                new AddressDelivery(
                        new Address(
                                "321 Xô Viết Nghệ Tỉnh",
                                "HCM",
                                "Bình Thạnh",
                                "phường 25")
                        , "Nguyen Van C", "01234567890"));
        addressDeliveries.add(
                new AddressDelivery(
                        new Address(
                                "321 Xô Viết Nghệ Tỉnh",
                                "HCM",
                                "Bình Thạnh",
                                "phường 25")
                        , "Nguyen Van C", "01234567890"));
        addressDeliveries.add(
                new AddressDelivery(
                        new Address(
                                "321 Xô Viết Nghệ Tỉnh",
                                "HCM",
                                "Bình Thạnh",
                                "phường 25")
                        , "Nguyen Van C", "01234567890"));
        addressDeliveries.add(
                new AddressDelivery(
                        new Address(
                                "321 Xô Viết Nghệ Tỉnh",
                                "HCM",
                                "Bình Thạnh",
                                "phường 25")
                        , "Nguyen Van C", "01234567890"));
        addressDeliveries.add(
                new AddressDelivery(
                        new Address(
                                "321 Xô Viết Nghệ Tỉnh",
                                "HCM",
                                "Bình Thạnh",
                                "phường 25")
                        , "Nguyen Van C", "01234567890"));

        addressDeliveries.add(
                new AddressDelivery(
                        new Address(
                                "321 Xô Viết Nghệ Tỉnh",
                                "HCM",
                                "Bình Thạnh",
                                "phường 25")
                        , "Nguyen Van C", "01234567890"));
        addressDeliveries.add(
                new AddressDelivery(
                        new Address(
                                "321 Xô Viết Nghệ Tỉnh",
                                "HCM",
                                "Bình Thạnh",
                                "phường 25")
                        , "Nguyen Van C", "01234567890"));
        addressDeliveries.add(
                new AddressDelivery(
                        new Address(
                                "321 Xô Viết Nghệ Tỉnh",
                                "HCM",
                                "Bình Thạnh",
                                "phường 25")
                        , "Nguyen Van C", "01234567890"));
        addressDeliveries.add(
                new AddressDelivery(
                        new Address(
                                "321 Xô Viết Nghệ Tỉnh",
                                "HCM",
                                "Bình Thạnh",
                                "phường 25")
                        , "Nguyen Van C", "01234567890"));
        addressDeliveries.add(
                new AddressDelivery(
                        new Address(
                                "321 Xô Viết Nghệ Tỉnh",
                                "HCM",
                                "Bình Thạnh",
                                "phường 25")
                        , "Nguyen Van C", "01234567890"));
        addressDeliveries.add(
                new AddressDelivery(
                        new Address(
                                "321 Xô Viết Nghệ Tỉnh",
                                "HCM",
                                "Bình Thạnh",
                                "phường 25")
                        , "Nguyen Van C", "01234567890"));
    }
}
