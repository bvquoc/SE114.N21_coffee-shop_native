package com.example.coffee_shop_staff_admin.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;

import com.example.coffee_shop_staff_admin.R;
import com.example.coffee_shop_staff_admin.adapters.StoreAdminAdapter;
import com.example.coffee_shop_staff_admin.databinding.ActivityChooseStoreBinding;
import com.example.coffee_shop_staff_admin.repositories.StoreRepository;
import com.example.coffee_shop_staff_admin.utils.interfaces.OnStoreAdminClickListener;
import com.example.coffee_shop_staff_admin.viewmodels.ChooseStoreViewModel;

public class ChooseStoreActivity extends AppCompatActivity {
    private ActivityChooseStoreBinding activityChooseStoreBinding;
    private final ChooseStoreViewModel chooseStoreViewModel = new ChooseStoreViewModel();

    private final StoreAdminAdapter storeAdminAdapter = new StoreAdminAdapter();
    private final Handler handler = new Handler();
    private Runnable searchRunnable;
    private final int MILLISECOND_DELAY_SEARCH  = 300;
    private  final OnStoreAdminClickListener listener = (storeId, storeName) -> {
        Intent intent = new Intent();
        intent.putExtra("storeId", storeId);
        intent.putExtra("storeName", storeName);
        setResult(RESULT_OK, intent);
        finish();
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityChooseStoreBinding =
                DataBindingUtil.setContentView(this, R.layout.activity_choose_store);

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        toolbar.setTitle("Chọn cửa hàng");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        init();
    }
    private void init()
    {
        activityChooseStoreBinding.setViewModel(chooseStoreViewModel);
        storeAdminAdapter.setOnStoreAdminClickListener(listener);
        activityChooseStoreBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        activityChooseStoreBinding.recyclerView.setAdapter(storeAdminAdapter);

        StoreRepository.getInstance().getStoreListMutableLiveData().observe(this, stores -> {
            chooseStoreViewModel.setLoading(true);
            if (stores != null) {
                storeAdminAdapter.changeDataSet(stores);
                chooseStoreViewModel.setLoading(false);
            }
        });

        activityChooseStoreBinding.editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeCallbacks(searchRunnable);
                searchRunnable = () -> storeAdminAdapter.getFilter().filter(s);
                handler.postDelayed(searchRunnable, MILLISECOND_DELAY_SEARCH);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}