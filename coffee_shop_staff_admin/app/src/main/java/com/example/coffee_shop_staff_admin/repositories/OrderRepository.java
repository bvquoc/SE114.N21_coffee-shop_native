package com.example.coffee_shop_staff_admin.repositories;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.example.coffee_shop_staff_admin.models.AddressDelivery;
import com.example.coffee_shop_staff_admin.models.Order;
import com.example.coffee_shop_staff_admin.models.OrderFood;
import com.example.coffee_shop_staff_admin.models.Store;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.joda.time.DateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OrderRepository {
    private static final String TAG = "OrderRepository";
    //singleton
    private static OrderRepository instance;
    private MutableLiveData<List<Order>> orderListMutableLiveData;

    private FirebaseFirestore firestore;
    private OrderRepository() {
        orderListMutableLiveData = new MutableLiveData<>();

        //define firestore
        firestore = FirebaseFirestore.getInstance();
    }
    public static synchronized OrderRepository getInstance() {
        if (instance == null) {
            instance = new OrderRepository();
        }
        return instance;
    }
    public MutableLiveData<List<Order>> getOrderListMutableLiveData() {
        if(orderListMutableLiveData.getValue() == null)
        {
            registerSnapshotListener();
        }
        return orderListMutableLiveData;
    }
    ListenerRegistration listenerRegistration;
    public void registerSnapshotListener()
    {
        if(listenerRegistration!=null){
            listenerRegistration.remove();
        }
        if(StoreRepository.getInstance().getCurrentStoreLiveData().getValue()==null){
            return;
        }
        String storeId=StoreRepository.getInstance().getCurrentStoreLiveData().getValue().getId();
        listenerRegistration = firestore.collection("beorders").whereEqualTo("store", storeId)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                Log.d(TAG, "get order started.");
                List<Order> listOrder=new ArrayList<>();
                if(value==null || value.isEmpty()){
                    orderListMutableLiveData.postValue(listOrder);
                    Log.d(TAG, "no orders");
                    return;
                }
                for (QueryDocumentSnapshot doc :
                        value) {
                    Map<String, Object> map = doc.getData();
                    firestore.collection("Store")
                            .document(map.get("store").toString())
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    Store store= Store.fromFireBase(documentSnapshot);
                                    String dateOrderString = map.get("dateOrder").toString();
                                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                                    try {
                                        Date orderDate = format.parse(dateOrderString);
                                        Order currentOrder=new Order(
                                                orderDate,
                                                new ArrayList<>(),
                                                map.get("status").toString());
                                        if(map.get("status").toString().equals("Đã tạo")){
                                            return;
                                        }
                                        currentOrder.setId(doc.getId());
                                        currentOrder.setStore(store);
                                        currentOrder.setUserId(map.get("user").toString());
                                        if(map.containsKey("discountPrice")){
                                            currentOrder.setDiscount(((Number) map
                                                    .get("discountPrice"))
                                                    .doubleValue());
                                        }
                                        currentOrder.setTotal(((Number) map
                                                .get("totalPrice"))
                                                .doubleValue());
                                        currentOrder.setTotalProduct(((Number) map
                                                .get("totalProduct"))
                                                .doubleValue());
                                        if(map.containsKey("deliveryCost")){
                                            currentOrder.setDeliveryCost(((Number) map
                                                    .get("deliveryCost"))
                                                    .doubleValue());
                                        } else{
                                            currentOrder.setDeliveryCost(null);
                                        }
                                        if(map.containsKey("pickupTime")){
                                            String dateString = map.get("pickupTime").toString();
                                            try {
                                                Date pickupDate = format.parse(dateString);
                                                currentOrder.setPickupTime(pickupDate);
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                                Log.e(TAG, "get order food failed.");
                                                // Handle the parsing exception if necessary
                                            }
                                        }else{
                                            currentOrder.setPickupTime(null);
                                        }
                                        if(map.containsKey("address")){
                                            currentOrder.setAddress(AddressDelivery
                                                    .fromFireBase((Map<String, Object>)map.get("address")));
                                        } else{
                                            currentOrder.setAddress(null);
                                        }
                                        firestore.collection("users").document(currentOrder.getUserId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                Map<String, Object> user=documentSnapshot.getData();
                                                currentOrder.setUserName(user.get("name").toString());
                                                currentOrder.setPhoneNumber(user.get("phoneNumber").toString());

                                                //TODO: get orderFoods
                                                if(map.get("orderedFoods")!=null){
                                                    List<Object> orderedFoods = (List<Object>)map.get("orderedFoods");
                                                    ArrayList<OrderFood> listOrderFood=new ArrayList<>();
                                                    for (Object orderFood: orderedFoods) {
                                                        listOrderFood.add(OrderFood.fromSnapshot((Map<String, Object>) orderFood));
                                                        currentOrder.setProducts(listOrderFood);
                                                    }

                                                    listOrder.add(currentOrder);
                                                    Collections.sort(listOrder, new Comparator<Order>() {
                                                        @Override
                                                        public int compare(Order o1, Order o2) {
                                                            DateTime d1=new DateTime(o1.getDateOrder());
                                                            DateTime d2=new DateTime(o2.getDateOrder());
                                                            if(d1.isEqual(d2)){
                                                                return 0;
                                                            } else if(d1.isAfter(d2)){
                                                                return -1;
                                                            }else{
                                                                return 1;
                                                            }
                                                        }
                                                    });
                                                    orderListMutableLiveData.postValue(listOrder);
                                                } else{
                                                    Log.e(TAG, "get order food failed.");
                                                }
                                            }
                                        });
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                        Log.e(TAG, "get order food failed.");
                                        // Handle the parsing exception if necessary
                                    }
                                }
                            }).addOnFailureListener(e->{
                                Log.e(TAG, "get order store failed.");
                            });
                }
                Log.d(TAG, "get order finished.");
            }
        });
    }
    public static String orderFoodsToString(Order order){
        HashMap<String, Integer> map=new HashMap<String, Integer>();
        for (OrderFood orderFood:
                order.getProducts()) {
            if(map.containsKey(orderFood.getName())){
                int oldValue=map.get(orderFood.getName());
                map.replace(orderFood.getName(), oldValue+orderFood.getQuantity());
            }else {
                map.put(orderFood.getName(), orderFood.getQuantity());
            }
        }

        ArrayList<String> names=new ArrayList<>();
        map.forEach((key, value)->{
            names.add(key+ " (x" +value.toString()+")");
        });
        return names.stream().collect(Collectors.joining(", "));
    }
}
