package com.example.coffee_shop_staff_admin.repositories;

import android.net.Uri;
import android.util.Log;


import com.example.coffee_shop_staff_admin.models.Notification;
import com.example.coffee_shop_staff_admin.utils.interfaces.UpdateDataListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NotificationRepository {
    private static final String TAG = "NotificationRepository";
    //singleton
    private static NotificationRepository instance;
    private NotificationRepository() {
        //define fireStore
        fireStore = FirebaseFirestore.getInstance();
        //define storage
        storageRef = FirebaseStorage.getInstance().getReference();
    }
    public static synchronized NotificationRepository getInstance() {
        if (instance == null) {
            instance = new NotificationRepository();
        }
        return instance;
    }

    //properties
    private final FirebaseFirestore fireStore;
    private final StorageReference storageRef;

    //Function
    public void insertNotification(Notification notification, UpdateDataListener listener)
    {
        CollectionReference collectionNotificationRef = fireStore.collection("Notification");
        Map<String, Object> newData = new HashMap<>();
        String imageId = UUID.randomUUID().toString().replace("-", "");
        StorageReference imageRef = storageRef.child("notifications/" + imageId);
        Uri uriNotification = Uri.parse(notification.getImage());
        UploadTask uploadTask = imageRef.putFile(uriNotification);
        uploadTask.addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    String downloadUrl = uri.toString();
                    newData.put("title", notification.getTitle());
                    newData.put("content", notification.getContent());
                    newData.put("image", downloadUrl);
                    newData.put("dateNoti", new Date());
                    collectionNotificationRef.add(newData)
                            .addOnSuccessListener(documentReference -> listener.onUpdateData(true, ""))
                            .addOnFailureListener(exception -> listener.onUpdateData(false, exception.getMessage()));

                }).addOnFailureListener(exception -> {
                    Log.e(TAG, "Failed to get the download URL");
                    listener.onUpdateData(false, exception.getMessage());
                })).addOnFailureListener(exception -> {
            Log.e(TAG, "Failed to upload the image");
            listener.onUpdateData(false, exception.getMessage());
        });
    }
}
