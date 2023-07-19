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
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.coffee_shop_staff_admin.R;
import com.example.coffee_shop_staff_admin.adapters.product.StaffProductCardAdapter;
import com.example.coffee_shop_staff_admin.models.Food;
import com.example.coffee_shop_staff_admin.models.FoodChecker;
import com.example.coffee_shop_staff_admin.models.StoreProduct;
import com.example.coffee_shop_staff_admin.models.Topping;
import com.example.coffee_shop_staff_admin.repositories.StoreRepository;
import com.example.coffee_shop_staff_admin.repositories.ToppingRepository;
import com.example.coffee_shop_staff_admin.utils.validation.TextValidator;
import com.example.coffee_shop_staff_admin.viewmodels.product.ProductOfStoreViewModel;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ToppingListFragment extends Fragment {

    private RecyclerView recyclerView;
    private StaffProductCardAdapter adapter;
    private LinearLayout noProductView;
    private SwipeRefreshLayout refreshLayout;

    ProductOfStoreViewModel viewModel;

    TextInputEditText searchText;
    CheckBox isStocking, unStocking;
    List<StoreProduct> listAll, listSearch;

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

        //filter
        searchText = view.findViewById(R.id.txt_search_topping);
        isStocking = view.findViewById(R.id.topping_stocking_check);
        unStocking = view.findViewById(R.id.topping_unstocking_check);

        getDrink();
        setFilterListener();

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //Load drink list
                StoreRepository.getInstance().registerSnapshotListener();
                refreshLayout.setRefreshing(false);
            }
        });
    }

    private void getDrink() {
        viewModel.getToppingListLiveData().observe(getViewLifecycleOwner(), listDrink -> {
            setListTopping(listDrink);
        });
    }

    private void setListTopping(List<StoreProduct> toppings) {
        listAll = toppings;
        listSearch = search(toppings, searchText.getText().toString());

        //Filter
        List<StoreProduct> toppingList = filter(listSearch, isStocking.isChecked(), unStocking.isChecked());

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

    private  List<StoreProduct> filter(List<StoreProduct> raw, Boolean isStocking, Boolean unStocking){
        List<StoreProduct> res = new ArrayList<>();
        if(raw == null){
            return res;
        }
        if(!isStocking && !unStocking){
            res = raw;
        }
        else if(isStocking && unStocking){
            res = raw;
        }
        else {
            if(isStocking){
                res.addAll(raw.stream().filter(item -> item.getStocking()).collect(Collectors.toList()));
            }
            if(unStocking){
                res.addAll(raw.stream().filter(item -> !item.getStocking()).collect(Collectors.toList()));
            }
        }
        return res;
    }

    private List<StoreProduct> search(List<StoreProduct> raw, String textSearch){
        List<StoreProduct> res = new ArrayList<>();
        if(textSearch == null || textSearch.isEmpty()){
            return raw;
        }
        if(raw == null){
            return res;
        }

        res = raw.stream().filter(item -> ((Topping) item.getProduct()).getName().toLowerCase().contains(textSearch.toLowerCase())).collect(Collectors.toList());
        return res;
    }

    private void setFilterListener(){
        searchText.addTextChangedListener(new TextValidator(searchText) {
            @Override
            public void validate(TextView textView, String text) {
                setListTopping(listAll);
            }
        });

        isStocking.setOnClickListener(v -> {
            setListTopping(listAll);
        });

        unStocking.setOnClickListener(v -> {
            setListTopping(listAll);
        });
    }
}