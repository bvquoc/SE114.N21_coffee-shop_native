package com.example.coffee_shop_app.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.coffee_shop_app.models.Promo;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
                Promo promo = Promo.fromFireBase(doc);
                if(now.compareTo(promo.getDateStart()) < 0 || now.compareTo(promo.getDateEnd()) > 0 )
                {
                    continue;
                }
                promoList.add(promo);
            }
        }
        promoList.sort((o1, o2) -> {
            if(o1.isForNewCustomer() && !o2.isForNewCustomer())
            {
                return 1;
            }
            else if (!o1.isForNewCustomer()&& o2.isForNewCustomer())
            {
                return -1;
            }
            else
            {
                return o1.getDateEnd().compareTo(o2.getDateEnd());
            }

        });
        promoListMutableLiveData.postValue(promoList);
    }
}