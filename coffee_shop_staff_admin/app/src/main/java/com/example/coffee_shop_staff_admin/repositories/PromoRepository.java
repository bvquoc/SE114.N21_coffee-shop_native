package com.example.coffee_shop_staff_admin.repositories;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.coffee_shop_staff_admin.models.Promo;
import com.example.coffee_shop_staff_admin.utils.interfaces.UpdateDataListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PromoRepository {
    private static final String TAG = "PromoRepository";
    //singleton
    private static PromoRepository instance;
    private final MutableLiveData<List<Promo>> promoListMutableLiveData;
    private final FirebaseFirestore fireStore;
    private PromoRepository() {
        promoListMutableLiveData = new MutableLiveData<>();
        //define fireStore
        fireStore = FirebaseFirestore.getInstance();
    }
    public static synchronized PromoRepository getInstance() {
        if (instance == null) {
            instance = new PromoRepository();
        }
        return instance;
    }
    public MutableLiveData<List<Promo>> getPromoListMutableLiveData() {
        if(promoListMutableLiveData.getValue() == null)
        {
            registerSnapshotListener();
        }
        return promoListMutableLiveData;
    }
    public void registerSnapshotListener()
    {
        fireStore.collection("Promo").addSnapshotListener((value, error) -> {
            Log.d(TAG, "get promos started.");
            if(value!=null)
            {
                getPromo(value);
            }
            Log.d(TAG, "get promos finishes.");
        });
    }
    void getPromo(QuerySnapshot value)
    {
        List<Promo> promoList = new ArrayList<>();
        Date now = new Date();

        for (QueryDocumentSnapshot doc : value) {
            if (doc != null) {
                promoList.add(Promo.fromFireBase(doc));
            }
        }
        promoList.sort((o1, o2) -> {
            int dateEnd1Compare = o1.getDateEnd().compareTo(now);
            int dateEnd2Compare = o2.getDateEnd().compareTo(now);
            if(dateEnd1Compare > dateEnd2Compare)
            {
                return 1;
            }
            else if(dateEnd1Compare < dateEnd2Compare)
            {
                return -1;
            }
            else
            {
                return o1.getDateStart().compareTo(o2.getDateStart());
            }
        });
        promoListMutableLiveData.postValue(promoList);
    }
    public void updatePromo(Promo promo, UpdateDataListener listener)
    {
        DocumentReference promoRef = fireStore.collection("Promo").document(promo.getPromoCode());
        Map<String, Object> newData = new HashMap<>();
        newData.put("dateEnd", promo.getDateEnd());
        newData.put("dateStart", promo.getDateStart());
        newData.put("description", promo.getDescription());
        newData.put("forNewCustomer", promo.isForNewCustomer());
        newData.put("maxPrice", promo.getMaxPrice());
        newData.put("minPrice", promo.getMinPrice());
        newData.put("percent", promo.getPercent());
        newData.put("stores", promo.getStores());
        promoRef.update(newData)
                .addOnSuccessListener(aVoid -> listener.onUpdateData(true))
                .addOnFailureListener(e -> listener.onUpdateData(false));
    }

    public void insertPromo(Promo promo, UpdateDataListener listener)
    {
        Map<String, Object> newData = new HashMap<>();
        newData.put("dateEnd", promo.getDateEnd());
        newData.put("dateStart", promo.getDateStart());
        newData.put("description", promo.getDescription());
        newData.put("forNewCustomer", promo.isForNewCustomer());
        newData.put("maxPrice", promo.getMaxPrice());
        newData.put("minPrice", promo.getMinPrice());
        newData.put("percent", promo.getPercent());
        newData.put("stores", promo.getStores());
        fireStore.collection("Promo").document(promo.getPromoCode()).set(newData)
                .addOnSuccessListener(aVoid -> listener.onUpdateData(true))
                .addOnFailureListener(e -> listener.onUpdateData(false));
    }
    public void deletePromo(String promoId, UpdateDataListener listener)
    {
        DocumentReference promoRef = fireStore.collection("Promo").document(promoId);
        promoRef.delete()
                .addOnSuccessListener(taskSnapshot -> listener.onUpdateData(true))
                .addOnFailureListener(exception -> listener.onUpdateData(false));
    }
}