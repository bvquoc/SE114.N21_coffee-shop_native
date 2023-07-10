package com.example.coffee_shop_app.repository;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.example.coffee_shop_app.Data;
import com.example.coffee_shop_app.models.Product;
import com.example.coffee_shop_app.models.Store;
import com.example.coffee_shop_app.utils.LocationHelper;
import com.example.coffee_shop_app.viewmodels.CartButtonViewModel;
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

public class ProductRepository {
    private static final String TAG = "ProductRepository";
    //singleton
    private static ProductRepository instance;
    private MutableLiveData<List<Product>> productListMutableLiveData;
    private FirebaseFirestore firestore;
    private ProductRepository() {
        productListMutableLiveData = new MutableLiveData<>();
        //define firestore
        firestore = FirebaseFirestore.getInstance();
    }
    public static synchronized ProductRepository getInstance() {
        if (instance == null) {
            instance = new ProductRepository();
        }
        return instance;
    }
    public MutableLiveData<List<Product>> getProductListMutableLiveData() {
        if(productListMutableLiveData.getValue() == null)
        {
            if(CartButtonViewModel.getInstance().getSelectedStore().getValue() != null)
            {
                registerSnapshotListener(CartButtonViewModel.getInstance().getSelectedStore().getValue().getStateFood());
            }
        }
        return productListMutableLiveData;
    }
    public void registerSnapshotListener(Map<String, List<String>> stateFood)
    {
        firestore.collection("Food").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                Log.d(TAG, "get products started.");
                getProduct(value, stateFood);
                Log.d(TAG, "get products finishes.");
            }
        });
    }
    void getProduct(QuerySnapshot value, Map<String, List<String>> stateFood)
    {
        DocumentReference userRef = firestore.collection("users").document(Data.instance.userId);
        userRef
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> favoriteProductList = new ArrayList<>();
                        Object favoriteProducts = documentSnapshot.get("favoriteFoods");
                        if(favoriteProducts != null)
                        {
                            favoriteProductList = (List<String>)favoriteProducts;
                        }
                        List<Product> productList = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : value) {
                            if (doc != null) {
                                boolean isAvailable =
                                        (stateFood.get(doc.getId()) == null || !stateFood.get(doc.getId()).isEmpty());
                                productList.add(Product.fromFireBase(doc,  isAvailable, favoriteProductList.contains(doc.getId())));
                            }
                        }

                        productList.sort(new Comparator<Product>() {
                            @Override
                            public int compare(Product o1, Product o2) {
                                return  o1.getName().compareTo(o2.getName());
                            }
                        });

                        productListMutableLiveData.postValue(productList);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "get products failed.");
                });
    }

}
