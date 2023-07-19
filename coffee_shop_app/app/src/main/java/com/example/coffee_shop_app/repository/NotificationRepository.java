package com.example.coffee_shop_app.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.coffee_shop_app.models.Notification;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;
import java.util.List;
public class NotificationRepository {
    private static final String TAG = "NotificationRepository";
    //singleton
    private static NotificationRepository instance;
    private NotificationRepository() {
        notificationListMutableLiveData = new MutableLiveData<>();
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
    private final MutableLiveData<List<Notification>> notificationListMutableLiveData;
    private final FirebaseFirestore fireStore;
    private final StorageReference storageRef;

    //Function
    public MutableLiveData<List<Notification>> getNotificationListMutableLiveData() {
        if(notificationListMutableLiveData.getValue() == null)
        {
            registerSnapshotListener();
        }
        return notificationListMutableLiveData;
    }
    public void registerSnapshotListener()
    {
        fireStore.collection("Notification")
                .orderBy("dateNoti", Query.Direction.DESCENDING)
                .limit(20)
                .addSnapshotListener((value, error) -> {
                    Log.d(TAG, "get notifications started.");
                    if(value!=null)
                    {
                        getNotifications(value);
                    }
                    Log.d(TAG, "get notifications finishes.");
                });
    }
    void getNotifications(QuerySnapshot value)
    {
        List<Notification> notificationList = new ArrayList<>();
        for (QueryDocumentSnapshot doc : value) {
            if (doc != null) {
                notificationList.add(Notification.fromFireBase(doc));
            }
        }
        notificationListMutableLiveData.postValue(notificationList);
    }
}
