package com.example.coffee_shop_staff_admin.repositories;

import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.coffee_shop_staff_admin.models.Store;
import com.example.coffee_shop_staff_admin.models.User;
import com.example.coffee_shop_staff_admin.utils.interfaces.CallBack;
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

public class StoreRepository {
    private static final String TAG = "StoreRepository";

    //singleton
    private static StoreRepository instance;
    private StoreRepository() {
        storeListMutableLiveData = new MutableLiveData<>();
        //define fireStore
        fireStore = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();
        currentStoreLiveData = new MutableLiveData<>();
    }
    public static synchronized StoreRepository getInstance() {
        if (instance == null) {
            instance = new StoreRepository();
        }
        return instance;
    }

    //properties
    private final MutableLiveData<List<Store>> storeListMutableLiveData;

    private MutableLiveData<Store> currentStoreLiveData;

    public MutableLiveData<Store> getCurrentStore() {
        return currentStoreLiveData;
    }

    private final FirebaseFirestore fireStore;

    //get stores from firebase fireStore
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
        fireStore.collection("Store").addSnapshotListener((value, error) -> {
            Log.d(TAG, "get stores started.");
            if(value!=null) {
                getStore(value);
            }
            Log.d(TAG, "get stores finishes.");
        });
    }
    void getStore(QuerySnapshot value)
    {
        List<Store> storeList = new ArrayList<>();
        for (QueryDocumentSnapshot doc : value) {
            if (doc != null) {
                Store store = Store.fromFireBase(doc);
                storeList.add(store);
                if(currentStoreLiveData.getValue() != null && store.getId().equals(currentStoreLiveData.getValue().getId())){
                    currentStoreLiveData.postValue(store);
                }
            }
        }

        storeList.sort(Comparator.comparing(Store::getShortName));

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

        List<String> images = new ArrayList<>();
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
                                .addOnSuccessListener(unused -> listener.onUpdateData(true, ""))
                                .addOnFailureListener(e -> listener.onUpdateData(false, e.getMessage()));
                    }
                } else {
                    //The image is from phone
                    String imageId = UUID.randomUUID().toString().replace("-", "");
                    StorageReference imageRef = storageRef.child("stores/" + imageId);
                    UploadTask uploadTask = imageRef.putFile(uriStore);
                    uploadTask.addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                images.add(uri.toString());
                                if (images.size() == amountImage) {
                                    newData.put("images", images);
                                    storeRef.update(newData)
                                            .addOnSuccessListener(unused -> listener.onUpdateData(true, ""))
                                            .addOnFailureListener(e -> listener.onUpdateData(false, e.getMessage()));
                                }
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
    }

    public void updateProduct(Store store, CallBack onSuccess, CallBack onFailed){
        currentStoreLiveData.postValue(store);

        List<Store> storeList = storeListMutableLiveData.getValue();
        List<Store> newList = new ArrayList<>();

        for(Store item : storeList){
            if(item.getId() == store.getId()){
                newList.add(store);
            }
            else {
                newList.add(item);
            }
        }
        storeListMutableLiveData.postValue(newList);

        fireStore.collection("Store").document(store.getId()).update(Store.toFireStore(store)).addOnSuccessListener((temp) -> {

            onSuccess.invoke();
        }).addOnFailureListener(e -> {
            Log.e("auth repository", "update user failed.");
            onFailed.invoke();
        });;
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

        List<String> images = new ArrayList<>();
        int amountImage = store.getImages().size();
        for (String image: store.getImages()) {
            Uri uriStore = Uri.parse(image);

            String imageId = UUID.randomUUID().toString().replace("-", "");
            StorageReference imageRef = storageRef.child("stores/" + imageId);
            UploadTask uploadTask = imageRef.putFile(uriStore);
            uploadTask.addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl()
                    .addOnSuccessListener(uri -> {
                        images.add(uri.toString());
                        if (images.size() == amountImage) {
                            newData.put("images", images);
                            collectionStoreRef.add(newData)
                                    .addOnSuccessListener(unused -> listener.onUpdateData(true, ""))
                                    .addOnFailureListener(e -> listener.onUpdateData(false, e.getMessage()));
                        }
                    }).addOnFailureListener(exception -> {
                        Log.e(TAG, "Failed to get the download URL");
                        listener.onUpdateData(false, exception.getMessage());
                    })).addOnFailureListener(exception -> {
                        Log.e(TAG, "Failed to upload the image");
                        listener.onUpdateData(false, exception.getMessage());
                    });
        }
    }
    public void deleteStore(String storeId, UpdateDataListener listener)
    {
        DocumentReference storeRef = fireStore.collection("Store").document(storeId);
        storeRef.delete()
                .addOnSuccessListener(taskSnapshot -> listener.onUpdateData(true, ""))
                .addOnFailureListener(exception -> listener.onUpdateData(false, exception.getMessage()));
    }
}
