package com.example.coffee_shop_staff_admin.repositories;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.example.coffee_shop_staff_admin.models.Food;
import com.example.coffee_shop_staff_admin.utils.interfaces.UpdateDataListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FoodRepository {
    private static final String TAG = "FoodRepository";
    //singleton
    private static FoodRepository instance;
    private MutableLiveData<List<Food>> foodListMutableLiveData;
    private FirebaseFirestore firestore;
    private FoodRepository() {
        foodListMutableLiveData = new MutableLiveData<>();
        //define firestore
        firestore = FirebaseFirestore.getInstance();
    }
    public static synchronized FoodRepository getInstance() {
        if (instance == null) {
            instance = new FoodRepository();
        }
        return instance;
    }
    public MutableLiveData<List<Food>> getFoodListMutableLiveData() {
        if(foodListMutableLiveData.getValue() == null)
        {
            registerSnapshotListener();
        }
        return foodListMutableLiveData;
    }
    public void registerSnapshotListener()
    {
        firestore.collection("Food").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                Log.d(TAG, "get foods started.");
                getFood(value);
                Log.d(TAG, "get foods finishes.");
            }
        });
    }
    void getFood(QuerySnapshot value)
    {
        List<Food> foodList = new ArrayList<>();

        for (QueryDocumentSnapshot doc : value) {
            if (doc != null) {
                foodList.add(Food.fromFireBase(doc));
            }
        }

        foodList.sort(new Comparator<Food>() {
            @Override
            public int compare(Food o1, Food o2) {
                return  o1.getName().compareTo(o2.getName());
            }
        });

        foodListMutableLiveData.postValue(foodList);
    }
    public void updateToppingSizeOfFood(String foodId, List<String> toppingIds, List<String> sizeIds, UpdateDataListener listener)
    {
        try
        {
            DocumentReference foodRef = firestore.collection("Food").document(foodId);
            Map<String, Object> updates = new HashMap<>();
            updates.put("toppings", toppingIds);
            updates.put("sizes", sizeIds);
            foodRef.update(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    listener.onUpdateData(true);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    listener.onUpdateData(false);
                }
            });
        }
        catch (Exception e)
        {
            listener.onUpdateData(false);
        }
    }
    public void deleteFood(String foodId, UpdateDataListener listener)
    {
        DocumentReference foodRef = firestore.collection("Food").document(foodId);
        foodRef.delete().addOnSuccessListener(taskSnapshot -> {
            listener.onUpdateData(true);
        }).addOnFailureListener(exception -> {
            listener.onUpdateData(false);
        });
    }
}
