package com.example.coffee_shop_app.activities.store;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.coffee_shop_app.R;
import com.example.coffee_shop_app.adapters.StoreAdapter;
import com.example.coffee_shop_app.databinding.ActivityStoreBinding;
import com.example.coffee_shop_app.models.Store;
import com.example.coffee_shop_app.repository.StoreRepository;
import com.example.coffee_shop_app.utils.interfaces.OnStoreClickListener;
import com.example.coffee_shop_app.viewmodels.CartButtonViewModel;
import com.example.coffee_shop_app.viewmodels.StoreViewModel;

import java.util.ArrayList;
import java.util.List;

public class StoreActivity extends AppCompatActivity {
    private ActivityStoreBinding activityStoreBinding;
    private final StoreAdapter nearestStoreAdapter = new StoreAdapter(new ArrayList<>());
    private final StoreAdapter favoriteStoresAdapter = new StoreAdapter(new ArrayList<>());
    private final StoreAdapter otherStoresAdapter = new StoreAdapter(new ArrayList<>());
    private final OnStoreClickListener listener = storeId -> {
        List<Store> storeList = StoreRepository.getInstance().getStoreListMutableLiveData().getValue();
        Store selectedStore = null;
        if(storeList!=null)
        {
            for (Store store: storeList) {
                if(store.getId().equals(storeId))
                {
                    selectedStore = store;
                    break;
                }
            }
            CartButtonViewModel.getInstance().getSelectedStore().postValue(selectedStore);
            finish();
        }
        else
        {
            Toast.makeText(StoreActivity.this, "Đã có lỗi xảy ra, xin hãy thử lại sau.", Toast.LENGTH_SHORT).show();
        }
    };
    private final ActivityResultLauncher<Intent> activityFindStoreResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        String storeId = data.getStringExtra("storeId");
                        List<Store> storeList = StoreRepository.getInstance().getStoreListMutableLiveData().getValue();
                        Store selectedStore = null;
                        if(storeList!=null)
                        {
                            for (Store store:
                                    storeList) {
                                if(store.getId().equals(storeId))
                                {
                                    selectedStore = store;
                                    break;
                                }
                            }
                            CartButtonViewModel.getInstance().getSelectedStore().postValue(selectedStore);
                            finish();
                        }
                        else
                        {
                            Toast.makeText(StoreActivity.this, "Đã có lỗi xảy ra, xin hãy thử lại sau.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        activityStoreBinding = DataBindingUtil.setContentView(this, R.layout.activity_store);

        init();
    }
    private void init()
    {
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        toolbar.setTitle("Cửa hàng");
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        nearestStoreAdapter.setOnClickListener(listener);
        favoriteStoresAdapter.setOnClickListener(listener);
        otherStoresAdapter.setOnClickListener(listener);

        activityStoreBinding.nearestStore.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        activityStoreBinding.nearestStore.setAdapter(nearestStoreAdapter);

        activityStoreBinding.favoriteStores.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        activityStoreBinding.favoriteStores.setAdapter(favoriteStoresAdapter);

        activityStoreBinding.otherStores.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        activityStoreBinding.otherStores.setAdapter(otherStoresAdapter);
        activityStoreBinding.findStoreFrame.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), StoreSearchActivity.class);
            intent.putExtra("isPurposeForShowingDetail", false);
            activityFindStoreResultLauncher.launch(intent);
        });
        StoreViewModel storeViewModel = new StoreViewModel();
        storeViewModel.getNearestStoreMutableLiveData().observe(this, nearestStore ->{
            if(nearestStore == null)
            {
                storeViewModel.setHasNearestStore(false);
                nearestStoreAdapter.changeDataSet(new ArrayList<>());
            }
            else
            {
                storeViewModel.setHasNearestStore(true);

                List<Store> storeList = new ArrayList<>();
                storeList.add(nearestStore);
                nearestStoreAdapter.changeDataSet(storeList);
            }
        });
        storeViewModel.getFavoriteStores().observe(this, favoriteStores ->{
            if(favoriteStores==null || favoriteStores.size() == 0)
            {
                storeViewModel.setHasFavoriteStores(false);

                favoriteStoresAdapter.changeDataSet(new ArrayList<>());
            }
            else
            {
                storeViewModel.setHasFavoriteStores(true);

                favoriteStoresAdapter.changeDataSet(favoriteStores);
            }

        });
        storeViewModel.getOtherStores().observe(this, otherStores ->{
            if(otherStores!=null)
            {
                otherStoresAdapter.changeDataSet(otherStores);
                if(otherStores.size() == 0)
                {
                    storeViewModel.setOtherText("");
                }
                else if(storeViewModel.isHasNearestStore() || storeViewModel.isHasFavoriteStores())
                {
                    storeViewModel.setOtherText("Khác");
                }
                else{
                    storeViewModel.setOtherText("Tất cả");
                }
            }
            else
            {
                otherStoresAdapter.changeDataSet(new ArrayList<>());
                storeViewModel.setOtherText("");
            }
        });
        storeViewModel.getIsLoading().observe(this, isLoading ->{
            if(isLoading)
            {
                activityStoreBinding.shimmerLayout.setVisibility(View.VISIBLE);
                activityStoreBinding.nestedScrollView.setVisibility(View.GONE);
                activityStoreBinding.searchTextBox.setVisibility(View.GONE);
                activityStoreBinding.shimmerLayout.startShimmer();
            }
            else
            {
                activityStoreBinding.shimmerLayout.setVisibility(View.GONE);
                activityStoreBinding.nestedScrollView.setVisibility(View.VISIBLE);
                activityStoreBinding.searchTextBox.setVisibility(View.VISIBLE);
                activityStoreBinding.shimmerLayout.stopShimmer();
            }
        });
        activityStoreBinding.setViewModel(storeViewModel);
    }
}