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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.coffee_shop_app.R;
import com.example.coffee_shop_app.adapters.StoreAdapter;
import com.example.coffee_shop_app.databinding.ActivityStoreBinding;
import com.example.coffee_shop_app.databinding.FragmentStoresBinding;
import com.example.coffee_shop_app.fragments.StoresFragment;
import com.example.coffee_shop_app.models.Store;
import com.example.coffee_shop_app.repository.StoreRepository;
import com.example.coffee_shop_app.utils.interfaces.OnStoreClickListener;
import com.example.coffee_shop_app.viewmodels.CartButtonViewModel;
import com.example.coffee_shop_app.viewmodels.StoreViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class StoreActivity extends AppCompatActivity {
    private ActivityStoreBinding activityStoreBinding;
    private StoreAdapter nearestStoreAdapter = new StoreAdapter(new ArrayList<Store>());
    private StoreAdapter favoriteStoresAdapter = new StoreAdapter(new ArrayList<Store>());
    private StoreAdapter otherStoresAdapter = new StoreAdapter(new ArrayList<Store>());
    private int previousLocation;
    private OnStoreClickListener listener = new OnStoreClickListener() {
        @Override
        public void onStoreClick(String storeId) {
            List<Store> storeList = StoreRepository.getInstance().getStoreListMutableLiveData().getValue();
            Store selectedStore = null;
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
    } ;
    private ActivityResultLauncher<Intent> activityFindStoreResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        String storeId = data.getStringExtra("storeId");
                        List<Store> storeList = StoreRepository.getInstance().getStoreListMutableLiveData().getValue();
                        Store selectedStore = null;
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
                } else {
                    //User do nothing
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
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        nearestStoreAdapter.setOnClickListener(listener);
        favoriteStoresAdapter.setOnClickListener(listener);
        otherStoresAdapter.setOnClickListener(listener);

        activityStoreBinding.nearestStore.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        activityStoreBinding.nearestStore.setAdapter(nearestStoreAdapter);

        activityStoreBinding.favoriteStores.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        activityStoreBinding.favoriteStores.setAdapter(favoriteStoresAdapter);

        activityStoreBinding.otherStores.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        activityStoreBinding.otherStores.setAdapter(otherStoresAdapter);
        activityStoreBinding.findStoreFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previousLocation = activityStoreBinding.nestedScrollView.getScrollY();
                Intent intent = new Intent(getApplicationContext(), StoreSearchActivity.class);
                intent.putExtra("isPurposeForShowingDetail", false);
                activityFindStoreResultLauncher.launch(intent);
            }
        });
        StoreViewModel storeViewModel = new StoreViewModel();
        storeViewModel.getNearestStoreMutableLiveData().observe(this, nearestStore ->{
            if(nearestStore == null)
            {
                storeViewModel.setHasNearestStore(false);
                nearestStoreAdapter.changeDataSet(new ArrayList<Store>());
            }
            else
            {
                storeViewModel.setHasNearestStore(true);

                List<Store> storeList = new ArrayList<Store>();
                storeList.add(nearestStore);
                nearestStoreAdapter.changeDataSet(storeList);
            }
        });
        storeViewModel.getFavoriteStores().observe(this, favoriteStores ->{
            if(favoriteStores.size() == 0)
            {
                storeViewModel.setHasFavoriteStores(false);

                favoriteStoresAdapter.changeDataSet(new ArrayList<Store>());
            }
            else
            {
                storeViewModel.setHasFavoriteStores(true);

                favoriteStoresAdapter.changeDataSet(favoriteStores);
            }

        });
        storeViewModel.getOtherStores().observe(this, otherStores ->{
            otherStoresAdapter.changeDataSet(otherStores);
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
    public interface OnStoreTouchListener {
        void onStoreTouch(String storeId);
    }
}