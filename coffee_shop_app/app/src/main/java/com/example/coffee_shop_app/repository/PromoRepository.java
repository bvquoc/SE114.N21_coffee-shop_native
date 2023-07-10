package com.example.coffee_shop_app.repository;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.example.coffee_shop_app.Data;
import com.example.coffee_shop_app.models.Product;
import com.example.coffee_shop_app.models.Promo;
import com.example.coffee_shop_app.models.Size;
import com.example.coffee_shop_app.viewmodels.CartButtonViewModel;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class PromoRepository {
    private static final String TAG = "PromoRepository";
    //singleton
    private static PromoRepository instance;
    private MutableLiveData<List<Promo>> promoListMutableLiveData;
    private FirebaseFirestore firestore;
    private PromoRepository() {
        promoListMutableLiveData = new MutableLiveData<>();
        //define firestore
        firestore = FirebaseFirestore.getInstance();
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
        firestore.collection("Promo").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                Log.d(TAG, "get promos started.");
                getPromo(value);
                Log.d(TAG, "get promos finishes.");
            }
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
                promoList.add(Promo.fromFireBase(doc));
            }
        }
        promoList.sort(new Comparator<Promo>() {
            @Override
            public int compare(Promo o1, Promo o2) {
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

            }
        });
        promoListMutableLiveData.postValue(promoList);
    }
}
