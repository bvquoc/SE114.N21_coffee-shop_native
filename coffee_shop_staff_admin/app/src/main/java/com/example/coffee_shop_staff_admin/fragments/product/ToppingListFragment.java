package com.example.coffee_shop_staff_admin.fragments.product;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.coffee_shop_staff_admin.R;
import com.example.coffee_shop_staff_admin.adapters.product.StaffProductCardAdapter;
import com.example.coffee_shop_staff_admin.models.StoreProduct;
import com.example.coffee_shop_staff_admin.repositories.ToppingRepository;
import com.example.coffee_shop_staff_admin.viewmodels.product.ProductOfStoreViewModel;

import java.util.List;

public class ToppingListFragment extends Fragment {

    private RecyclerView recyclerView;
    private StaffProductCardAdapter adapter;
    private LinearLayout noProductView;
    private SwipeRefreshLayout refreshLayout;

    ProductOfStoreViewModel viewModel;

    public ToppingListFragment() {
        // Required empty public constructor
        viewModel = ProductOfStoreViewModel.getInstance();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_topping_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerDrinkCard);
        noProductView = view.findViewById(R.id.noProductView);
        refreshLayout = view.findViewById(R.id.refreshLayout);

        getDrink();

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //Load drink list
                ToppingRepository.getInstance().registerSnapshotListener();
                refreshLayout.setRefreshing(false);
            }
        });
    }

    private void getDrink() {
        viewModel.getToppingListLiveData().observe(getViewLifecycleOwner(), listDrink -> {
            setListTopping(listDrink, "filter", 0);
        });
    }

    private void setListTopping(List<StoreProduct> toppings, String text, int status) {
        //Filter
        List<StoreProduct> toppingList;
        if (true) {
            toppingList = toppings;
        } else {

        }

        //Set adapter
        adapter = new StaffProductCardAdapter(toppingList);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        //Set empty
        if (toppingList.size() == 0 && !ProductOfStoreViewModel.getInstance().isLoading()) {
            noProductView.setVisibility(View.VISIBLE);
        } else {
            noProductView.setVisibility(View.GONE);
        }
    }
}