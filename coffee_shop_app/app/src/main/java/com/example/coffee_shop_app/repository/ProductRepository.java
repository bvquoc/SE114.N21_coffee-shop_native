package com.example.coffee_shop_app.repository;


import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.coffee_shop_app.models.Product;

import com.example.coffee_shop_app.viewmodels.CartButtonViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ProductRepository {
    private static final String TAG = "ProductRepository";
    //singleton
    private static ProductRepository instance;
    private final MutableLiveData<List<Product>> productListMutableLiveData;
    private final FirebaseFirestore fireStore;
    private ProductRepository() {
        productListMutableLiveData = new MutableLiveData<>();
        //define fireStore
        fireStore = FirebaseFirestore.getInstance();
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
            Map<String, List<String>> stateFood = new HashMap<>();
            if(CartButtonViewModel.getInstance().getSelectedStore().getValue()!=null)
            {
                stateFood = CartButtonViewModel.getInstance().getSelectedStore().getValue().getStateFood();
            }
            registerSnapshotListener(stateFood);
        }
        return productListMutableLiveData;
    }
    public void registerSnapshotListener(Map<String, List<String>> stateFood)
    {
        fireStore.collection("Food").addSnapshotListener((value, error) -> {
            Log.d(TAG, "get products started.");
            if(value!=null)
            {
                getProduct(value, stateFood);
            }
            Log.d(TAG, "get products finishes.");
        });
    }
    void getProduct(QuerySnapshot value, Map<String, List<String>> stateFood)
    {
        DocumentReference userRef = fireStore.collection("users").document(Objects.requireNonNull(AuthRepository.getInstance().getCurrentUser()).getId());
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
                                        (stateFood.get(doc.getId()) == null || !Objects.requireNonNull(stateFood.get(doc.getId())).isEmpty());
                                productList.add(Product.fromFireBase(doc,  isAvailable, favoriteProductList.contains(doc.getId())));
                            }
                        }

                        productList.sort(Comparator.comparing(Product::getName));

                        productListMutableLiveData.postValue(productList);
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "get products failed."));
    }

    public void updateFavorite(String prdId, boolean isFav){
        fireStore.collection("users")
                .document(AuthRepository.getInstance().getCurrentUser().getId())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Map<String, Object> map=documentSnapshot.getData();
                        List<String> favorites=new ArrayList<>();
                        if(map!=null && map.containsKey("favoriteFoods")){
                             favorites=(List<String>)(map.get("favoriteFoods"));
                        }

                        if(favorites.contains(prdId)){
                            if(!isFav){
                                favorites.remove(prdId);
                            }
                        } else{
                            if(isFav){
                                favorites.add(prdId);
                            }
                        }

                        fireStore.collection("users")
                                .document(AuthRepository.getInstance().getCurrentUser().getId())
                                .update("favoriteFoods", favorites).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                      List<Product> tempList=
                                              ProductRepository.getInstance().getProductListMutableLiveData().getValue();
                                        if(tempList!=null)
                                        {
                                            for (Product prd:tempList) {
                                                if(prd.getId().equals(prdId))
                                                {
                                                    prd.setFavorite(isFav);
                                                    break;
                                                }
                                            }
                                            ProductRepository.getInstance().getProductListMutableLiveData().setValue(tempList);
                                        }
                                    }
                                });
                    }
                });
    }
}
