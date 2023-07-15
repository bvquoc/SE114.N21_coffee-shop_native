package com.example.coffee_shop_staff_admin.repositories;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.coffee_shop_staff_admin.models.Store;
import com.example.coffee_shop_staff_admin.models.Topping;
import com.example.coffee_shop_staff_admin.utils.interfaces.UpdateDataListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
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

public class StoreRepository {
    private static final String TAG = "StoreRepository";

    //singleton
    private static StoreRepository instance;
    private StoreRepository() {
        storeListMutableLiveData = new MutableLiveData<>();
        //define firestore
        fireStore = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();
    }
    public static synchronized StoreRepository getInstance() {
        if (instance == null) {
            instance = new StoreRepository();
        }
        return instance;
    }

    //properties
    private final MutableLiveData<List<Store>> storeListMutableLiveData;
    private final FirebaseFirestore fireStore;

    //get stores from firebase firestore
    private final StorageReference storageRef;
    public MutableLiveData<List<Store>> getStoreListMutableLiveData() {
        if(storeListMutableLiveData.getValue() == null)
        {
            registerSnapshotListener();
        }
        return storeListMutableLiveData;
    }
    public void registerSnapshotListener()
    {
        fireStore.collection("Store").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                Log.d(TAG, "get stores started.");
                getStore(value);
                Log.d(TAG, "get stores finishes.");
            }
        });
    }
    void getStore(QuerySnapshot value)
    {
        List<Store> storeList = new ArrayList<>();
        for (QueryDocumentSnapshot doc : value) {
            if (doc != null) {
                storeList.add(Store.fromFireBase(doc));
            }
        }

        storeList.sort(new Comparator<Store>() {
            @Override
            public int compare(Store o1, Store o2) {
                return o1.getShortName().compareTo(o2.getShortName());
            }
        });

        storeListMutableLiveData.postValue(storeList);
    }
    public void updateStoreWithoutUpdatingState(Store store, UpdateDataListener listener)
    {
        DocumentReference storeRef = fireStore.collection("Store").document(store.getId());
        Map<String, Object> newData = new HashMap<>();
        newData.put("shortName", store.getShortName());
        newData.put("phone", store.getPhone());
        Map<String, Object> address = new HashMap<>();
        address.put("formattedAddress", store.getAddress().getFormattedAddress());
        address.put("lat", store.getAddress().getLat());
        address.put("lng", store.getAddress().getLng());
        newData.put("address", address);
        newData.put("timeOpen", store.getTimeOpen());
        newData.put("timeClose", store.getTimeClose());

        List<String> images = new ArrayList<String>();
        int amountImage = store.getImages().size();
        for (String image: store.getImages()) {
            Uri uriStore = Uri.parse(image);
            String scheme = uriStore.getScheme();
            if (scheme != null) {
                if (scheme.equals("https") || scheme.equals("gs")) {
                    //The image is still from firebase
                    images.add(image);
                    if (images.size() == amountImage) {
                        newData.put("images", images);
                        storeRef.update(newData)
                                .addOnSuccessListener(unused -> listener.onUpdateData(true))
                                .addOnFailureListener(e -> listener.onUpdateData(false));
                    }
                } else {
                    //The image is from phone
                    String imageId = UUID.randomUUID().toString().replace("-", "");
                    StorageReference imageRef = storageRef.child("stores/" + imageId);
                    UploadTask uploadTask = imageRef.putFile(uriStore);
                    uploadTask.addOnSuccessListener(taskSnapshot -> {
                        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            images.add(uri.toString());
                            if (images.size() == amountImage) {
                                newData.put("images", images);
                                storeRef.update(newData)
                                        .addOnSuccessListener(unused -> listener.onUpdateData(true))
                                        .addOnFailureListener(e -> listener.onUpdateData(false));
                            }
                        }).addOnFailureListener(exception -> {
                            Log.e(TAG, "Failed to get the download URL");
                            listener.onUpdateData(false);
                        });

                    }).addOnFailureListener(exception -> {
                        Log.e(TAG, "Failed to upload the image");
                        listener.onUpdateData(false);
                    });
                }
            } else {
                Log.e(TAG, "The URI does not have a scheme or is invalid");
                listener.onUpdateData(false);
            }
        }
    }

    public void insertStore(Store store, UpdateDataListener listener)
    {
        CollectionReference collectionStoreRef = fireStore.collection("Store");
        Map<String, Object> newData = new HashMap<>();
        newData.put("shortName", store.getShortName());
        newData.put("phone", store.getPhone());
        Map<String, Object> address = new HashMap<>();
        address.put("formattedAddress", store.getAddress().getFormattedAddress());
        address.put("lat", store.getAddress().getLat());
        address.put("lng", store.getAddress().getLng());
        newData.put("address", address);
        newData.put("timeOpen", store.getTimeOpen());
        newData.put("timeClose", store.getTimeClose());
        newData.put("stateFood", new HashMap<>());
        newData.put("stateTopping", new ArrayList<>());

        List<String> images = new ArrayList<String>();
        int amountImage = store.getImages().size();
        for (String image: store.getImages()) {
            Uri uriStore = Uri.parse(image);

            String imageId = UUID.randomUUID().toString().replace("-", "");
            StorageReference imageRef = storageRef.child("stores/" + imageId);
            UploadTask uploadTask = imageRef.putFile(uriStore);
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    images.add(uri.toString());
                    if (images.size() == amountImage) {
                        newData.put("images", images);
                        collectionStoreRef.add(newData)
                                .addOnSuccessListener(unused -> listener.onUpdateData(true))
                                .addOnFailureListener(e -> listener.onUpdateData(false));
                    }
                }).addOnFailureListener(exception -> {
                    Log.e(TAG, "Failed to get the download URL");
                    listener.onUpdateData(false);
                });

            }).addOnFailureListener(exception -> {
                Log.e(TAG, "Failed to upload the image");
                listener.onUpdateData(false);
            });
        }
    }
    public void deleteStore(String storeId, UpdateDataListener listener)
    {
        DocumentReference storeRef = fireStore.collection("Store").document(storeId);
        storeRef.delete().addOnSuccessListener(taskSnapshot -> {
            listener.onUpdateData(true);
        }).addOnFailureListener(exception -> {
            listener.onUpdateData(false);
        });
    }
}
