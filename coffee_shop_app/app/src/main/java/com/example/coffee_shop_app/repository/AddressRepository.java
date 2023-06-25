package com.example.coffee_shop_app.repository;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.example.coffee_shop_app.Data;
import com.example.coffee_shop_app.models.AddressDelivery;
import com.example.coffee_shop_app.models.Store;
import com.example.coffee_shop_app.utils.LocationHelper;
import com.example.coffee_shop_app.utils.interfaces.UpdateDataListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class AddressRepository {
    private static final String TAG = "AddressRepository";
    //singleton
    private static AddressRepository instance;
    private AddressRepository() {
        addressListMutableLiveData = new MutableLiveData<>();
        //define firestore
        firestore = FirebaseFirestore.getInstance();
    }
    public static synchronized AddressRepository getInstance() {
        if (instance == null) {
            instance = new AddressRepository();
        }
        return instance;
    }

    //properties
    private MutableLiveData<List<AddressDelivery>> addressListMutableLiveData;
    FirebaseFirestore firestore;

    //get addresses from firebase firestore
    public MutableLiveData<List<AddressDelivery>> getAddressListMutableLiveData() {
        if(addressListMutableLiveData.getValue() == null)
        {
            registerSnapshotListener();
        }
        return addressListMutableLiveData;
    }
    void registerSnapshotListener()
    {
        firestore.collection("users").document(Data.instance.userId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                Log.d(TAG, "get address started.");
                getAddress(value);
            }
        });
    }
    void getAddress(DocumentSnapshot value)
    {
        List<AddressDelivery> addressDeliveries = new ArrayList<AddressDelivery>();
        Map<String, Object> data = value.getData();
        List<Object> addresses = (List<Object>)data.get("addresses");
        for (Object address: addresses) {
            addressDeliveries.add(AddressDelivery.fromFireBase((Map<String, Object>) address));
        }
        addressListMutableLiveData.postValue(addressDeliveries);
    }
    public void deleteAddress(int index, UpdateDataListener listener)
    {
        DocumentReference userRef = firestore.collection("users").document(Data.instance.userId);
        userRef.get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<Object> addresses = (List<Object>) documentSnapshot.get("addresses");
                        if(addresses == null)
                        {
                            addresses = new ArrayList<Object>();
                        }
                        if (addresses.size() > 0 && addresses.size() > index)
                        {
                            addresses.remove(index);
                        }

                        userRef.update("addresses", addresses)
                                .addOnSuccessListener(aVoid -> {
                                    listener.onUpdateData(true);
                                })
                                .addOnFailureListener(e -> {
                                    listener.onUpdateData(false);
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    listener.onUpdateData(false);
                });
    }
    public void insertAddress(AddressDelivery addressDelivery, UpdateDataListener listener)
    {
        DocumentReference userRef = firestore.collection("users").document(Data.instance.userId);
        userRef.get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<Object> addresses = (List<Object>) documentSnapshot.get("addresses");
                        if(addresses == null)
                        {
                            addresses = new ArrayList<Object>();
                        }
                        addresses.add(AddressDelivery.toFireBase(addressDelivery));

                        userRef.update("addresses", addresses)
                                .addOnSuccessListener(aVoid -> {
                                    listener.onUpdateData(true);
                                })
                                .addOnFailureListener(e -> {
                                    listener.onUpdateData(false);
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    listener.onUpdateData(false);
                });
    }
    public void updateAddress(AddressDelivery addressDelivery, int index, UpdateDataListener listener)
    {
        DocumentReference userRef = firestore.collection("users").document(Data.instance.userId);
        userRef.get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<Object> addresses = (List<Object>) documentSnapshot.get("addresses");
                        if(addresses == null)
                        {
                            addresses = new ArrayList<Object>();
                        }
                        if (addresses.size() > index)
                        {
                            addresses.set(index, AddressDelivery.toFireBase(addressDelivery));
                        }

                        userRef.update("addresses", addresses)
                                .addOnSuccessListener(aVoid -> {
                                    listener.onUpdateData(true);
                                })
                                .addOnFailureListener(e -> {
                                    listener.onUpdateData(false);
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    listener.onUpdateData(false);
                });
    }
}
