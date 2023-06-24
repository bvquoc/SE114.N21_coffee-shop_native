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
import com.example.coffee_shop_app.utils.SqliteHelper;
import com.example.coffee_shop_app.viewmodels.CartViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CartDeliveryActivity extends AppCompatActivity {
    private ActivityCartDeliveryBinding activityCartDeliveryBinding;
    private CartViewModel viewModel;
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
        init();
    }

    private void init() {
        activityCartDeliveryBinding.orderDetails.recyclerOrderDetails.setNestedScrollingEnabled(false);
        activityCartDeliveryBinding.orderDetails.recyclerOrderDetails.setLayoutManager(new LinearLayoutManager(this));
        activityCartDeliveryBinding.orderDetails.recyclerOrderDetails.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));

        SqliteHelper repo = new SqliteHelper(CartDeliveryActivity.this);
        ArrayList<HashMap<String, Object>> items = repo.getCartFood("1");
        viewModel = new ViewModelProvider(this).get(CartViewModel.class);
        activityCartDeliveryBinding.setViewModel(viewModel);
        activityCartDeliveryBinding.btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: open the comment
                /*viewModel.placeOrder(null, new AddressDelivery(
                        new Address("nice", "quao", "1", "3"),
                        "name",
                        "0123456"), null, "test");*/
            }
        });
        cartFoods = new ArrayList<>();
        for (HashMap<String, Object> item :
                items) {
            Product prd = Data.instance.products
                    .stream()
                    .filter(p -> p.getId().equals(item.get("foodId")))
                    .findFirst()
                    .orElse(null);
            CartFood cartFood = new CartFood(prd, item.get("size").toString(), 0);
            cartFood.setId(Integer.valueOf((String) item.get("id")));
            if (item.get("topping") != null) {
                cartFood.setTopping(item.get("topping").toString());
            }
            cartFood.setQuantity((int) item.get("quantity"));
            cartFoods.add(cartFood);
        }
        OrderDetailAdapter adapter = new OrderDetailAdapter();
        adapter.setOnCartQuantityUpdate(new OrderDetailAdapter.OnCartQuantityUpdate() {
            @Override
            public void onItemClick(CartFood cartFood, boolean isRemoved) {
                List<CartFood> newList = new ArrayList<>();
                cartFood = new CartFood(cartFood);
                for (CartFood cf :
                        viewModel.getCartFoods().getValue()) {
                    if (cartFood.getId() != cf.getId()) {
                        newList.add(cf);
                    } else if (!isRemoved) {
                        newList.add(cartFood);
                    }
                }
                viewModel.getCartFoods().setValue(newList);
            }
        });
        activityCartDeliveryBinding.orderDetails.recyclerOrderDetails.setAdapter(adapter);

        viewModel.getCartFoods().setValue(cartFoods);
        viewModel.getCartFoods().observe(this, new Observer<List<CartFood>>() {
            @Override
            public void onChanged(List<CartFood> cartFoods) {
                viewModel.calculateTotalPrice();
                activityCartDeliveryBinding.btnPay.setText(viewModel.getTotal().getValue() + getString(R.string.vndUnit));
                activityCartDeliveryBinding.orderDetails
                        .txtPrice.setText(viewModel.getTotalFood().getValue() + getString(R.string.vndUnit));

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

    }
}