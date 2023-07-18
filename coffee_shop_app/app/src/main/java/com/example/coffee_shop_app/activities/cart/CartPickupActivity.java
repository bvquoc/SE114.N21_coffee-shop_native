package com.example.coffee_shop_app.activities.cart;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import com.example.coffee_shop_app.R;
import com.example.coffee_shop_app.activities.promo.PromoActivity;
import com.example.coffee_shop_app.adapters.OrderDetailAdapter;
import com.example.coffee_shop_app.databinding.ActivityCartPickupBinding;
import com.example.coffee_shop_app.fragments.Dialog.ConfirmDialog;
import com.example.coffee_shop_app.fragments.Dialog.NotificationDialog;
import com.example.coffee_shop_app.fragments.LoadingDialog;
import com.example.coffee_shop_app.fragments.TimePickerBottomSheet;
import com.example.coffee_shop_app.models.CartFood;

import com.example.coffee_shop_app.models.Product;
import com.example.coffee_shop_app.models.Promo;
import com.example.coffee_shop_app.models.Store;
import com.example.coffee_shop_app.repository.AuthRepository;
import com.example.coffee_shop_app.repository.ProductRepository;
import com.example.coffee_shop_app.utils.SqliteHelper;
import com.example.coffee_shop_app.viewmodels.CartPickupViewModel;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CartPickupActivity extends AppCompatActivity {
    private ActivityCartPickupBinding activityCartPickupBinding;
    private CartPickupViewModel viewModel;
    private ArrayList<CartFood> cartFoods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityCartPickupBinding = DataBindingUtil.setContentView(this, R.layout.activity_cart_pickup);

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        toolbar.setTitle("Giỏ hàng");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        init();
        startTimer();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        init();
    }

    private void init() {
        activityCartPickupBinding.orderDetails.recyclerOrderDetails.setNestedScrollingEnabled(false);
        activityCartPickupBinding.orderDetails.recyclerOrderDetails.setLayoutManager(new LinearLayoutManager(this));
        activityCartPickupBinding.orderDetails.recyclerOrderDetails.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));

        activityCartPickupBinding.orderDetails.txtShip.setVisibility(View.GONE);
        activityCartPickupBinding.orderDetails.txtShipStr.setVisibility(View.GONE);
        activityCartPickupBinding.btnApplyChevron.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(CartPickupActivity.this, PromoActivity.class);
                startActivity(intent);
            }
        });
        SqliteHelper repo = new SqliteHelper(CartPickupActivity.this);
        ProductRepository.getInstance().getProductListMutableLiveData().observe(this, new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> products) {
                //TODO: get the user properly
                cartFoods = repo.getCartFood(AuthRepository.getInstance().getCurrentUser().getId());
                setUICartFood();
            }
        });
        viewModel = new CartPickupViewModel();
        activityCartPickupBinding.setViewModel(viewModel);
        activityCartPickupBinding.btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: place order
                ConfirmDialog dialog=new ConfirmDialog(
                        "Lưu ý",
                        "Bạn sẽ không được huỷ đơn nếu xác nhận đặt hàng",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                placeOrder();
                            }
                        },
                        null);
                dialog.show(getSupportFragmentManager(), PLACEORDER);
            }
        });


        viewModel.getTimePickup().observe(this, new Observer<Date>() {
            @Override
            public void onChanged(Date date) {
                activityCartPickupBinding.pickupDetailCard.txtPickupTime
                        .setText(viewModel.datetimeToPickup(new DateTime(date)));
            }
        });
        viewModel.getStorePickup().observe(this, new Observer<Store>() {
            @Override
            public void onChanged(Store store) {
                activityCartPickupBinding.pickupDetailCard.txtPickupStore.setText(store.getAddress().getFormattedAddress());
            }
        });
        initPickupTime();
    }

    private void initPickupTime(){
        activityCartPickupBinding.pickupDetailCard.btnPickTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetDialog();
            }
        });
    }
    TimePickerBottomSheet bottomSheet;
    private void showBottomSheetDialog() {
        bottomSheet=new TimePickerBottomSheet(viewModel);
        bottomSheet.show(getSupportFragmentManager(), null);
    }

    final Handler handler = new Handler();
    int i=0;
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    viewModel.initDayTimeList();
                }
            });
            Date selectedDate=viewModel.getTimePickup().getValue();
            if(selectedDate!=null){
                DateTime date=new DateTime(selectedDate);
                DateTime now=DateTime.now();
                if((date.withTimeAtStartOfDay().isEqual(now.withTimeAtStartOfDay()))){
                    if(viewModel.getHourStartTimeList().getValue().size()==3
                    && viewModel.getDayList().getValue().size()==3){
                        int hourStartTime=viewModel.getHourStartTimeList().getValue().get(0);
                        int startHour=(hourStartTime/2);
                        int startMinute=(hourStartTime%2)*30;
                        if(date.getHourOfDay()<startHour
                        || date.getHourOfDay()==startHour
                        && date.getMinuteOfHour()<startMinute){
                            viewModel.initSelectedDate();
                        }
                    } else{
                        bottomSheet.initNumberPicker();
                    }
                }
            }
//            Log.d("TIMER", new Date().toString()+"   " +i++);
        }
    };
    Timer timer = new Timer();
    private void startTimer(){

        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(timerRunnable);
            }
        };
        timer.schedule(doAsynchronousTask, 0, 10000); //execute in every 5 second
    }
    @Override
    public void onPause() {
        super.onPause();
        timer.cancel();
        timer.purge();
        handler.removeCallbacks(timerRunnable);
    }

    private void setUICartFood(){

        OrderDetailAdapter adapter = new OrderDetailAdapter(cartFoods);
        adapter.setOnCartQuantityUpdate(new OrderDetailAdapter.OnCartQuantityUpdate() {
            @Override
            public void onItemClick(CartFood cartFood, boolean isRemoved) {
                List<CartFood> newList = new ArrayList<>();

                cartFood = new CartFood(cartFood);
                for (CartFood cf :
                        viewModel.getCartViewModel().getCartFoods().getValue()) {
                    if (cartFood.getId() != cf.getId()) {
                        newList.add(cf);
                    } else if (!isRemoved) {
                        newList.add(cartFood);
                    }
                }
                viewModel.getCartViewModel().getCartFoods().setValue(newList);
                if(newList.size()==0){
                    onBackPressed();
                }
            }
        });
        activityCartPickupBinding.orderDetails.recyclerOrderDetails.setAdapter(adapter);

        viewModel.getCartViewModel().getCartFoods().setValue(cartFoods);
        viewModel.getCartViewModel().getCartFoods().observe(this, new Observer<List<CartFood>>() {
            @Override
            public void onChanged(List<CartFood> cartFoods) {
                activityCartPickupBinding.btnPay.setText(formatter.format(viewModel.getCartViewModel().getTotalFood().getValue()) + getString(R.string.vndUnit));
                activityCartPickupBinding.orderDetails
                        .txtPrice.setText(formatter.format(viewModel.getCartViewModel().getTotalFood().getValue()) + getString(R.string.vndUnit));

                adapter.setCartFoods(cartFoods);
                adapter.notifyDataSetChanged();
            }
        });
        viewModel.getTotal().observe(this, totalPrice->setTotalPriceUI(totalPrice));
        viewModel.getCartViewModel().getDiscount().observe(this, dis->setPromoPriceUI(dis));
        viewModel.getCartViewModel().getPromo().observe(this, new Observer<Promo>() {
            @Override
            public void onChanged(Promo promo) {
                if(promo!=null){
                    activityCartPickupBinding.txtUsePromo.setText(promo.getPromoCode());
                }else{
                    activityCartPickupBinding.txtUsePromo.setText("Sử dụng mã giảm giá");
                }
            }
        });
    }
    private final   DecimalFormat formatter = new DecimalFormat("#,##0.##");
    private void setTotalPriceUI(double totalPrice){
        activityCartPickupBinding.btnPay.setText(formatter.format(totalPrice) + getString(R.string.vndUnit));
        }
        private void setPromoPriceUI(double promoPrice){
            if(promoPrice>0){
                activityCartPickupBinding.orderDetails.txtPromo.setVisibility(View.VISIBLE);
                activityCartPickupBinding.orderDetails.txtPromoPricr.setVisibility(View.VISIBLE);
                activityCartPickupBinding.orderDetails.txtPromoPricr.setText("- "+
                        formatter.format(promoPrice) + getString(R.string.vndUnit));
            }else{
                activityCartPickupBinding.orderDetails.txtPromo.setVisibility(View.GONE);
                activityCartPickupBinding.orderDetails.txtPromoPricr.setVisibility(View.GONE);
            }
        }
    private static final String PLACEORDER="PLACE ORDER";
    private FirebaseFirestore firestore;
    public void placeOrder(){
        try{
            HashMap<String, Object> map=new HashMap<>();
            map.put("user", AuthRepository.getInstance().getCurrentUser().getId());
            map.put("store", viewModel.getStorePickup().getValue().getId());

            //TODO: put promo
//            if(viewModel.getCartViewModel().getPromo().getValue()!=null){
//                map.put("promo", viewModel.getCartViewModel().getPromo().getValue().getPromoCode());
//            }

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            map.put("dateOrder", format.format(new Date()));

            map.put("pickupTime", format.format(viewModel.getTimePickup().getValue()));
            List<HashMap<String, Object>> listFoods=new ArrayList<>();
            for (CartFood cf :
                    viewModel.getCartViewModel().getCartFoods().getValue()) {
                listFoods.add(CartFood.toMap(cf));
            }
            map.put("orderedFoods", listFoods);

            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            String json = ow.writeValueAsString(map);
//            Log.d(PLACEORDER, json);
            try {
                LoadingDialog dialog=new LoadingDialog();

                CartPickupActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        dialog.showDialog(CartPickupActivity.this);
                    }
                });
                post(urlPlaceOrder, json, new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        Log.e("POST", e.getMessage());
                    }
                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        if (response.isSuccessful()) {
                            int code=response.code();
                            if(response.code()==201){
                                try {
                                    String jsonData = response.body().string();
                                    JSONObject token = new JSONObject(jsonData);
                                    if(token.has("orderID")){
                                        String orderId=token.get("orderID").toString();
                                        firestore = FirebaseFirestore.getInstance();
                                        firestore.collection("beorders")
                                                .document(orderId)
                                                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                            @Override
                                            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                                //TODO: add listener
                                                if(value.contains("status")){
                                                    if(value.get("status").toString().equals("Đã tạo")){
                                                        Log.d(PLACEORDER, "Đã tạo");
                                                    } else{
                                                        CartPickupActivity.this.runOnUiThread(new Runnable() {
                                                            public void run() {
                                                                SqliteHelper repo=new SqliteHelper(CartPickupActivity.this);
                                                                for (CartFood cf :
                                                                        viewModel.getCartViewModel().getCartFoods().getValue()) {
                                                                    repo.deleteCartFood(cf.getId());
                                                                }
                                                                viewModel.getCartViewModel().getCartFoods().postValue(new ArrayList<>());
                                                                NotificationDialog alertDialog=new NotificationDialog(
                                                                        NotificationDialog.NotificationType.success,
                                                                        "Đặt hàng thành công",
                                                                        new View.OnClickListener() {
                                                                            @Override
                                                                            public void onClick(View v) {
                                                                                dialog.dismiss();
                                                                                onBackPressed();
                                                                            }
                                                                        });
                                                                dialog.dismiss();
                                                                alertDialog.show(getSupportFragmentManager(), PLACEORDER);
                                                            }
                                                        });
                                                        Log.d(PLACEORDER, "Tạo thành công");
                                                    }
                                                }
                                            }
                                        });
                                        Log.d(PLACEORDER, "orderID" + orderId);
                                    }else{
                                        dialog.dismiss();
                                        NotificationDialog alertDialog=new NotificationDialog(
                                                NotificationDialog.NotificationType.failed,
                                                "Đặt hàng không thành công",
                                                new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        dialog.dismiss();
                                                        onBackPressed();
                                                    }
                                                });
                                        alertDialog.show(getSupportFragmentManager(), PLACEORDER);
                                        Log.e(PLACEORDER, "Receive json error");
                                    }
                                } catch (JSONException e) {
                                    dialog.dismiss();
                                    NotificationDialog alertDialog=new NotificationDialog(
                                            NotificationDialog.NotificationType.failed,
                                            "Đặt hàng không thành công",
                                            new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    dialog.dismiss();
                                                    onBackPressed();
                                                }
                                            });
                                    alertDialog.show(getSupportFragmentManager(), PLACEORDER);
                                    Log.e(PLACEORDER, e.getMessage());
                                }
                            }
                        }
                        else {
                            if(response.code()==400){
                                dialog.dismiss();
                                Log.d(PLACEORDER, "Invalid request body");
                            }
                        }
                    }
                });
            } catch (Exception e) {
                Log.e("POST", e.getMessage());
            }
        }catch (Exception e){
            Log.d("PLACE ORDER", "placeOrder: failed");
        }
    }
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private String urlPlaceOrder="http://103.166.182.58/orders";
    Call post(String url, String json, Callback callback) {
        OkHttpClient client=new OkHttpClient().newBuilder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(url)
                .method("POST", body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }
}