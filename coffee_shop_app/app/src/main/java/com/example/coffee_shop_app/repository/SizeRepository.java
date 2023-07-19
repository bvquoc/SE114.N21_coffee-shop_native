package com.example.coffee_shop_app.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.coffee_shop_app.models.Size;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SizeRepository {
    private static final String TAG = "SizeRepository";

    //singleton
    private static SizeRepository instance;
    private SizeRepository() {
        sizeListMutableLiveData = new MutableLiveData<>();
        //define fireStore
        fireStore = FirebaseFirestore.getInstance();
    }
    public static synchronized SizeRepository getInstance() {
        if (instance == null) {
            instance = new SizeRepository();
        }
        return instance;
    }

    //properties
    private final MutableLiveData<List<Size>> sizeListMutableLiveData;
    private final FirebaseFirestore fireStore;

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
        fireStore.collection("Size")
                .orderBy("price")
                .addSnapshotListener((value, error) -> {
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
        sizeListMutableLiveData.postValue(sizeList);
    }
}
