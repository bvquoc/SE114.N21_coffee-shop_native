package com.example.coffee_shop_app.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class User {
    String id;
    String name;
    String phoneNumber;
    String email;
    Date dob;
    Boolean isActive;
    Boolean isAdmin;
    Boolean isStaff;
    String avatarUrl;
    String coverUrl;
    Date createDate;

    public User(String id,
                String name,
                String phoneNumber,
                String email,
                Date dob,
                Boolean isActive,
                Boolean isAdmin,
                Boolean isStaff,
                String avatarUrl,
                String coverUrl,
                Date createDate) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.dob = dob;
        this.isActive = isActive;
        this.isAdmin = isAdmin;
        this.isStaff = isStaff;
        this.avatarUrl = avatarUrl;
        this.coverUrl = coverUrl;
        this.createDate = createDate;
    }

    @Nullable
    public static User fromFireStore(DocumentSnapshot doc) {
        Map<String, Object> data = doc.getData();
        if (data == null) {
            return null;
        }
        if (data.get("isActive") == null || !((Boolean) data.get("isActive"))) return null;
        Date dob, createDate;
        try {
            dob = ((Timestamp) data.get("dob")).toDate();
        } catch (Exception e) {
            dob = DateTime.parse("9/9/1900").toDate();
        }
        try {
            createDate = ((Timestamp) data.get("createDate")).toDate();
        } catch (Exception e) {
            createDate = DateTime.now().toDate();
        }
        return new User(
                doc.getId(),
                data.get("name").toString(),
                data.get("phoneNumber").toString(),
                data.get("email").toString(),
                dob,
                (Boolean) data.get("isActive"),
                (Boolean) data.get("isAdmin"),
                (Boolean) data.get("isStaff"),
                data.get("avatarUrl").toString(),
                data.get("coverUrl").toString(),
                createDate
        );
    }

    public static Map<String, Object> toFireStore(User user){
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("name", user.name);
        data.put("isActive", user.isActive);
        data.put("email", user.email);
        data.put("dob", user.dob);
        data.put("avatarUrl", user.avatarUrl);
        data.put("coverUrl", user.coverUrl);
        data.put("phoneNumber", user.phoneNumber);
        return  data;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public Boolean getAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

    public Boolean getStaff() {
        return isStaff;
    }

    public void setStaff(Boolean staff) {
        isStaff = staff;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
}
