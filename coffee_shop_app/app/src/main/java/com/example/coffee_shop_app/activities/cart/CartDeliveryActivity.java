package com.example.coffee_shop_app.activities.cart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;

import com.example.coffee_shop_app.Data;
import com.example.coffee_shop_app.R;
import com.example.coffee_shop_app.adapters.OrderDetailAdapter;
import com.example.coffee_shop_app.databinding.ActivityCartDeliveryBinding;
import com.example.coffee_shop_app.models.Address;
import com.example.coffee_shop_app.models.AddressDelivery;
import com.example.coffee_shop_app.models.CartFood;
import com.example.coffee_shop_app.models.Product;
import com.example.coffee_shop_app.models.Store;
import com.example.coffee_shop_app.repository.ProductRepository;
import com.example.coffee_shop_app.utils.SqliteHelper;
import com.example.coffee_shop_app.viewmodels.CartDeliveryViewModel;
import com.example.coffee_shop_app.viewmodels.CartViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CartDeliveryActivity extends AppCompatActivity {
    private ActivityCartDeliveryBinding activityCartDeliveryBinding;
    private CartDeliveryViewModel viewModel;
    private ArrayList<CartFood> cartFoods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityCartDeliveryBinding = DataBindingUtil.setContentView(this, R.layout.activity_cart_delivery);

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
        cartFoods = repo.getCartFood(Data.instance.userId);
        viewModel = new CartDeliveryViewModel();
        activityCartDeliveryBinding.setViewModel(viewModel);
        activityCartDeliveryBinding.btnPay.setOnClickListener(new View.OnClickListener() {
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
        activityCartDeliveryBinding.orderDetails.recyclerOrderDetails.setAdapter(adapter);

        viewModel.getCartViewModel().getCartFoods().setValue(cartFoods);
        viewModel.getCartViewModel().getCartFoods().observe(this, new Observer<List<CartFood>>() {
            @Override
            public void onChanged(List<CartFood> cartFoods) {
                viewModel.calculateTotalPrice();
                activityCartDeliveryBinding.btnPay.setText(viewModel.getTotal().getValue() + getString(R.string.vndUnit));
                activityCartDeliveryBinding.orderDetails
                        .txtPrice.setText(viewModel.getCartViewModel().getTotalFood().getValue() + getString(R.string.vndUnit));

                if (viewModel.getDeliveryCost().getValue() == null) {
                    activityCartDeliveryBinding.orderDetails.txtShip.setVisibility(View.GONE);
                    activityCartDeliveryBinding.orderDetails.txtShipStr.setVisibility(View.GONE);

                } else {
                    activityCartDeliveryBinding.orderDetails
                            .txtShip.setText(viewModel.getDeliveryCost().getValue() + getString(R.string.vndUnit));
                }
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
                String txt=addressDelivery.getNameReceiver()+" • " +addressDelivery.getPhone();
                activityCartDeliveryBinding.shippingDetails.txtToAddress.setText(txt);
                activityCartDeliveryBinding.shippingDetails.toTxt.setText(addressDelivery.getAddress().getFormattedAddress());
            }
        });
    }
}