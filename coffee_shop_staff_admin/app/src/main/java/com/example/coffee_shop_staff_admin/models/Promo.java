package com.example.coffee_shop_staff_admin.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class Promo {
    private final String promoCode;
    private final Date dateEnd;
    private final Date dateStart;
    private final String description;
    private final boolean isForNewCustomer;
    private final double maxPrice;
    private final double minPrice;
    private final double percent;
    private final List<String> stores;

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
}