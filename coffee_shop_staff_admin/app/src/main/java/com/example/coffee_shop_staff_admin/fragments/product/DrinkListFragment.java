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
import com.example.coffee_shop_staff_admin.models.Store;
import com.example.coffee_shop_staff_admin.models.StoreProduct;
import com.example.coffee_shop_staff_admin.repositories.FoodRepository;
import com.example.coffee_shop_staff_admin.repositories.StoreRepository;
import com.example.coffee_shop_staff_admin.utils.validation.TextValidator;
import com.example.coffee_shop_staff_admin.viewmodels.product.ProductOfStoreViewModel;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class DrinkListFragment extends Fragment {
    private RecyclerView recyclerView;
    private StaffProductCardAdapter adapter;
    private LinearLayout noProductView;
    private SwipeRefreshLayout refreshLayout;

    ProductOfStoreViewModel viewModel;

    TextInputEditText searchText;
    CheckBox isStocking, unStocking, inComplete;
    List<StoreProduct> listAll, listSearch;

    public DrinkListFragment() {
        // Required empty public constructor
        viewModel = ProductOfStoreViewModel.getInstance();
        listAll = new ArrayList<>();
        listSearch = new ArrayList<>();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_drink_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerDrinkCard);
        noProductView = view.findViewById(R.id.noProductView);
        refreshLayout = view.findViewById(R.id.refreshLayout);

        //filter
        searchText = view.findViewById(R.id.txt_search_product);
        isStocking = view.findViewById(R.id.product_stocking_check);
        unStocking = view.findViewById(R.id.product_unstocking_check);
        inComplete = view.findViewById(R.id.product_incomplete_check);

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
        viewModel.getDrinkListLiveData().observe(getViewLifecycleOwner(), listDrink -> {
            setListDrink(listDrink);
        });
    }

    private void setListDrink(List<StoreProduct> drinks) {
        listAll = drinks;
        listSearch = search(drinks, searchText.getText().toString());
        //Filter
        List<StoreProduct> drinkList = filter(listSearch, isStocking.isChecked(), unStocking.isChecked(), inComplete.isChecked());

        //Set adapter
        adapter = new StaffProductCardAdapter(drinkList);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        //Set empty
        if (drinkList.size() == 0 && !ProductOfStoreViewModel.getInstance().isLoading()) {
            noProductView.setVisibility(View.VISIBLE);
        } else {
            noProductView.setVisibility(View.GONE);
        }
    }

    private  List<StoreProduct> filter(List<StoreProduct> raw, Boolean isStocking, Boolean unStocking, Boolean inComplete){
        List<StoreProduct> res = new ArrayList<>();
        if(!isStocking && !unStocking && !inComplete){
            res = raw;
        }
        else if(isStocking && unStocking && inComplete){
            res = raw;
        }
        else {
            if(isStocking){
                res.addAll(raw.stream().filter(item -> item.getStocking()).collect(Collectors.toList()));
            }
            if(unStocking){
                res.addAll(raw.stream().filter(item -> !item.getStocking()).collect(Collectors.toList()));
            }
            if(inComplete){
                res.addAll(raw.stream().filter(item -> {
                    Food food = (Food) item.getProduct();
                    FoodChecker itemRaw = (FoodChecker) item;
                    if(itemRaw.getBlockSize() != null && !itemRaw.getBlockSize().isEmpty() && itemRaw.getBlockSize().size() < food.getSizes().size()){
                        return true;
                    }
                    return false;
                }).collect(Collectors.toList()));
            }
        }
        return res;
    }

    private List<StoreProduct> search(List<StoreProduct> raw, String textSearch){
        List<StoreProduct> res = new ArrayList<>();
        if(textSearch == null || textSearch.isEmpty()){
            return raw;
        }

        res = raw.stream().filter(item -> ((Food) item.getProduct()).getName().toLowerCase().contains(textSearch.toLowerCase())).collect(Collectors.toList());
        return res;
    }

    private void setFilterListener(){
        searchText.addTextChangedListener(new TextValidator(searchText) {
            @Override
            public void validate(TextView textView, String text) {
                setListDrink(listAll);
            }
        });

        isStocking.setOnClickListener(v -> {
            setListDrink(listAll);
        });

        unStocking.setOnClickListener(v -> {
            setListDrink(listAll);
        });

        inComplete.setOnClickListener(v -> {
            setListDrink(listAll);
        });
    }
}