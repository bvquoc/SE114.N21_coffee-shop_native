package com.example.coffee_shop_app.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.coffee_shop_app.models.Topping;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ToppingRepository {
    private static final String TAG = "ToppingRepository";

    //singleton
    private static ToppingRepository instance;
    private ToppingRepository() {
        toppingListMutableLiveData = new MutableLiveData<>();
        //define firestore
        fireStore = FirebaseFirestore.getInstance();
    }
    public static synchronized ToppingRepository getInstance() {
        if (instance == null) {
            instance = new ToppingRepository();
        }
        return instance;
    }

    //properties
    private final MutableLiveData<List<Topping>> toppingListMutableLiveData;
    private final FirebaseFirestore fireStore;

    //Function
    public MutableLiveData<List<Topping>> getToppingListMutableLiveData() {
        if(toppingListMutableLiveData.getValue() == null)
        {
            registerSnapshotListener();
        }
        return toppingListMutableLiveData;
    }
    void registerSnapshotListener()
    {
        fireStore.collection("Topping")
                .orderBy("name")
                .addSnapshotListener((value, error) -> {
                    Log.d(TAG, "get toppings started.");
                    if(value!=null)
                    {
                        getTopping(value);
                    }
                    Log.d(TAG, "get toppings finishes.");
                });
    }
    void getTopping(QuerySnapshot value)
    {
        List<Topping> toppingList = new ArrayList<>();
        for (QueryDocumentSnapshot doc : value) {
            if (doc != null) {
                toppingList.add(Topping.fromFireBase(doc));
            }
        }
        toppingListMutableLiveData.postValue(toppingList);
    }
}
