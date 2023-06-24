package com.example.coffee_shop_app.repository;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.example.coffee_shop_app.Data;
import com.example.coffee_shop_app.models.Store;
import com.example.coffee_shop_app.utils.LocationHelper;
import com.example.coffee_shop_app.utils.interfaces.UpdateDataListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class StoreRepository {
    private static final String TAG = "StoreRepository";

    //singleton
    private static StoreRepository instance;
    private StoreRepository() {
        storeListMutableLiveData = new MutableLiveData<>();
        //define firestore
        firestore = FirebaseFirestore.getInstance();
    }
    public static synchronized StoreRepository getInstance() {
        if (instance == null) {
            instance = new StoreRepository();
        }
        return instance;
    }

    //properties
    private MutableLiveData<List<Store>> storeListMutableLiveData;
    FirebaseFirestore firestore;

    //get stores from firebase firestore
    public MutableLiveData<List<Store>> getStoreListMutableLiveData() {
        if(storeListMutableLiveData.getValue() == null)
        {
            registerSnapshotListener();
        }
        return storeListMutableLiveData;
    }
    void registerSnapshotListener()
    {
        firestore.collection("Store").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                Log.d(TAG, "get stores started.");
                //get the favoriteStores
                getStore(value);
            }
        });
    }
    void getStore(QuerySnapshot value)
    {
        DocumentReference userRef = firestore.collection("users").document(Data.instance.userId);
        userRef
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> favoriteStoreList = (List<String>)documentSnapshot.get("favoriteStores");
                        List<Store> storeList = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : value) {
                            if (doc != null) {
                                if(favoriteStoreList.contains(doc.getId()))
                                {
                                    storeList.add(Store.fromFireBase(doc, true));
                                }
                                else
                                {
                                    storeList.add(Store.fromFireBase(doc, false));
                                }
                            }
                        }
                        if(LocationHelper.getInstance().getCurrentLocation() != null)
                        {
                            storeList.sort(new Comparator<Store>() {
                                @Override
                                public int compare(Store a, Store b) {
                                    double distanceA = 0, distanceB = 0;

                                    distanceA = LocationHelper.calculateDistance(a.getAddress().getLat(),
                                            a.getAddress().getLng(),
                                            LocationHelper.getInstance().getCurrentLocation().latitude,
                                            LocationHelper.getInstance().getCurrentLocation().longitude
                                    );
                                    distanceB = LocationHelper.calculateDistance(b.getAddress().getLat(),
                                            b.getAddress().getLng(),
                                            LocationHelper.getInstance().getCurrentLocation().latitude,
                                            LocationHelper.getInstance().getCurrentLocation().longitude
                                    );
                                    if(distanceA < distanceB)
                                        return -1;
                                    if(distanceA == distanceB)
                                        return 0;
                                    return 1;
                                }
                            });
                        }

                        storeListMutableLiveData.postValue(storeList);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "get store failed.");
                });
    }
    public void updateFavorite(String storeId, boolean isFavorite, UpdateDataListener listener)
    {
        DocumentReference userRef = firestore.collection("users").document(Data.instance.userId);
        if(isFavorite)
        {
            userRef.update("favoriteStores", FieldValue.arrayUnion(storeId))
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "update favorite success.");
                        listener.onUpdateData(true);
                        List<Store> tempStores = storeListMutableLiveData.getValue();
                        for (Store store:tempStores) {
                            if(store.getId() == storeId)
                            {
                                store.setFavorite(isFavorite);
                            }
                        }
                        storeListMutableLiveData.setValue(tempStores);
                        return;
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "update favorite failed.");
                        listener.onUpdateData(false);
                        return;
                    });
        }
        else
        {
            userRef.update("favoriteStores", FieldValue.arrayRemove(storeId))
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "update favorite success.");
                        listener.onUpdateData(true);
                        List<Store> tempStores = storeListMutableLiveData.getValue();
                        for (Store store:tempStores) {
                            if(store.getId() == storeId)
                            {
                                store.setFavorite(isFavorite);
                            }
                        }
                        storeListMutableLiveData.setValue(tempStores);
                        return;
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "update favorite failed.");
                        listener.onUpdateData(false);
                        return;
                    });
        }

    }
}
