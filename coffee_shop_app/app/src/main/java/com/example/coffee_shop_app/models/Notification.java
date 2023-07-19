package com.example.coffee_shop_app.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Date;
import java.util.Map;

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

    public static Notification fromFireBase(QueryDocumentSnapshot doc)
    {
        String id = doc.getId();
        Map<String, Object> map = doc.getData();
        String title = map.get("title").toString();
        String content = map.get("content").toString();
        String image = map.get("image").toString();
        Date dateNoti = ((Timestamp)map.get("dateNoti")).toDate();
        return new Notification(id, title, content, image, dateNoti);
    }
}
