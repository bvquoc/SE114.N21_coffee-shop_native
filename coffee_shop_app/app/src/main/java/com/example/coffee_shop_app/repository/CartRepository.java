package com.example.coffee_shop_app.repository;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.coffee_shop_app.Data;
import com.example.coffee_shop_app.models.Order;
import com.example.coffee_shop_app.models.OrderFood;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class CartRepository {
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference orderRF;
    //TODO: get the user id
//    private String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();


    public CartRepository() {
        firebaseFirestore = FirebaseFirestore.getInstance();
        orderRF=firebaseFirestore.collection("orders");
    }

    public void addOrder(Order order){
        HashMap<String, Object> orderMap=new HashMap<>();
        orderMap.put("user", Data.instance.userId);
        if(order.getStore()!=null){
            orderMap.put("store", order.getStore().getId());
        }
        orderMap.put("totalPrice", order.getTotal());
        orderMap.put("dateOrder", order.getDateOrder());
        orderMap.put("totalProduct", order.getTotalProduct());
        orderMap.put("status", order.getStatus());

        orderRF.add(orderMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                HashMap<String, Object> updateMap=new HashMap<>();
                if(order.getDeliveryCost()!=null && order.getAddress()!=null){
                    //TODO: put address properly
                    HashMap<String, Object> addressMap=new HashMap<>();
                    addressMap.put("formattedAddress", order.getAddress().getAddress().toString());
                    addressMap.put("nameReceiver", order.getAddress().getNameReceiver());
                    addressMap.put("phone", order.getAddress().getPhone());

                    updateMap.put("deliveryCost", order.getDeliveryCost());
                    updateMap.put("address", addressMap);

                    orderRF.document(documentReference.getId()).update(updateMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){

                            } else{
                                Log.d("PLACE ORDER", "Update address in order failed");
                            }
                        }
                    });
                } else if(order.getPickupTime()!=null){
                    updateMap.put("pickupTime", order.getPickupTime());
                    orderRF.document(documentReference.getId()).update(updateMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){

                            }else{
                                Log.d("PLACE ORDER", "Update pickup time in order failed");
                            }
                        }
                    });
                }
                for (OrderFood orderFood :
                        order.getProducts()) {
                    HashMap<String, Object> prdMap=new HashMap<>();
                    prdMap.put("name", orderFood.getName());
                    prdMap.put("image", orderFood.getImage());
                    prdMap.put("quantity", orderFood.getQuantity());
                    prdMap.put("size", orderFood.getSize());
                    prdMap.put("topping", orderFood.getTopping());
                    prdMap.put("unitPrice", orderFood.getUnitPrice());
                    prdMap.put("note", orderFood.getNote());
                    prdMap.put("totalPrice", orderFood.getUnitPrice()*orderFood.getQuantity());
                    orderRF.document(documentReference
                            .getId())
                            .collection("orderedFoods")
                            .add(prdMap)
                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    if(task.isSuccessful()){

                                    }else{
                                        Log.d("PLACE ORDER", "add orderFoods failed");
                                    }
                                }
                            });
                }
            }
        }).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if(task.isComplete()){
                    Log.d("PLACE ORDER", "onComplete: successfully");
                }
            }
        });
    }

}
