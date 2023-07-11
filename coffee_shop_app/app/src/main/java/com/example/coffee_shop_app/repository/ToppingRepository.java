package com.example.coffee_shop_app.repository;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.example.coffee_shop_app.models.Size;
import com.example.coffee_shop_app.models.Topping;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ToppingRepository {
    private static final String TAG = "ToppingRepository";

    //singleton
    private static ToppingRepository instance;
    private ToppingRepository() {
        toppingListMutableLiveData = new MutableLiveData<>();
        //define firestore
        firestore = FirebaseFirestore.getInstance();
    }
    public static synchronized ToppingRepository getInstance() {
        if (instance == null) {
            instance = new ToppingRepository();
        }
        return instance;
    }

    //properties
    private MutableLiveData<List<Topping>> toppingListMutableLiveData;
    private FirebaseFirestore firestore;

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
        firestore.collection("Topping").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                Log.d(TAG, "get toppings started.");
                getTopping(value);
                Log.d(TAG, "get toppings finishes.");
            }
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
        toppingList.sort(new Comparator<Topping>() {
            @Override
            public int compare(Topping o1, Topping o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        toppingListMutableLiveData.postValue(toppingList);
    }
}
