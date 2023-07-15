package com.example.coffee_shop_staff_admin.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Date;
import java.util.Map;

public class User {
    private final String id;
    private final String name;
    private final String avatarUrl;
    private final String coverUrl;
    private final Date dob;
    private final String email;
    private final String phoneNumber;
    private boolean isActive;
    private boolean isAdmin;
    private boolean isStaff;
    private String store;

    public User(String id, String name, String avatarUrl, String coverUrl, Date dob, String email,
                String phoneNumber, boolean isActive, boolean isAdmin, boolean isStaff, String store) {
        this.id = id;
        this.name = name;
        this.avatarUrl = avatarUrl;
        this.coverUrl = coverUrl;
        this.dob = dob;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.isActive = isActive;
        this.isAdmin = isAdmin;
        this.isStaff = isStaff;
        this.store = store;
    }

    public String getId()
    {
        return id;
    }
    public String getName() {
        return name;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public Date getDob() {
        return dob;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public boolean isActive() {
        return isActive;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public boolean isStaff() {
        return isStaff;
    }

    public String getStore() {
        return store;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public void setStaff(boolean staff) {
        isStaff = staff;
    }

    public void setStore(String store) {
        this.store = store;
    }
    public static User fromFireBase(DocumentSnapshot doc)
    {
        String id = doc.getId();
        Map<String, Object> map = doc.getData();
        if(map!=null)
        {
            String name = nullableConvertString(map.get("name"));
            String email = nullableConvertString(map.get("email"));
            String avatarUrl = nullableConvertString(map.get("avatarUrl"));
            String coverUrl = nullableConvertString(map.get("coverUrl"));
            String store = nullableConvertString(map.get("store"));
            String phoneNumber = nullableConvertString(map.get("phoneNumber"));
            Date dob = nullableConvertDate(map.get("dob"));
            boolean isActive = nullableConvertBoolean(map.get("isActive"));
            boolean isAdmin = nullableConvertBoolean(map.get("isAdmin"));
            boolean isStaff = nullableConvertBoolean(map.get("isStaff"));
            return new User(id, name, avatarUrl, coverUrl, dob, email, phoneNumber, isActive, isAdmin, isStaff, store);
        }
        return null;
    }
    private static String nullableConvertString(Object object)
    {
        try
        {
            return object.toString();
        }
        catch (Exception e)
        {
            return "";
        }
    }
    private static boolean nullableConvertBoolean(Object object)
    {
        try
        {
            return (boolean)object;
        }
        catch (Exception e)
        {
            return false;
        }
    }
    private static Date nullableConvertDate(Object date)
    {
        try
        {
            return ((Timestamp)date).toDate();
        }
        catch (Exception e)
        {
            return null;
        }
    }
}
