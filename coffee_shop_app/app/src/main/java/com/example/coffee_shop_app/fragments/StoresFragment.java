package com.example.coffee_shop_app.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.coffee_shop_app.R;
import com.example.coffee_shop_app.activities.store.StoreActivity;
import com.example.coffee_shop_app.activities.store.StoreDetailActivity;
import com.example.coffee_shop_app.adapters.StoreAdapter;
import com.example.coffee_shop_app.databinding.FragmentStoresBinding;
import com.example.coffee_shop_app.models.Store;
import com.example.coffee_shop_app.viewmodels.StoreViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class StoresFragment extends Fragment {
    private static final String TAG = "StoreFragment";
    private FragmentStoresBinding fragmentStoresBinding;
    private StoreAdapter nearestStoreAdapter = new StoreAdapter(new ArrayList<Store>());
    private StoreAdapter favoriteStoresAdapter = new StoreAdapter(new ArrayList<Store>());
    private StoreAdapter otherStoresAdapter = new StoreAdapter(new ArrayList<Store>());
    private int previousLocation;
    private OnStoreTouchListener listener = new OnStoreTouchListener() {
        @Override
        public void onStoreTouch(String storeId) {
            Intent intent = new Intent(getContext(), StoreDetailActivity.class);
            intent.putExtra("storeId", storeId);
            activitySeeStoreDetailResultLauncher.launch(intent);
        }
    } ;
    private ActivityResultLauncher<Intent> activityFindStoreResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottomNavView);
                        bottomNavigationView.setSelectedItemId(R.id.menuFragment);
                        StoreDetailActivity.OrderType orderType = (StoreDetailActivity.OrderType)data.getSerializableExtra("orderType");
                        String storeId = data.getStringExtra("storeId");
                        Toast.makeText(
                                getContext(),
                                orderType.toString() + storeId,
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                } else {
                    //User do nothing
                }
                fragmentStoresBinding.nearestStore.clearFocus();
                fragmentStoresBinding.favoriteStores.clearFocus();
                fragmentStoresBinding.otherStores.clearFocus();
                fragmentStoresBinding.nestedScrollView.requestFocus();
                fragmentStoresBinding.nestedScrollView.scrollTo(0, previousLocation);
            }
    );
    private ActivityResultLauncher<Intent> activitySeeStoreDetailResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottomNavView);
                        bottomNavigationView.setSelectedItemId(R.id.menuFragment);
                        StoreDetailActivity.OrderType orderType =
                                (StoreDetailActivity.OrderType)data.getSerializableExtra("orderType");
                        String storeId = data.getStringExtra("storeId");
                        Toast.makeText(
                                getContext(),
                                orderType.toString() + storeId,
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                } else {
                    //User don't choose "Mang đi" or "Giao hàng"
                }
            }
    );

    public StoresFragment() {
        // Required empty public constructor
    }

    public static StoresFragment newInstance() {
        StoresFragment fragment = new StoresFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }
    void setToolBarTittle()
    {
        Toolbar toolbar = ((AppCompatActivity)requireActivity()).findViewById(R.id.my_toolbar);
        toolbar.setTitle("Cửa hàng");
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setToolBarTittle();
        nearestStoreAdapter.setOnTouchListener(listener);
        favoriteStoresAdapter.setOnTouchListener(listener);
        otherStoresAdapter.setOnTouchListener(listener);
        //set databinding
        fragmentStoresBinding = FragmentStoresBinding.inflate(inflater, container, false);

        fragmentStoresBinding.nearestStore.setLayoutManager(new LinearLayoutManager(getContext()));
        fragmentStoresBinding.nearestStore.setAdapter(nearestStoreAdapter);

        fragmentStoresBinding.favoriteStores.setLayoutManager(new LinearLayoutManager(getContext()));
        fragmentStoresBinding.favoriteStores.setAdapter(favoriteStoresAdapter);

        fragmentStoresBinding.otherStores.setLayoutManager(new LinearLayoutManager(getContext()));
        fragmentStoresBinding.otherStores.setAdapter(otherStoresAdapter);
        fragmentStoresBinding.findStoreFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previousLocation = fragmentStoresBinding.nestedScrollView.getScrollY();
                Intent intent = new Intent(getActivity(), StoreActivity.class);
                activityFindStoreResultLauncher.launch(intent);
            }
        });
        StoreViewModel storeViewModel = new StoreViewModel();
        storeViewModel.getNearestStoreMutableLiveData().observe(getViewLifecycleOwner(), nearestStore ->{
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
        storeViewModel.getFavoriteStores().observe(getViewLifecycleOwner(), favoriteStores ->{
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
        storeViewModel.getOtherStores().observe(getViewLifecycleOwner(), otherStores ->{
            otherStoresAdapter.changeDataSet(otherStores);
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
    public interface OnStoreTouchListener {
        void onStoreTouch(String storeId);
    }
}
