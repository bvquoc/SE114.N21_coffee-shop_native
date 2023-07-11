package com.example.coffee_shop_app.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class Promo {
    private String promoCode;
    private Date dateEnd;
    private Date dateStart;
    private String description;
    private boolean isForNewCustomer;
    private double maxPrice;
    private double minPrice;
    private double percent;
    private List<String> stores;

    public Promo(String promoCode, Date dateEnd, Date dateStart, String description, boolean isForNewCustomer, double maxPrice, double minPrice, double percent, List<String> stores) {
        this.promoCode = promoCode;
        this.dateEnd = dateEnd;
        this.dateStart = dateStart;
        this.description = description;
        this.isForNewCustomer = isForNewCustomer;
        this.maxPrice = maxPrice;
        this.minPrice = minPrice;
        this.percent = percent;
        this.stores = stores;
    }

    public String getPromoCode() {
        return promoCode;
    }

    public Date getDateEnd() {
        return dateEnd;
    }

    public Date getDateStart() {
        return dateStart;
    }

    public String getDescription() {
        return description;
    }

    public boolean isForNewCustomer() {
        return isForNewCustomer;
    }

    public double getMaxPrice() {
        return maxPrice;
    }

    public double getMinPrice() {
        return minPrice;
    }

    public double getPercent() {
        return percent;
    }

    public List<String> getStores() {
        return stores;
    }

    public static Promo fromFireBase(QueryDocumentSnapshot doc)
    {
        String promoCode = doc.getId();
        Map<String, Object> map = doc.getData();

        Date dateEnd = ((Timestamp)map.get("dateEnd")).toDate();
        Date dateStart = ((Timestamp)map.get("dateStart")).toDate();
        String description = map.get("description").toString();
        boolean isForNewCustomer = (boolean) map.get("forNewCustomer");
        double maxPrice = ((Number) map.get("maxPrice")).doubleValue();
        double minPrice = ((Number) map.get("minPrice")).doubleValue();
        double percent = ((Number) map.get("percent")).doubleValue();
        List<String> stores = (List<String>)map.get("stores");
        return new Promo(promoCode, dateEnd, dateStart, description, isForNewCustomer, maxPrice, minPrice, percent, stores);
    }

    public String getImage()
    {
        if(isForNewCustomer)
        {
            return "https://firebasestorage.googleapis.com/v0/b/coffee-shop-app-437c7.appspot.com/o/promo%2FNewCustomer.png?alt=media&token=bd0fde1b-76ce-4a1d-88f7-b9536aeeb767&_gl=1*1e6ij5z*_ga*NjUyMDc4ODQxLjE2Nzc2NjE1OTI.*_ga_CW55HF8NVT*MTY4NTY0Mjk4Mi4yNy4xLjE2ODU2NDMwOTUuMC4wLjA.";
        } else {
            return "https://firebasestorage.googleapis.com/v0/b/coffee-shop-app-437c7.appspot.com/o/promo%2FAll.png?alt=media&token=c16c732d-5eab-48bc-b111-c84ff835dc00&_gl=1*dqk2qm*_ga*ODY4OTcyNTgyLjE2ODU2OTgxMTU.*_ga_CW55HF8NVT*MTY4NTgzMzY0My4xNi4xLjE2ODU4MzQwMDcuMC4wLjA.";
        }
    }
}