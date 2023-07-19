package com.example.coffee_shop_staff_admin.models;

import java.util.Date;

public class Notification {
    private final String id;
    private final String title;
    private final String content;
    private final String image;
    private final Date dateNoti;

    public Notification(String id, String title, String content, String image, Date dateNoti) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.image = image;
        this.dateNoti = dateNoti;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getImage() {
        return image;
    }

    public Date getDateNoti() {
        return dateNoti;
    }
}
