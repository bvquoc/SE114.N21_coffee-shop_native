package com.example.coffee_shop_app.activities.cart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.example.coffee_shop_app.Data;
import com.example.coffee_shop_app.R;
import com.example.coffee_shop_app.adapters.OrderDetailAdapter;
import com.example.coffee_shop_app.databinding.ActivityCartPickupBinding;
import com.example.coffee_shop_app.fragments.TimePickerBottomSheet;
import com.example.coffee_shop_app.models.CartFood;

import com.example.coffee_shop_app.models.Store;
import com.example.coffee_shop_app.utils.SqliteHelper;
import com.example.coffee_shop_app.viewmodels.CartPickupViewModel;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class CartPickupActivity extends AppCompatActivity {
    private ActivityCartPickupBinding activityCartPickupBinding;
    private CartPickupViewModel viewModel;
    private ArrayList<CartFood> cartFoods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityCartPickupBinding = DataBindingUtil.setContentView(this, R.layout.activity_cart_pickup);

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        toolbar.setTitle("");
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

        SqliteHelper repo = new SqliteHelper(CartPickupActivity.this);

        //TODO: get the user properly
        cartFoods = repo.getCartFood(Data.instance.userId);
        viewModel = new CartPickupViewModel();
        activityCartPickupBinding.setViewModel(viewModel);
        activityCartPickupBinding.btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: place order
//                viewModel.placeOrder(null, null, null, null);
            }
        });

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
            }
        });
        activityCartPickupBinding.orderDetails.recyclerOrderDetails.setAdapter(adapter);

        viewModel.getCartViewModel().getCartFoods().setValue(cartFoods);
        viewModel.getCartViewModel().getCartFoods().observe(this, new Observer<List<CartFood>>() {
            @Override
            public void onChanged(List<CartFood> cartFoods) {
                viewModel.calculateTotalPrice();
                activityCartPickupBinding.btnPay.setText(viewModel.getCartViewModel().getTotalFood().getValue() + getString(R.string.vndUnit));
                activityCartPickupBinding.orderDetails
                        .txtPrice.setText(viewModel.getCartViewModel().getTotalFood().getValue() + getString(R.string.vndUnit));

                activityCartPickupBinding.orderDetails.txtShip.setVisibility(View.GONE);
                activityCartPickupBinding.orderDetails.txtShipStr.setVisibility(View.GONE);

                adapter.setCartFoods(cartFoods);
                adapter.notifyDataSetChanged();
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
//        viewModel.getHourStartTimeList().observe(this, new Observer<List<Integer>>() {
//            @Override
//            public void onChanged(List<Integer> integers) {
//                if(bottomSheet!=null && bottomSheet.getDialog().isShowing()){
//                    bottomSheet.initNumberPicker();
//                }
//            }
//        });
    }

    final Handler handler = new Handler();
    int i=0;
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            viewModel.initDayTimeList();
            Log.d("TIMER", new Date().toString()+"   " +i++);
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
}