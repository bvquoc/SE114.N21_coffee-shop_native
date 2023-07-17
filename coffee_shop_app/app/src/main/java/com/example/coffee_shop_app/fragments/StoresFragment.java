package com.example.coffee_shop_app.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.coffee_shop_app.R;
import com.example.coffee_shop_app.activities.store.StoreSearchActivity;
import com.example.coffee_shop_app.activities.store.StoreDetailActivity;
import com.example.coffee_shop_app.adapters.StoreAdapter;
import com.example.coffee_shop_app.databinding.FragmentStoresBinding;
import com.example.coffee_shop_app.models.Store;
import com.example.coffee_shop_app.repository.ProductRepository;
import com.example.coffee_shop_app.repository.StoreRepository;
import com.example.coffee_shop_app.utils.interfaces.OnStoreClickListener;
import com.example.coffee_shop_app.viewmodels.CartButtonViewModel;
import com.example.coffee_shop_app.viewmodels.OrderType;
import com.example.coffee_shop_app.viewmodels.StoreViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StoresFragment extends Fragment {
    private FragmentStoresBinding fragmentStoresBinding;
    private final StoreAdapter nearestStoreAdapter = new StoreAdapter(new ArrayList<>());
    private final StoreAdapter favoriteStoresAdapter = new StoreAdapter(new ArrayList<>());
    private final StoreAdapter otherStoresAdapter = new StoreAdapter(new ArrayList<>());
    private final OnStoreClickListener listener = new OnStoreClickListener() {
        @Override
        public void onStoreClick(String storeId) {
            Intent intent = new Intent(getContext(), StoreDetailActivity.class);
            intent.putExtra("storeId", storeId);
            intent.putExtra("isPurposeForShowingDetail", true);
            activitySeeStoreDetailResultLauncher.launch(intent);
        }
    } ;
    private final ActivityResultLauncher<Intent> activityFindStoreResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        OrderType orderType = (OrderType)data.getSerializableExtra("orderType");
                        String storeId = data.getStringExtra("storeId");
                        if(CartButtonViewModel.getInstance().getSelectedOrderType().getValue()!= orderType)
                        {
                            CartButtonViewModel.getInstance().getSelectedOrderType().postValue(orderType);
                        }
                        List<Store> storeList = StoreRepository.getInstance().getStoreListMutableLiveData().getValue();
                        if(storeList!=null)
                        {
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
                            BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottomNavView);
                            bottomNavigationView.setSelectedItemId(R.id.menuFragment);
                        }
                        else
                        {
                            Toast.makeText(
                                    getContext(),
                                    "Đã có lỗi xảy ra. Xin hãy thử lại sau.",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }
                }
            }
    );
    private final ActivityResultLauncher<Intent> activitySeeStoreDetailResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        OrderType orderType =
                                (OrderType)data.getSerializableExtra("orderType");
                        String storeId = data.getStringExtra("storeId");
                        if(CartButtonViewModel.getInstance().getSelectedOrderType().getValue()!= orderType)
                        {
                            CartButtonViewModel.getInstance().getSelectedOrderType().postValue(orderType);
                        }
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
                            BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottomNavView);
                            bottomNavigationView.setSelectedItemId(R.id.menuFragment);
                        }
                        else
                        {
                            Toast.makeText(
                                    getContext(),
                                    "Đã có lỗi xảy ra. Xin hãy thử lại sau.",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }
                }
            }
    );

    public StoresFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    void setToolBarTittle()
    {
        Toolbar toolbar = requireActivity().findViewById(R.id.my_toolbar);
        toolbar.setTitle("Cửa hàng");
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setToolBarTittle();
        nearestStoreAdapter.setOnClickListener(listener);
        favoriteStoresAdapter.setOnClickListener(listener);
        otherStoresAdapter.setOnClickListener(listener);

        fragmentStoresBinding = FragmentStoresBinding.inflate(inflater, container, false);

        fragmentStoresBinding.nearestStore.setLayoutManager(new LinearLayoutManager(getContext()));
        fragmentStoresBinding.nearestStore.setAdapter(nearestStoreAdapter);

        fragmentStoresBinding.favoriteStores.setLayoutManager(new LinearLayoutManager(getContext()));
        fragmentStoresBinding.favoriteStores.setAdapter(favoriteStoresAdapter);

        fragmentStoresBinding.otherStores.setLayoutManager(new LinearLayoutManager(getContext()));
        fragmentStoresBinding.otherStores.setAdapter(otherStoresAdapter);

        fragmentStoresBinding.refreshLayout.setOnRefreshListener(() -> {
            StoreRepository.getInstance().registerSnapshotListener();
            fragmentStoresBinding.refreshLayout.setRefreshing(false);
        });

        fragmentStoresBinding.findStoreFrame.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), StoreSearchActivity.class);
            activityFindStoreResultLauncher.launch(intent);
        });

        StoreViewModel storeViewModel = new StoreViewModel();
        storeViewModel.getNearestStoreMutableLiveData().observe(getViewLifecycleOwner(), nearestStore ->{
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
        storeViewModel.getFavoriteStores().observe(getViewLifecycleOwner(), favoriteStores ->{
            if(favoriteStores == null || favoriteStores.size() == 0)
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
        storeViewModel.getOtherStores().observe(getViewLifecycleOwner(), otherStores ->{
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
            }
        });
        storeViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading ->{
            if(isLoading)
            {
                fragmentStoresBinding.shimmerLayout.setVisibility(View.VISIBLE);
                fragmentStoresBinding.nestedScrollView.setVisibility(View.GONE);
                fragmentStoresBinding.searchTextBox.setVisibility(View.GONE);
                fragmentStoresBinding.shimmerLayout.startShimmer();
            }
            else
            {
                fragmentStoresBinding.shimmerLayout.setVisibility(View.GONE);
                fragmentStoresBinding.nestedScrollView.setVisibility(View.VISIBLE);
                fragmentStoresBinding.searchTextBox.setVisibility(View.VISIBLE);
                fragmentStoresBinding.shimmerLayout.stopShimmer();
            }
        });
        fragmentStoresBinding.setViewModel(storeViewModel);

        return fragmentStoresBinding.getRoot();
    }
}
