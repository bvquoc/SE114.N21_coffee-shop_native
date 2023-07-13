package com.example.coffee_shop_staff_admin.repositories;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.example.coffee_shop_staff_admin.models.Topping;
import com.example.coffee_shop_staff_admin.utils.interfaces.UpdateDataListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ToppingRepository {
    private static final String TAG = "ToppingRepository";

    //singleton
    private static ToppingRepository instance;

    private ToppingRepository() {
        toppingListMutableLiveData = new MutableLiveData<>();
        //define firestore
        firestore = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();
    }
    public static synchronized ToppingRepository getInstance() {
        if (instance == null) {
            instance = new ToppingRepository();
        }
        return instance;
    }

    //properties
    private final MutableLiveData<List<Topping>> toppingListMutableLiveData;
    private final FirebaseFirestore firestore;
    private final StorageReference storageRef;

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

    public void updateTopping(Topping topping, UpdateDataListener listener)
    {
        DocumentReference toppingRef = firestore.collection("Topping").document(topping.getId());
        Map<String, Object> newData = new HashMap<>();
        Uri uriTopping = Uri.parse(topping.getImage());
        String scheme = uriTopping.getScheme();
        if (scheme != null) {
            if (scheme.equals("https") || scheme.equals("gs")) {
                //The image is still from firebase
                newData.put("name", topping.getName());
                newData.put("price", topping.getPrice());
                newData.put("image", topping.getImage().toString());
                toppingRef.update(newData)
                        .addOnSuccessListener(aVoid -> {
                            listener.onUpdateData(true);
                        })
                        .addOnFailureListener(e -> {
                            listener.onUpdateData(false);
                        });
            }else {
                //The image is from phone
                String imageId = UUID.randomUUID().toString().replace("-", "");
                StorageReference imageRef = storageRef.child("products/topping/" + imageId);
                UploadTask uploadTask = imageRef.putFile(uriTopping);
                uploadTask.addOnSuccessListener(taskSnapshot -> {
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String downloadUrl = uri.toString();
                        newData.put("name", topping.getName());
                        newData.put("price", topping.getPrice());
                        newData.put("image", downloadUrl);
                        toppingRef.update(newData)
                                .addOnSuccessListener(aVoid -> {
                                    listener.onUpdateData(true);
                                })
                                .addOnFailureListener(e -> {
                                    listener.onUpdateData(false);
                                });

                    }).addOnFailureListener(exception -> {
                        Log.e(TAG, "Failed to get the download URL");
                        listener.onUpdateData(false);
                    });

                }).addOnFailureListener(exception -> {
                    Log.e(TAG, "Failed to upload the image");
                    listener.onUpdateData(false);
                });
            }
        } else {
            Log.e(TAG, "The URI does not have a scheme or is invalid");
            listener.onUpdateData(false);
        }
    }

    public void insertTopping(Topping topping, UpdateDataListener listener)
    {
        CollectionReference collectionToppingRef = firestore.collection("Topping");
        Map<String, Object> newData = new HashMap<>();
        String imageId = UUID.randomUUID().toString().replace("-", "");
        StorageReference imageRef = storageRef.child("products/topping/" + imageId);
        Uri uriTopping = Uri.parse(topping.getImage());
        UploadTask uploadTask = imageRef.putFile(uriTopping);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String downloadUrl = uri.toString();
                newData.put("name", topping.getName());
                newData.put("price", topping.getPrice());
                newData.put("image", downloadUrl);
                collectionToppingRef.add(newData)
                        .addOnSuccessListener(documentReference -> {
                            listener.onUpdateData(true);
                        })
                        .addOnFailureListener(exception -> {
                            listener.onUpdateData(false);
                        });

            }).addOnFailureListener(exception -> {
                Log.e(TAG, "Failed to get the download URL");
                listener.onUpdateData(false);
            });

        }).addOnFailureListener(exception -> {
            Log.e(TAG, "Failed to upload the image");
            listener.onUpdateData(false);
        });
    }
    public void deleteTopping(String toppingId, UpdateDataListener listener)
    {
        DocumentReference toppingRef = firestore.collection("Topping").document(toppingId);
        toppingRef.delete().addOnSuccessListener(taskSnapshot -> {
            listener.onUpdateData(true);
            }).addOnFailureListener(exception -> {
                listener.onUpdateData(false);
            });
    }
}