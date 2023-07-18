package com.example.coffee_shop_staff_admin.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.example.coffee_shop_staff_admin.R;
import com.example.coffee_shop_staff_admin.adapters.DeliveryCardAdapter;
import com.example.coffee_shop_staff_admin.adapters.OrderProdItemAdapter;
import com.example.coffee_shop_staff_admin.databinding.ActivityOrderDetailBinding;
import com.example.coffee_shop_staff_admin.models.Order;
import com.example.coffee_shop_staff_admin.repositories.OrderRepository;
import com.example.coffee_shop_staff_admin.viewmodels.OrderOfStoreViewModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OrderDetailActivity extends AppCompatActivity {
    ActivityOrderDetailBinding activityOrderDetailBinding;
    private Order order;
    private static final String TAG="OrderDetailActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityOrderDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_order_detail);
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        activityOrderDetailBinding.btnChangeStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> map=new HashMap<>();
                map.put("status", activityOrderDetailBinding.statusChange.getText().toString());
                ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
                try {
                    String json = ow.writeValueAsString(map);
                    String url=urlSetStatus+order.getId();
                    post(url, json, new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            Log.e(TAG, e.getMessage());
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            if (response.isSuccessful()) {
                                int code=response.code();
                                if(response.code()==200){
                                    try {
                                        String jsonData = response.body().string();
                                        JSONObject token = new JSONObject(jsonData);
                                        if(token.has("orderId")){
                                            String orderId=token.get("orderId").toString();
                                            Log.d(TAG, "Set order status successfully: " + orderId);
                                        }else{
                                            Log.e(TAG, "Receive json error");
                                        }
                                    } catch (JSONException e) {
                                        Log.e(TAG, e.getMessage());
                                    }
                                }
                            }
                            else {
                                if(response.code()==400){
                                    Log.d(TAG, "Invalid order ID");
                                }
                            }
                        }
                    });
                } catch (JsonProcessingException e) {
                    Log.d(TAG, "Get json string failed");
                }
            }
        });
        String orderId = OrderDetailActivity.this.getIntent().getStringExtra("orderId");
        OrderRepository.getInstance().getOrderListMutableLiveData().observe(this, new Observer<List<Order>>() {
            @Override
            public void onChanged(List<Order> orders) {
                order = null;
                for (Order ord: orders
                ) {
                    if(ord.getId().equals(orderId))
                    {
                        order = ord;
                        setOrder();
                        break;
                    }
                }
            }
        });
    }

    DecimalFormat formatter = new DecimalFormat("#,##0.##");
    private void setOrder(){
        boolean isPickup=false;
        if(order.getPickupTime()!=null){
            isPickup=true;
        }
        activityOrderDetailBinding.txtStatusLabel.setText(order.getStatus());
        if(order.getStatus().equals(getString(R.string.statusProcessing))){
            if(isPickup){
                activityOrderDetailBinding.imgOrderStatus.setImageResource(R.drawable.img_received);
            } else{
                activityOrderDetailBinding.imgOrderStatus.setImageResource(R.drawable.img_preparing);
            }
            activityOrderDetailBinding.txtStatusLabel.setTextColor(ContextCompat.getColor(this, R.color.orange));
        } else if(order.getStatus().equals(getString(R.string.statusDelivering))
                ||order.getStatus().equals(getString(R.string.statusReady))){
            if(isPickup){
                activityOrderDetailBinding.imgOrderStatus.setImageResource(R.drawable.img_ready_for_pickup);
            }else{
                activityOrderDetailBinding.imgOrderStatus.setImageResource(R.drawable.img_delivering);
            }
            activityOrderDetailBinding.txtStatusLabel.setTextColor(ContextCompat.getColor(this, R.color.blue));
            activityOrderDetailBinding.txtStatusLabel.setBackground(ContextCompat.getDrawable(
                    OrderDetailActivity.this, R.drawable.order_delivering_round_text));
        } else if(order.getStatus().equals(getString(R.string.statusDelivered))){
            activityOrderDetailBinding.imgOrderStatus.setImageResource(R.drawable.img_delivered);
            activityOrderDetailBinding.txtStatusLabel.setTextColor(ContextCompat.getColor(this, R.color.green));
            activityOrderDetailBinding.txtStatusLabel.setBackground(ContextCompat.getDrawable(
                    OrderDetailActivity.this, R.drawable.order_delivered_round_text));
        } else if(order.getStatus().equals(getString(R.string.statusDeliveryFailed))
                || order.getStatus().equals(getString(R.string.statusCancelled))){
            activityOrderDetailBinding.imgOrderStatus.setImageResource(R.drawable.img_delivery_failed);
            activityOrderDetailBinding.txtStatusLabel.setTextColor(ContextCompat.getColor(this, R.color.red));
            activityOrderDetailBinding.txtStatusLabel.setBackground(ContextCompat.getDrawable(
                    OrderDetailActivity.this, R.drawable.order_failed_round_text));
        }
        activityOrderDetailBinding.txtOrderId.setText(order.getId());
        activityOrderDetailBinding.txtOrderTime.setText(
                DeliveryCardAdapter.datetimeToPickup(new DateTime(order.getDateOrder())));
        activityOrderDetailBinding.txtFromStore.setText(order.getStore().getAddress().getFormattedAddress());
        if(isPickup){
            activityOrderDetailBinding.iconStore.setImageResource(R.drawable.ic_clock);
            activityOrderDetailBinding.fromTxt.setText("Thời gian lấy");
            activityOrderDetailBinding.txtToAddress.setText(order.getUserName() + " • " +order.getPhoneNumber());
            activityOrderDetailBinding.txtFromStore.setText(DeliveryCardAdapter.datetimeToPickup(new DateTime(order.getPickupTime())));
        } else{
            activityOrderDetailBinding.txtToAddress.setText(order.getAddress().getNameReceiver() + " • " +order.getAddress().getPhone());
            activityOrderDetailBinding.tempShip.setVisibility(View.VISIBLE);
            activityOrderDetailBinding.txtShippingFee.setVisibility(View.VISIBLE);
            activityOrderDetailBinding.txtShippingFee.setText(formatter.format(order.getDeliveryCost()) + getString(R.string.vndUnit));
        }

        activityOrderDetailBinding.txtPrice.setText(formatter.format(order.getTotalProduct()) +getString(R.string.vndUnit));
        activityOrderDetailBinding.txtTotal.setText(formatter.format(order.getTotal()) +getString(R.string.vndUnit));
        if(order.getDiscount()!=null && order.getDiscount()>0){
            activityOrderDetailBinding.tempPromo.setVisibility(View.VISIBLE);
            activityOrderDetailBinding.txtPromotion.setVisibility(View.VISIBLE);
            activityOrderDetailBinding.txtPromotion.setText("- "+formatter.format(order.getDiscount()) + getString(R.string.vndUnit));
        }

        OrderProdItemAdapter adapter=new OrderProdItemAdapter(order.getProducts());
        activityOrderDetailBinding.recyclerProductInfo.setNestedScrollingEnabled(false);
        activityOrderDetailBinding.recyclerProductInfo.setLayoutManager(new LinearLayoutManager(this));
        if(order.getProducts().size()>1){
            activityOrderDetailBinding.recyclerProductInfo.addItemDecoration(new DividerItemDecoration(this,
                    DividerItemDecoration.VERTICAL));
        }
        activityOrderDetailBinding.recyclerProductInfo.setAdapter(adapter);

        // set the change order status view
        setStatusLabel(order.getStatus());
        if(order.getStatus().equals(getString(R.string.statusComplete))
        || order.getStatus().equals(getString(R.string.statusCancelled))
        || order.getStatus().equals(getString(R.string.statusDelivered))
        || order.getStatus().equals(getString(R.string.statusDeliveryFailed))){
            activityOrderDetailBinding.changeStatusView.setVisibility(View.GONE);
        } else{
            setPopupMenu(isPickup);
        }

    }

    private void setPopupMenu(boolean isPickup){
        activityOrderDetailBinding.statusChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(OrderDetailActivity.this, activityOrderDetailBinding.statusChange);
                if(isPickup){
                    //Inflating the Popup using xml file
                    popup.getMenuInflater()
                            .inflate(R.menu.set_order_status_pickup_menu, popup.getMenu());
                } else{
                    //Inflating the Popup using xml file
                    popup.getMenuInflater()
                            .inflate(R.menu.set_order_status_menu, popup.getMenu());
                }
                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getItemId()==R.id.orderProcessing){
                            setStatusLabel(item.getTitle().toString());
                        }else if(item.getItemId()==R.id.orderDelivering){
                            setStatusLabel(item.getTitle().toString());
                        }else if(item.getItemId()==R.id.orderDelivered){
                            setStatusLabel(item.getTitle().toString());
                        }else if(item.getItemId()==R.id.orderCancelled){
                            setStatusLabel(item.getTitle().toString());
                        }else if(item.getItemId()==R.id.orderFailed){
                            setStatusLabel(item.getTitle().toString());
                        } else if(item.getItemId()==R.id.orderAll){
                            setStatusLabel(item.getTitle().toString());
                        }
                        return true;
                    }
                });

                popup.show(); //showing popup menu
            }
        });
    }

    private void setStatusLabel(String status){
        Drawable background;
        int color;
        background=ContextCompat.getDrawable(
                OrderDetailActivity.this, R.drawable.order_preparing_round_text);
        color=ContextCompat.getColor(OrderDetailActivity.this, R.color.orange);
        if(status.equals(getString(R.string.statusProcessing))){
            background=ContextCompat.getDrawable(
                    OrderDetailActivity.this, R.drawable.order_preparing_round_text);
            color=ContextCompat.getColor(OrderDetailActivity.this, R.color.orange);
        }else if(status.equals(getString(R.string.statusDelivering))
        || status.equals(getString(R.string.statusReady))){
            background=ContextCompat.getDrawable(
                    OrderDetailActivity.this, R.drawable.order_delivering_round_text);
            color=ContextCompat.getColor(OrderDetailActivity.this, R.color.blue);
        }else if(status.equals(getString(R.string.statusDelivered))
        || status.equals(getString(R.string.statusComplete))){
            background=ContextCompat.getDrawable(
                    OrderDetailActivity.this, R.drawable.order_delivered_round_text);
            color=ContextCompat.getColor(OrderDetailActivity.this, R.color.green);
        }else if(status.equals(getString(R.string.statusDeliveryFailed))
        || status.equals(getString(R.string.statusCancelled))){
            background=ContextCompat.getDrawable(
                    OrderDetailActivity.this, R.drawable.order_failed_round_text);
            color=ContextCompat.getColor(OrderDetailActivity.this, R.color.red);
        }
        activityOrderDetailBinding.statusChange.setText(status);
        activityOrderDetailBinding.statusChange.setBackground(background);
        activityOrderDetailBinding.statusChange.setTextColor(color);
//        OrderOfStoreViewModel.getInstance().getStatusDeli().setValue(getString(R.string.statusAll));
    }
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private String urlSetStatus="http://103.166.182.58/orders/";
    Call post(String url, String json, Callback callback) {
        OkHttpClient client=new OkHttpClient().newBuilder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(url)
                .method("PUT", body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }
}