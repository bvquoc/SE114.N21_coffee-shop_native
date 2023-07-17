package com.example.coffee_shop_app.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.coffee_shop_app.models.AddressDelivery;
import com.example.coffee_shop_app.utils.interfaces.UpdateDataListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AddressRepository {
    private static final String TAG = "AddressRepository";
    //singleton
    private static AddressRepository instance;
    private AddressRepository() {
        addressListMutableLiveData = new MutableLiveData<>();
        //define fireStore
        fireStore = FirebaseFirestore.getInstance();
    }
    public static synchronized AddressRepository getInstance() {
        if (instance == null) {
            instance = new AddressRepository();
        }
        return instance;
    }

    //properties
    private final MutableLiveData<List<AddressDelivery>> addressListMutableLiveData;
    FirebaseFirestore fireStore;

    //get addresses from firebase fireStore
    public MutableLiveData<List<AddressDelivery>> getAddressListMutableLiveData() {
        if(addressListMutableLiveData.getValue() == null)
        {
            registerSnapshotListener();
        }
        return addressListMutableLiveData;
    }
    public void registerSnapshotListener()
    {
        fireStore.collection("users").document(Objects.requireNonNull(AuthRepository.getInstance().getCurrentUser()).getId()).addSnapshotListener((value, error) -> {
            Log.d(TAG, "get address started.");
            if(value!=null)
            {
                getAddress(value);
            }
            Log.d(TAG, "get address finished.");
        });
    }
    void getAddress(DocumentSnapshot value)
    {
        List<AddressDelivery> addressDeliveries = new ArrayList<>();
        Map<String, Object> data = value.getData();
        if(data!=null)
        {
            if(data.get("addresses") != null)
            {
                List<Object> addresses = (List<Object>)data.get("addresses");
                if(addresses!=null)
                {
                    for (Object address: addresses) {
                        addressDeliveries.add(AddressDelivery.fromFireBase((Map<String, Object>) address));
                    }
                    addressListMutableLiveData.postValue(addressDeliveries);
                    return;
                }
            }
        }
        addressListMutableLiveData.postValue(new ArrayList<>());
    }
    public void deleteAddress(int index, UpdateDataListener listener)
    {
        DocumentReference userRef = fireStore.collection("users").document(Objects.requireNonNull(AuthRepository.getInstance().getCurrentUser()).getId());
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
        DocumentReference userRef = fireStore.collection("users").document(Objects.requireNonNull(AuthRepository.getInstance().getCurrentUser()).getId());
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
        DocumentReference userRef = fireStore.collection("users").document(Objects.requireNonNull(AuthRepository.getInstance().getCurrentUser()).getId());
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
