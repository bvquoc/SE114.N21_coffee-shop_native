package com.example.coffee_shop_app.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.coffee_shop_app.R;
import com.example.coffee_shop_app.adapters.PickupCardAdapter;
import com.example.coffee_shop_app.models.Order;
import com.example.coffee_shop_app.repository.OrderRepository;
import com.example.coffee_shop_app.viewmodels.OrderViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OrderPickupListFragment extends Fragment {

    private RecyclerView recyclerView;
    private PickupCardAdapter adapter;
    private boolean isHistory=false;
    private LinearLayout noOrderView;
    private SwipeRefreshLayout refreshLayout;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isHistory = getArguments().getBoolean("isHistory");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order_pickup_list, container, false);
    }

    public static OrderPickupListFragment newInstance(boolean isHistory) {
        OrderPickupListFragment fragment = new OrderPickupListFragment();
        Bundle args = new Bundle();
        args.putBoolean("isHistory", isHistory);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView= view.findViewById(R.id.recyclerPickupCard);
        noOrderView=view.findViewById(R.id.noOrderView);
        refreshLayout=view.findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                OrderRepository.getInstance().registerSnapshotListener();
                refreshLayout.setRefreshing(false);
            }
        });
        if(isHistory){
            OrderViewModel.getInstance().getOrderPickupListMutableLiveData().observe(getViewLifecycleOwner(), new Observer<List<Order>>() {
                @Override
                public void onChanged(List<Order> orders) {
                    List<Order> historyList=orders
                            .stream()
                            .filter(ord->ord.getStatus().equals(getString(R.string.statusDelivered))
                                    || ord.getStatus().equals(getString(R.string.statusComplete))
                                    ||ord.getStatus().equals(getString(R.string.statusCancelled))
                                    ||ord.getStatus().equals(getString(R.string.statusDeliveryFailed)))
                            .collect(Collectors.toList());
                    adapter=new PickupCardAdapter(historyList);
                    recyclerView.setNestedScrollingEnabled(false);
                    recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
                    recyclerView.setAdapter(adapter);
                    if(historyList.size()==0 && !OrderViewModel.getInstance().isLoading()){
                        noOrderView.setVisibility(View.VISIBLE);
                    }else{
                        noOrderView.setVisibility(View.GONE);
                    }
                }
            });
        } else{
            OrderViewModel.getInstance().getOrderPickupListMutableLiveData().observe(getViewLifecycleOwner(), new Observer<List<Order>>() {
                @Override
                public void onChanged(List<Order> orders) {
                    List<Order> normalList=orders
                            .stream()
                            .filter(ord-> !ord.getStatus().equals(getString(R.string.statusDelivered))
                                    && !ord.getStatus().equals(getString(R.string.statusComplete))
                                    && !ord.getStatus().equals(getString(R.string.statusCancelled))
                                    && !ord.getStatus().equals(getString(R.string.statusDeliveryFailed)))
                            .collect(Collectors.toList());
                    adapter=new PickupCardAdapter(normalList);
                    recyclerView.setNestedScrollingEnabled(false);
                    recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
                    recyclerView.setAdapter(adapter);
                    if(normalList.size()==0 && !OrderViewModel.getInstance().isLoading()){
                        noOrderView.setVisibility(View.VISIBLE);
                    }else{
                        noOrderView.setVisibility(View.GONE);
                    }
                }
            });
        }
    }
}