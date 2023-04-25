package com.example.coffee_shop_app.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;

import com.example.coffee_shop_app.Data;
import com.example.coffee_shop_app.R;
import com.example.coffee_shop_app.utils.styles.RecyclerViewGapDecoration;
import com.example.coffee_shop_app.adapters.AddressListingItemAdapter;

public class AddressListingActivity extends AppCompatActivity {
    RecyclerView addressListingRecyclerView;
    AddressListingItemAdapter addressListingItemAdapter;
    Toolbar toolbar;
    LinearLayout newAddressLinearLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_listing);

        addressListingRecyclerView = findViewById(R.id.address_listing_recyclerview);
        newAddressLinearLayout = findViewById(R.id.add_address_linear);
        toolbar = findViewById(R.id.my_toolbar);

        float dp = TypedValue
                .applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        1,
                        getResources().getDisplayMetrics()
                );

        toolbar.setTitle("Deliver to");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        addressListingRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        addressListingRecyclerView.addItemDecoration(new RecyclerViewGapDecoration((int) (12*dp)));
        addressListingItemAdapter = new AddressListingItemAdapter(Data.instance.addressDeliveries);
        addressListingRecyclerView.setAdapter(addressListingItemAdapter);

        newAddressLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = getApplicationContext();
                Intent intent = new Intent(context, EditDeliveryAddressActivity.class);
                intent.putExtra("index", -1);
                context.startActivity(intent);
            }
        });
    }
}