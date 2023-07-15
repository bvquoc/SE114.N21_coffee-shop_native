package com.example.coffee_shop_staff_admin.repositories;

import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.coffee_shop_staff_admin.models.Size;
import com.example.coffee_shop_staff_admin.utils.interfaces.UpdateDataListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SizeRepository {
    private static final String TAG = "SizeRepository";

    //singleton
    private static SizeRepository instance;
    private SizeRepository() {
        sizeListMutableLiveData = new MutableLiveData<>();
        //define firestore
        firestore = FirebaseFirestore.getInstance();
        //define storage
        storageRef = FirebaseStorage.getInstance().getReference();
    }
    public static synchronized SizeRepository getInstance() {
        if (instance == null) {
            instance = new SizeRepository();
        }
        return instance;
    }

    //properties
    private final MutableLiveData<List<Size>> sizeListMutableLiveData;
    private final FirebaseFirestore firestore;
    private final StorageReference storageRef;

    //Function
    public MutableLiveData<List<Size>> getSizeListMutableLiveData() {
        if(sizeListMutableLiveData.getValue() == null)
        {
            registerSnapshotListener();
        }
        return sizeListMutableLiveData;
    }
    void registerSnapshotListener()
    {
        firestore.collection("Size").addSnapshotListener((value, error) -> {
            Log.d(TAG, "get sizes started.");
            if(value!=null)
            {
                getSize(value);
            }
            Log.d(TAG, "get sizes finishes.");
        });
    }
    void getSize(QuerySnapshot value)
    {
        List<Size> sizeList = new ArrayList<>();
        for (QueryDocumentSnapshot doc : value) {
            if (doc != null) {
                sizeList.add(Size.fromFireBase(doc));
            }
        }
        sizeList.sort(Comparator.comparingDouble(Size::getPrice));
        sizeListMutableLiveData.postValue(sizeList);
    }
    public void updateSize(Size size, UpdateDataListener listener)
    {
        DocumentReference sizeRef = firestore.collection("Size").document(size.getId());
        Map<String, Object> newData = new HashMap<>();
        Uri uriSize = Uri.parse(size.getImage());
        String scheme = uriSize.getScheme();
        if (scheme != null) {
            if (scheme.equals("https") || scheme.equals("gs")) {
                //The image is still from firebase
                newData.put("name", size.getName());
                newData.put("price", size.getPrice());
                newData.put("image", size.getImage());
                sizeRef.update(newData)
                        .addOnSuccessListener(aVoid -> listener.onUpdateData(true, ""))
                        .addOnFailureListener(e -> listener.onUpdateData(false, e.getMessage()));
            }else {
                //The image is from phone
                String imageId = UUID.randomUUID().toString().replace("-", "");
                StorageReference imageRef = storageRef.child("size/" + imageId);
                UploadTask uploadTask = imageRef.putFile(uriSize);
                uploadTask.addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String downloadUrl = uri.toString();
                    newData.put("name", size.getName());
                    newData.put("price", size.getPrice());
                    newData.put("image", downloadUrl);
                    sizeRef.update(newData)
                            .addOnSuccessListener(aVoid -> listener.onUpdateData(true, ""))
                            .addOnFailureListener(e -> listener.onUpdateData(false, e.getMessage()));

                }).addOnFailureListener(exception -> {
                    Log.e(TAG, "Failed to get the download URL");
                    listener.onUpdateData(false, exception.getMessage());
                })).addOnFailureListener(exception -> {
                    Log.e(TAG, "Failed to upload the image");
                    listener.onUpdateData(false, exception.getMessage());
                });
            }
        } else {
            Log.e(TAG, "The URI does not have a scheme or is invalid");
            listener.onUpdateData(false, "The URI does not have a scheme or is invalid");
        }
    }

    public void insertSize(Size size, UpdateDataListener listener)
    {
        CollectionReference collectionSizeRef = firestore.collection("Size");
        Map<String, Object> newData = new HashMap<>();
        String imageId = UUID.randomUUID().toString().replace("-", "");
        StorageReference imageRef = storageRef.child("size/" + imageId);
        Uri uriSize = Uri.parse(size.getImage());
        UploadTask uploadTask = imageRef.putFile(uriSize);
        uploadTask.addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    String downloadUrl = uri.toString();
                    newData.put("name", size.getName());
                    newData.put("price", size.getPrice());
                    newData.put("image", downloadUrl);
                    collectionSizeRef.add(newData)
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
    public void deleteSize(String sizeId, UpdateDataListener listener)
    {
        DocumentReference sizeRef = firestore.collection("Size").document(sizeId);
        sizeRef.delete()
                .addOnSuccessListener(taskSnapshot -> listener.onUpdateData(true, ""))
                .addOnFailureListener(exception -> listener.onUpdateData(false, exception.getMessage()));
    }
}
