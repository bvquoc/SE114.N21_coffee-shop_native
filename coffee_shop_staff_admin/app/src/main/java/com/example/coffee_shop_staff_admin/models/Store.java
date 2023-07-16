package com.example.coffee_shop_staff_admin.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Store {
    private String id;
    private String shortName;
    private MLocation address;
    private String phone;
    private Date timeOpen;
    private Date timeClose;
    private List<String> images;
    private Map<String, List<String>> stateFood;
    private List<String> stateTopping;

    public Store(String id, String shortName, MLocation address, String phone, Date timeOpen, Date timeClose, List<String> images, Map<String, List<String>> stateFood, List<String> stateTopping) {
        this.id = id;
        this.shortName = shortName;
        this.address = address;
        this.phone = phone;
        this.timeOpen = timeOpen;
        this.timeClose = timeClose;
        this.images = images;
        this.stateFood = stateFood;
        this.stateTopping = stateTopping;
    }

    public String getId() {
        return id;
    }

    public String getShortName() {
        return shortName;
    }

    public MLocation getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public Date getTimeOpen() {
        return timeOpen;
    }

    public Date getTimeClose() {
        return timeClose;
    }

    public List<String> getImages() {
        return images;
    }

    public Map<String, List<String>> getStateFood() {
        return stateFood;
    }

    public List<String> getStateTopping() {
        return stateTopping;
    }

    public static Store fromFireBase(DocumentSnapshot doc)
    {
        String id = doc.getId();

        Map<String, Object> map = doc.getData();
        String shortName = map.get("shortName").toString();

        String formattedAddress = ((Map<String, Object>)map.get("address")).get("formattedAddress").toString();
        double lat = (double) ((Map<String, Object>)map.get("address")).get("lat");
        double lng = (double) ((Map<String, Object>)map.get("address")).get("lng");
        MLocation address = new MLocation(formattedAddress, lat, lng);

        String phone = map.get("phone").toString();

        Date timeOpen = ((Timestamp)map.get("timeOpen")).toDate();

        Date timeClose = ((Timestamp)map.get("timeClose")).toDate();

        List<String> images = (List<String>)map.get("images");

        Map<String, List<String>> stateFoods = new HashMap<String, List<String>>();
        Map<String, Object> sizeFireStores = (Map<String, Object>)map.get("stateFood");
        sizeFireStores.forEach((key, value) -> {
            if(value instanceof Boolean)
            {
                stateFoods.put(key, new ArrayList<>());
            }
            else
            {
                stateFoods.put(key, (List<String>) value);
            }
        });

        List<String> stateToppings = (List<String>)map.get("stateToppings");
        return new Store(id, shortName, address, phone, timeOpen, timeClose, images, stateFoods, stateToppings);
    }
}
