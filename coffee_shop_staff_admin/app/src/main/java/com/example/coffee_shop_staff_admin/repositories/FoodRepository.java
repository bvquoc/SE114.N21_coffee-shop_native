package com.example.coffee_shop_staff_admin.repositories;

import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.coffee_shop_staff_admin.models.Food;
import com.example.coffee_shop_staff_admin.models.FoodChecker;
import com.example.coffee_shop_staff_admin.utils.interfaces.UpdateDataListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.rpc.context.AttributeContext;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FoodRepository {
    private static final String TAG = "FoodRepository";
    //singleton
    private static FoodRepository instance;
    private final MutableLiveData<List<Food>> foodListMutableLiveData;
    private final FirebaseFirestore fireStore;
    private final StorageReference storageRef;

    private FoodRepository() {
        foodListMutableLiveData = new MutableLiveData<>();
        //define fireStore
        fireStore = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();
    }

    public static synchronized FoodRepository getInstance() {
        if (instance == null) {
            instance = new FoodRepository();
        }
        return instance;
    }

    public MutableLiveData<List<Food>> getFoodListMutableLiveData() {
        if (foodListMutableLiveData.getValue() == null) {
            registerSnapshotListener();
        }
        return foodListMutableLiveData;
    }

    public void registerSnapshotListener() {
        fireStore.collection("Food").addSnapshotListener((value, error) -> {
            Log.d(TAG, "get foods started.");
            if (value != null) {
                getFood(value);
            }
            Log.d(TAG, "get foods finishes.");
        });
    }

    void getFood(QuerySnapshot value) {
        List<Food> foodList = new ArrayList<>();

        for (QueryDocumentSnapshot doc : value) {
            if (doc != null) {
                foodList.add(Food.fromFireBase(doc));
            }
        }

        foodList.sort(Comparator.comparing(Food::getName));

        foodListMutableLiveData.postValue(foodList);
    }

    public void updateToppingSizeOfFood(String foodId, List<String> toppingIds, List<String> sizeIds, UpdateDataListener listener) {
        try {
            DocumentReference foodRef = fireStore.collection("Food").document(foodId);
            Map<String, Object> updates = new HashMap<>();
            updates.put("toppings", toppingIds);
            updates.put("sizes", sizeIds);
            foodRef.update(updates)
                    .addOnSuccessListener(unused -> listener.onUpdateData(true, ""))
                    .addOnFailureListener(e -> listener.onUpdateData(false, e.getMessage()));
        } catch (Exception e) {
            listener.onUpdateData(false, e.getMessage());
        }
    }

    public void updateFood(Food food, UpdateDataListener listener) {
        DocumentReference foodRef = fireStore.collection("Food").document(food.getId());
        Map<String, Object> newData = new HashMap<>();
        newData.put("name", food.getName());
        newData.put("price", (int) food.getPrice());
        newData.put("description", food.getDescription());
        newData.put("sizes", food.getSizes());
        newData.put("toppings", food.getToppings());

        List<String> images = new ArrayList<>();
        int amountImage = food.getImages().size();
        for (String image : food.getImages()) {
            Uri uriFood = Uri.parse(image);
            String scheme = uriFood.getScheme();
            if (scheme != null) {
                if (scheme.equals("https") || scheme.equals("gs")) {
                    //The image is still from firebase
                    images.add(image);
                    if (images.size() == amountImage) {
                        newData.put("images", images);
                        foodRef.update(newData)
                                .addOnSuccessListener(unused -> listener.onUpdateData(true, ""))
                                .addOnFailureListener(e -> listener.onUpdateData(false, e.getMessage()));
                    }
                } else {
                    //The image is from phone
                    String imageId = UUID.randomUUID().toString().replace("-", "");
                    StorageReference imageRef = storageRef.child("products/food/" + imageId);
                    UploadTask uploadTask = imageRef.putFile(uriFood);
                    uploadTask.addOnSuccessListener(
                            taskSnapshot -> imageRef.getDownloadUrl()
                                    .addOnSuccessListener(uri -> {
                                        images.add(uri.toString());
                                        if (images.size() == amountImage) {
                                            newData.put("images", images);
                                            foodRef.update(newData)
                                                    .addOnSuccessListener(
                                                            unused -> listener.onUpdateData(true, ""))
                                                    .addOnFailureListener(
                                                            e -> listener.onUpdateData(false, e.getMessage()));
                                        }
                                    }).addOnFailureListener(exception -> {
                                        Log.e(TAG, "Failed to get the download URL");
                                        listener.onUpdateData(false, exception.getMessage());
                                    })).addOnFailureListener(exception -> {
                        Log.e(TAG, "Failed to upload the image");
                        listener.onUpdateData(false, exception.getMessage());
                    });
                }
            } else {
                Log.e(TAG, "The URI does not have a scheme or is invalid");
                listener.onUpdateData(false, "The URI does not have a scheme or is invalid");
            }
        }
    }

    public void insertFood(Food food, UpdateDataListener listener) {
        CollectionReference collectionFoodRef = fireStore.collection("Food");
        Map<String, Object> newData = new HashMap<>();
        newData.put("name", food.getName());
        newData.put("description", food.getDescription());
        newData.put("price", (int) food.getPrice());
        newData.put("sizes", food.getSizes());
        newData.put("toppings", food.getToppings());

        List<String> images = new ArrayList<>();
        int amountImage = food.getImages().size();
        for (String image : food.getImages()) {
            Uri uriFood = Uri.parse(image);

            String imageId = UUID.randomUUID().toString().replace("-", "");
            StorageReference imageRef = storageRef.child("products/food/" + imageId);
            UploadTask uploadTask = imageRef.putFile(uriFood);
            uploadTask.addOnSuccessListener(taskSnapshot ->
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        images.add(uri.toString());
                        if (images.size() == amountImage) {
                            newData.put("images", images);
                            newData.put("createAt", new Date());
                            collectionFoodRef.add(newData)
                                    .addOnSuccessListener(unused -> listener.onUpdateData(true, ""))
                                    .addOnFailureListener(e -> listener.onUpdateData(false, e.getMessage()));
                        }
                    }).addOnFailureListener(exception -> {
                        Log.e(TAG, "Failed to get the download URL");
                        listener.onUpdateData(false, exception.getMessage());
                    })).addOnFailureListener(exception -> {
                Log.e(TAG, "Failed to upload the image");
                listener.onUpdateData(false, exception.getMessage());
            });
        }
    }

    public void deleteFood(String foodId, UpdateDataListener listener) {
        DocumentReference foodRef = fireStore.collection("Food").document(foodId);
        foodRef.delete()
                .addOnSuccessListener(
                        taskSnapshot -> listener.onUpdateData(true, ""))
                .addOnFailureListener(
                        exception -> listener.onUpdateData(false, exception.getMessage()));
    }

    public List<FoodChecker<Food>> toFoodChecker(List<Food> foods) {
        List<FoodChecker<Food>> res = new ArrayList<>();
        for(Food item : foods){
            res.add(new FoodChecker(item, true, AuthRepository.getInstance().getCurrentUser().getStore(), item.getId(), new ArrayList<>()));
        }
        return res;
    }
}
