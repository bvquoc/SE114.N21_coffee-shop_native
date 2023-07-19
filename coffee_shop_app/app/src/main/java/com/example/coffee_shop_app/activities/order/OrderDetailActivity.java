package com.example.coffee_shop_app.activities.order;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;

import com.example.coffee_shop_app.R;
import com.example.coffee_shop_app.adapters.OrderProdItemAdapter;
import com.example.coffee_shop_app.databinding.ActivityOrderDetailBinding;
import com.example.coffee_shop_app.fragments.StoreInfoDialog;
import com.example.coffee_shop_app.models.Order;
import com.example.coffee_shop_app.repository.OrderRepository;
import com.example.coffee_shop_app.viewmodels.CartPickupViewModel;

import org.joda.time.DateTime;

import java.text.DecimalFormat;
import java.util.List;

public class OrderDetailActivity extends AppCompatActivity {
    ActivityOrderDetailBinding activityOrderDetailBinding;
    private Order order;

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

        String orderId = OrderDetailActivity.this.getIntent().getStringExtra("orderId");
        OrderRepository.getInstance().getOrderListMutableLiveData().observe(this, new Observer<List<Order>>() {
            @Override
            public void onChanged(List<Order> orders) {
//                activityProductDetailBinding.setIsLoading(true);
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
//                activityProductDetailBinding.setIsLoading(false);
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
        } else if(order.getStatus().equals(getString(R.string.statusDelivered))){
            activityOrderDetailBinding.imgOrderStatus.setImageResource(R.drawable.img_delivered);
            activityOrderDetailBinding.txtStatusLabel.setTextColor(ContextCompat.getColor(this, R.color.green));
        } else if(order.getStatus().equals(getString(R.string.statusDeliveryFailed))
                || order.getStatus().equals(getString(R.string.statusCancelled))){
            activityOrderDetailBinding.imgOrderStatus.setImageResource(R.drawable.img_delivery_failed);
            activityOrderDetailBinding.txtStatusLabel.setTextColor(ContextCompat.getColor(this, R.color.red));
        }
        activityOrderDetailBinding.txtOrderId.setText(order.getId());
        activityOrderDetailBinding.txtOrderTime.setText(
                CartPickupViewModel.datetimeToPickup(new DateTime(order.getDateOrder())));
        activityOrderDetailBinding.txtFromStore.setText(order.getStore().getAddress().getFormattedAddress());
        if(isPickup){
            activityOrderDetailBinding.iconLocation.setImageResource(R.drawable.baseline_access_time_filled_24);
            activityOrderDetailBinding.txtAddressName.setVisibility(View.GONE);
            activityOrderDetailBinding.txtAddressNote.setVisibility(View.GONE);
            activityOrderDetailBinding.txtToAddress.setText(CartPickupViewModel
                    .datetimeToPickup(new DateTime(order.getPickupTime())));
        } else{
            activityOrderDetailBinding.txtAddressName.setText(order.getAddress().getNameReceiver() + " • " +order.getAddress().getPhone());
            if(order.getAddress().getAddressNote().equals(""))
            {
                activityOrderDetailBinding.txtAddressNote.setVisibility(View.GONE);
            }
            else
            {
                activityOrderDetailBinding.txtAddressNote.setText("Note: " + order.getAddress().getAddressNote());
            }
            activityOrderDetailBinding.tempShip.setVisibility(View.VISIBLE);
            activityOrderDetailBinding.txtShippingFee.setVisibility(View.VISIBLE);
            activityOrderDetailBinding.txtShippingFee.setText(formatter.format(order.getDeliveryCost()) + getString(R.string.vndUnit));
            activityOrderDetailBinding.txtToAddress.setText(order.getAddress().getAddress().getFormattedAddress());
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

        activityOrderDetailBinding.txtContactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StoreInfoDialog dialog=new StoreInfoDialog();
                dialog.showDialog(OrderDetailActivity.this, order.getStore());
            }
        });
    }
}