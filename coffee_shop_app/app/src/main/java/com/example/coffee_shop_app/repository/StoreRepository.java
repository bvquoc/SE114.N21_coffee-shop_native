package com.example.coffee_shop_app.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.coffee_shop_app.models.Store;
import com.example.coffee_shop_app.utils.LocationHelper;
import com.example.coffee_shop_app.utils.interfaces.UpdateDataListener;
import com.example.coffee_shop_app.viewmodels.CartButtonViewModel;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class StoreRepository {
    private static final String TAG = "StoreRepository";

    //singleton
    private static StoreRepository instance;
    private StoreRepository() {
        storeListMutableLiveData = new MutableLiveData<>();
        //define fireStore
        fireStore = FirebaseFirestore.getInstance();

        storeListMutableLiveData.observeForever(stores -> {
            //Change store in cart_button
            Store prevSelectedStore = CartButtonViewModel.getInstance().getSelectedStore().getValue();
            if(prevSelectedStore!=null)
            {
                Store newSelectedStore = null;
                for (Store store:
                        stores) {
                    if(store.getId().equals(prevSelectedStore.getId()))
                    {
                        newSelectedStore = store;
                        break;
                    }
                }
                if(newSelectedStore != null)
                {
                    CartButtonViewModel.getInstance().getSelectedStore().postValue(newSelectedStore);
                }
                else if(stores.size() != 0)
                {
                    CartButtonViewModel.getInstance().getSelectedStore().postValue(stores.get(0));
                }
                else
                {
                    CartButtonViewModel.getInstance().getSelectedStore().postValue(null);
                }
            }
            else
            {
                if(stores.size() != 0)
                {
                    CartButtonViewModel.getInstance().getSelectedStore().postValue(stores.get(0));
                }
                else
                {
                    CartButtonViewModel.getInstance().getSelectedStore().postValue(null);
                }
            }
        });
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

    //get stores from firebase fireStore
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
            if(value!=null)
            {
                getStore(value);
            }
            else
            {
                storeListMutableLiveData.postValue(null);
            }
            Log.d(TAG, "get stores finishes.");
        });
    }
    void getStore(QuerySnapshot value)
    {
        DocumentReference userRef = fireStore.collection("users").document(Objects.requireNonNull(AuthRepository.getInstance().getCurrentUser()).getId());
        userRef
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> favoriteStoreList = new ArrayList<>();
                        Object favoriteStores = documentSnapshot.get("favoriteStores");
                        if(favoriteStores != null)
                        {
                            favoriteStoreList = (List<String>)favoriteStores;
                        }
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
                            storeList.sort((a, b) -> {
                                double distanceA, distanceB;

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
                                {
                                    return  a.getShortName().compareTo(b.getShortName());
                                }
                                return 1;
                            });
                        }
                        else
                        {
                            storeList.sort(Comparator.comparing(Store::getShortName));
                        }

                        storeListMutableLiveData.postValue(storeList);
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "get store failed."));
    }
    public void updateFavorite(String storeId, boolean isFavorite, UpdateDataListener listener)
    {
        DocumentReference userRef = fireStore.collection("users").document(Objects.requireNonNull(AuthRepository.getInstance().getCurrentUser()).getId());
        if(isFavorite)
        {
            userRef.update("favoriteStores", FieldValue.arrayUnion(storeId))
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "update favorite success.");
                        listener.onUpdateData(true);
                        List<Store> tempStores = storeListMutableLiveData.getValue();
                        if(tempStores!=null)
                        {
                            for (Store store:tempStores) {
                                if(store.getId().equals(storeId))
                                {
                                    store.setFavorite(true);
                                    break;
                                }
                            }
                            storeListMutableLiveData.setValue(tempStores);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "update favorite failed.");
                        listener.onUpdateData(false);
                    });
        }
        else
        {
            userRef.update("favoriteStores", FieldValue.arrayRemove(storeId))
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "update favorite success.");
                        listener.onUpdateData(true);
                        List<Store> tempStores = storeListMutableLiveData.getValue();
                        if(tempStores!=null)
                        {
                            for (Store store:tempStores) {
                                if(store.getId().equals(storeId))
                                {
                                    store.setFavorite(false);
                                }
                            }
                            storeListMutableLiveData.setValue(tempStores);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "update favorite failed.");
                        listener.onUpdateData(false);
                    });
        }

    }
}
