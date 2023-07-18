package com.example.coffee_shop_app.activities.cart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.coffee_shop_app.R;
import com.example.coffee_shop_app.activities.address.AddressListingActivity;
import com.example.coffee_shop_app.activities.promo.PromoActivity;
import com.example.coffee_shop_app.adapters.OrderDetailAdapter;
import com.example.coffee_shop_app.databinding.ActivityCartDeliveryBinding;
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

import java.text.DecimalFormat;
import java.util.ArrayList;
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
    }

    private void setUICartFood(){
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
}