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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.example.coffee_shop_app.R;
import com.example.coffee_shop_app.activities.address.AddressListingActivity;
import com.example.coffee_shop_app.activities.promo.PromoActivity;
import com.example.coffee_shop_app.activities.store.StoreActivity;
import com.example.coffee_shop_app.adapters.OrderDetailAdapter;
import com.example.coffee_shop_app.databinding.ActivityCartDeliveryBinding;
import com.example.coffee_shop_app.databinding.OrderTypeBottomSheetBinding;
import com.example.coffee_shop_app.fragments.Dialog.ConfirmDialog;
import com.example.coffee_shop_app.fragments.Dialog.NotificationDialog;
import com.example.coffee_shop_app.fragments.LoadingDialog;
import com.example.coffee_shop_app.models.AddressDelivery;
import com.example.coffee_shop_app.models.CartFood;
import com.example.coffee_shop_app.models.Product;
import com.example.coffee_shop_app.models.Promo;
import com.example.coffee_shop_app.models.Store;
import com.example.coffee_shop_app.repository.AuthRepository;
import com.example.coffee_shop_app.repository.ProductRepository;
import com.example.coffee_shop_app.utils.SqliteHelper;
import com.example.coffee_shop_app.viewmodels.CartButtonViewModel;
import com.example.coffee_shop_app.viewmodels.CartDeliveryViewModel;
import com.example.coffee_shop_app.viewmodels.OrderType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CartDeliveryActivity extends AppCompatActivity {
    private ActivityCartDeliveryBinding activityCartDeliveryBinding;
    private CartDeliveryViewModel viewModel;
    private ArrayList<CartFood> cartFoods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityCartDeliveryBinding = DataBindingUtil.setContentView(this, R.layout.activity_cart_delivery);

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
    }

    @Override
    protected void onResume() {
        super.onResume();
//        init();
    }

    private void init() {
        activityCartDeliveryBinding.orderDetails.recyclerOrderDetails.setNestedScrollingEnabled(false);
        activityCartDeliveryBinding.orderDetails.recyclerOrderDetails.setLayoutManager(new LinearLayoutManager(this));
        activityCartDeliveryBinding.orderDetails.recyclerOrderDetails.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
        SqliteHelper repo = new SqliteHelper(CartDeliveryActivity.this);
        ProductRepository.getInstance().getProductListMutableLiveData().observe(this, new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> products) {
                cartFoods = repo.getCartFood(AuthRepository.getInstance().getCurrentUser().getId());
                setUICartFood();
            }
        });
        activityCartDeliveryBinding.btnApplyChevron.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(CartDeliveryActivity.this, PromoActivity.class);
                startActivity(intent);
            }
        });
        activityCartDeliveryBinding.shippingDetails.iconAddressChevron.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(CartDeliveryActivity.this, AddressListingActivity.class);
                startActivity(intent);
            }
        });
        activityCartDeliveryBinding.txtChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeOrderTypeBottomSheet = new BottomSheetDialog(CartDeliveryActivity.this, R.style.BottomSheetTheme);
                OrderTypeBottomSheetBinding orderTypeBottomSheetBinding =
                        OrderTypeBottomSheetBinding
                                .inflate(LayoutInflater.from(CartDeliveryActivity.this), null, false);
                orderTypeBottomSheetBinding.setViewModel(CartButtonViewModel.getInstance());

                orderTypeBottomSheetBinding.closeButton.setOnClickListener(view1 -> changeOrderTypeBottomSheet.dismiss());

                orderTypeBottomSheetBinding.pickUpEditButton.setOnClickListener(view12 -> {
                    changeOrderTypeBottomSheet.dismiss();
                    Intent intent = new Intent(CartDeliveryActivity.this, StoreActivity.class);
                    startActivity(intent);
                });

                orderTypeBottomSheetBinding.deliveryEditButton.setOnClickListener(view13 -> {
                    changeOrderTypeBottomSheet.dismiss();
                    Intent intent = new Intent(CartDeliveryActivity.this, AddressListingActivity.class);
                    startActivity(intent);
                });

                orderTypeBottomSheetBinding.deliveryLayout.setOnClickListener(view14 -> {
                    CartButtonViewModel.getInstance().getSelectedOrderType().postValue(OrderType.Delivery);
                    changeOrderTypeBottomSheet.dismiss();
                });
                orderTypeBottomSheetBinding.pickUpLayout.setOnClickListener(view15 -> {
                    CartButtonViewModel.getInstance().getSelectedOrderType().postValue(OrderType.StorePickUp);
                    changeOrderTypeBottomSheet.dismiss();
                    Intent intent = new Intent(CartDeliveryActivity.this,CartPickupActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
                    startActivity(intent);
                    CartDeliveryActivity.this.finish();
                });

                changeOrderTypeBottomSheet.setContentView(orderTypeBottomSheetBinding.getRoot());
                // Set the behavior to STATE_EXPANDED
                View bottomSheetInternal = changeOrderTypeBottomSheet
                        .findViewById(com.google.android.material.R.id.design_bottom_sheet);
                if (bottomSheetInternal != null) {
                    BottomSheetBehavior.from(bottomSheetInternal).setState(BottomSheetBehavior.STATE_EXPANDED);
                    changeOrderTypeBottomSheet.show();
                }
            }
        });
    }
    private BottomSheetDialog changeOrderTypeBottomSheet;
    private OrderDetailAdapter adapter;
    private void setUICartFood(){
        viewModel = new CartDeliveryViewModel();
        activityCartDeliveryBinding.setViewModel(viewModel);
        setStoreListener();
        activityCartDeliveryBinding.btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewModel.getCartViewModel().getNotAvailableCartFoods().getValue().size()>0){
                    NotificationDialog notAvaiDialog=new NotificationDialog(
                            NotificationDialog.NotificationType.failed,
                            "Có sản phẩm không hợp lệ trong giỏ hàng !", null);
                    notAvaiDialog.show(getSupportFragmentManager(), PLACEORDER);
                }else{
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

            }
        });

        adapter = new OrderDetailAdapter(cartFoods);
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
        activityCartDeliveryBinding.orderDetails.recyclerOrderDetails.setAdapter(adapter);

        viewModel.getCartViewModel().getCartFoods().setValue(cartFoods);
        viewModel.getCartViewModel().getCartFoods().observe(this, new Observer<List<CartFood>>() {
            @Override
            public void onChanged(List<CartFood> cartFoods) {
                activityCartDeliveryBinding.orderDetails
                        .txtPrice.setText(formatter.format(viewModel.getCartViewModel().getTotalFood().getValue())  + getString(R.string.vndUnit));

                adapter.setCartFoods(cartFoods);
                adapter.notifyDataSetChanged();
            }
        });
        viewModel.getFromStore().observe(this, new Observer<Store>() {
            @Override
            public void onChanged(Store store) {
                activityCartDeliveryBinding.shippingDetails.txtFromStore.setText(store.getAddress().getFormattedAddress());
            }
        });
        viewModel.getToAddress().observe(this, new Observer<AddressDelivery>() {
            @Override
            public void onChanged(AddressDelivery addressDelivery) {
                if(addressDelivery==null){
                    activityCartDeliveryBinding.shippingDetails.txtNamePhone.setVisibility(View.GONE);
                    activityCartDeliveryBinding.shippingDetails.txtToAddress.setText("Không có thông tin địa chỉ");
                }else{
                    String txt=addressDelivery.getNameReceiver()+" • " +addressDelivery.getPhone();
                    activityCartDeliveryBinding.shippingDetails.txtNamePhone.setVisibility(View.VISIBLE);
                    activityCartDeliveryBinding.shippingDetails.txtNamePhone.setText(txt);
                    activityCartDeliveryBinding.shippingDetails.txtToAddress.setText(addressDelivery.getAddress().getFormattedAddress());
                }
             }
        });
        viewModel.getTotal().observe(this, totalPrice->setTotalPriceUI(totalPrice));
        viewModel.getCartViewModel().getDiscount().observe(this, dis->setPromoPriceUI(dis));
        CartButtonViewModel
                .getInstance()
                .getDistance()
                .observe(this,
                        distance->viewModel.getDeliveryCost().setValue(calShip(distance)));
        viewModel.getDeliveryCost().observe(this,
                shipFee->setShipTextUI(shipFee));
        viewModel.getCartViewModel().getPromo().observe(this, new Observer<Promo>() {
            @Override
            public void onChanged(Promo promo) {
                if(promo!=null){
                    activityCartDeliveryBinding.txtUsePromo.setText(promo.getPromoCode());
                }else{
                    activityCartDeliveryBinding.txtUsePromo.setText("Sử dụng mã giảm giá");
                }
            }
        });
    }
    private void setStoreListener(){
        CartButtonViewModel.getInstance().getSelectedStore()
                .observe(CartDeliveryActivity.this ,new Observer<Store>() {
            @Override
            public void onChanged(Store store) {
                for (CartFood cf:
                    viewModel.getCartViewModel().getCartFoods().getValue()) {
                    List<Integer> notAvaiTempList=viewModel.getCartViewModel().
                            getNotAvailableCartFoods().getValue();
                    boolean isAvailable=!viewModel.getCartViewModel()
                            .getNotAvailableCartFoods().getValue().contains(cf.getId());
                    boolean isCurrent=true;
                    String prdId= cf.getProduct().getId();
                    if(store.getStateFood().containsKey(cf.getProduct().getId())
                    && store.getStateFood().get(prdId)!=null){
                        if(store.getStateFood().get(prdId).contains(cf.getSize())){
                            isCurrent=false;
                        }
                        if(Objects.requireNonNull(store.getStateFood().get(cf.getProduct().getId())).isEmpty()){
                            isCurrent=false;
                        }
                    }

                    if(store.getStateTopping()!=null && store.getStateTopping().size()>0){
                        if(cf.getTopping()!=null && !cf.getTopping().isEmpty()){
                            for (String storeTopping :
                                    store.getStateTopping()) {
                                if(cf.getTopping().contains(storeTopping)){
                                    isCurrent=false;
                                }
                            }
                        }
                    }

                    if(isAvailable && !isCurrent){
                        notAvaiTempList.add((Integer) cf.getId());
                        viewModel.getCartViewModel()
                                .getNotAvailableCartFoods().postValue(notAvaiTempList);
                    }

                    if(!isAvailable && isCurrent){
                        notAvaiTempList.remove((Integer) cf.getId());
                        viewModel.getCartViewModel()
                                .getNotAvailableCartFoods().postValue(notAvaiTempList);
                    }
                }
            }
        });
        viewModel.getCartViewModel().getNotAvailableCartFoods().observe(CartDeliveryActivity.this, new Observer<List<Integer>>() {
            @Override
            public void onChanged(List<Integer> integers) {
                if(adapter!=null && adapter.getItemCount()>0){
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }
    private static final double firstDistance=10;
    private static final double firstDistancePrice=15000;
    private static final double eachKmPrice=3000;
    private double calShip(double km){
        if(km<=firstDistance){
            return firstDistancePrice;
        }else{
            double price=firstDistancePrice + (Math.floor(km) -firstDistance)*eachKmPrice;
            return price>50000 ? 50000 : price;
        }
    }
    private final DecimalFormat formatter = new DecimalFormat("#,##0.##");

    private void setTotalPriceUI(double totalPrice){
        activityCartDeliveryBinding.btnPay.setText(formatter.format(totalPrice) + getString(R.string.vndUnit));
    }
    private void setPromoPriceUI(double promoPrice){
        if(promoPrice>0){
            activityCartDeliveryBinding.orderDetails.txtPromo.setVisibility(View.VISIBLE);
            activityCartDeliveryBinding.orderDetails.txtPromoPricr.setVisibility(View.VISIBLE);
            activityCartDeliveryBinding.orderDetails.txtPromoPricr.setText("- "+
                    formatter.format(promoPrice) + getString(R.string.vndUnit));
        }else{
            activityCartDeliveryBinding.orderDetails.txtPromo.setVisibility(View.GONE);
            activityCartDeliveryBinding.orderDetails.txtPromoPricr.setVisibility(View.GONE);
        }
    }
    private void setShipTextUI(double shipFee){
            activityCartDeliveryBinding.orderDetails.txtShip.setVisibility(View.VISIBLE);
            activityCartDeliveryBinding.orderDetails.txtShipStr.setVisibility(View.VISIBLE);
            activityCartDeliveryBinding.orderDetails
                    .txtShip.setText(formatter.format(viewModel.getDeliveryCost().getValue()) + getString(R.string.vndUnit));
    }
    private void showFailedDialog(String content){
        NotificationDialog alertDialog=new NotificationDialog(
                NotificationDialog.NotificationType.failed,
                content,
                null);
        alertDialog.show(getSupportFragmentManager(), PLACEORDER);
    }
    private static final String PLACEORDER="PLACE ORDER";
    private FirebaseFirestore firestore;
    private void placeOrder(){
        try{
            HashMap<String, Object> map=new HashMap<>();
            map.put("user", AuthRepository.getInstance().getCurrentUser().getId());
            map.put("store", viewModel.getFromStore().getValue().getId());

            Date now=new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm") ;

            Store store=viewModel.getFromStore().getValue();

            if(store==null){
                showFailedDialog("Chưa có thông tin cửa hàng");
                return;
            }
            //TODO: check if store open, comment to order at anytime
            try{
                if(dateFormat.parse(dateFormat.format(now)).after(dateFormat.parse(dateFormat.format(store.getTimeClose())))
                        || dateFormat.parse(dateFormat.format(now)).before(dateFormat.parse(dateFormat.format(store.getTimeOpen())))){
                    showFailedDialog("Không trong thời gian hoạt động");
                    return;
                }
            }catch (Exception e){
                Log.d(PLACEORDER, e.getMessage());
            }

            if(viewModel.getCartViewModel().getPromo().getValue()!=null){
                map.put("promo", viewModel.getCartViewModel().getPromo().getValue().getPromoCode());
            }

            //TODO: end comment

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            map.put("dateOrder", format.format(now));
            if(viewModel.getToAddress().getValue()==null){
                showFailedDialog("Chưa có thông tin địa chỉ");
                return;
            }
            map.put("address", AddressDelivery.toFireBase(viewModel.getToAddress().getValue()));

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

                CartDeliveryActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        dialog.showDialog(CartDeliveryActivity.this);
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
                                                        if(value.contains("status")){
                                                            if(value.get("status").toString().equals("Đã tạo")){
                                                                Log.d(PLACEORDER, "Đã tạo");
                                                            } else{
                                                                CartDeliveryActivity.this.runOnUiThread(new Runnable() {
                                                                    public void run() {
                                                                        SqliteHelper repo=new SqliteHelper(CartDeliveryActivity.this);
                                                                        for (CartFood cf :
                                                                                viewModel.getCartViewModel().getCartFoods().getValue()) {
                                                                            repo.deleteCartFood(cf.getId());
                                                                        }
                                                                        viewModel.getCartViewModel().getCartFoods().postValue(new ArrayList<>());
                                                                        viewModel.getCartViewModel().getPromo().postValue(null);
                                                                        NotificationDialog alertDialog=new NotificationDialog(
                                                                                NotificationDialog.NotificationType.success,
                                                                                "Đặt hàng thành công",
                                                                                dialog1 -> {
                                                                                    onBackPressed();
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
                                                null);
                                        alertDialog.show(getSupportFragmentManager(), PLACEORDER);
                                        Log.e(PLACEORDER, "Receive json error");
                                    }
                                } catch (JSONException e) {
                                    dialog.dismiss();
                                    NotificationDialog alertDialog=new NotificationDialog(
                                            NotificationDialog.NotificationType.failed,
                                            "Đặt hàng không thành công",
                                            null);
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