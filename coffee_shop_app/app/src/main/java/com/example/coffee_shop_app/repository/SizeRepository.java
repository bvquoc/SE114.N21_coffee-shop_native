package com.example.coffee_shop_app.repository;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.example.coffee_shop_app.Data;
import com.example.coffee_shop_app.models.AddressDelivery;
import com.example.coffee_shop_app.models.Product;
import com.example.coffee_shop_app.models.Size;
import com.example.coffee_shop_app.models.Store;
import com.example.coffee_shop_app.utils.LocationHelper;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class SizeRepository {
    private static final String TAG = "SizeRepository";

    //singleton
    private static SizeRepository instance;
    private SizeRepository() {
        sizeListMutableLiveData = new MutableLiveData<>();
        //define firestore
        firestore = FirebaseFirestore.getInstance();
    }
    public static synchronized SizeRepository getInstance() {
        if (instance == null) {
            instance = new SizeRepository();
        }
        return instance;
    }

    //properties
    private MutableLiveData<List<Size>> sizeListMutableLiveData;
    private FirebaseFirestore firestore;

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
        firestore.collection("Size").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                Log.d(TAG, "get sizes started.");
                getSize(value);
                Log.d(TAG, "get sizes finishes.");
            }
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
        sizeList.sort(new Comparator<Size>() {
            @Override
            public int compare(Size o1, Size o2) {
                if(o1.getPrice() < o2.getPrice())
                {
                    return -1;
                }
                if (o1.getPrice() == o2.getPrice())
                {
                    return  0;
                }
                return 1;
            }
        });
        sizeListMutableLiveData.postValue(sizeList);
    }
}
