package com.example.coffee_shop_staff_admin.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.coffee_shop_staff_admin.R;
import com.example.coffee_shop_staff_admin.adapters.DeliveryCardAdapter;
import com.example.coffee_shop_staff_admin.models.Order;
import com.example.coffee_shop_staff_admin.repositories.OrderRepository;
import com.example.coffee_shop_staff_admin.viewmodels.OrderOfStoreViewModel;

import java.util.List;
import java.util.stream.Collectors;

public class OrderPickupListFragment extends Fragment {

    TextView txtStatusLabel;
    private RecyclerView recyclerView;
    private DeliveryCardAdapter adapter;
    private LinearLayout noOrderView;
    private SwipeRefreshLayout refreshLayout;

    public OrderPickupListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order_pickup_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        txtStatusLabel=view.findViewById(R.id.txtStatusLabel);
        recyclerView= view.findViewById(R.id.recyclerDeliveryCard);
        noOrderView=view.findViewById(R.id.noOrderView);
        refreshLayout=view.findViewById(R.id.refreshLayout);
        getOrder();
        setPopupMenu();
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                OrderRepository.getInstance().registerSnapshotListener();
                refreshLayout.setRefreshing(false);
            }
        });

    }
    private void getOrder(){
        OrderOfStoreViewModel.getInstance().getOrderPickupListMutableLiveData().observe(getViewLifecycleOwner(), new Observer<List<Order>>() {
            @Override
            public void onChanged(List<Order> orders) {
                String status=OrderOfStoreViewModel.getInstance().getStatusPickup().getValue();
                setListOrderPickup(orders, status);
            }
        });

        OrderOfStoreViewModel.getInstance().getStatusPickup().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                setListOrderPickup(OrderOfStoreViewModel.getInstance().getOrderPickupListMutableLiveData().getValue(), s);
            }
        });
    }
    private void setListOrderPickup(List<Order> orders, String status){
        List<Order> listOrders;
        if(status.equals("Tất cả")){
            listOrders=orders;
        } else{
            listOrders=orders.stream()
                    .filter(ord->ord.getStatus().equals(status))
                    .collect(Collectors.toList());
        }
        adapter=new DeliveryCardAdapter(listOrders);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        if(listOrders.size()==0 && !OrderOfStoreViewModel.getInstance().isLoading()){
            noOrderView.setVisibility(View.VISIBLE);
        }else{
            noOrderView.setVisibility(View.GONE);
        }
    }

    private void setPopupMenu(){
        txtStatusLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(getContext(), txtStatusLabel);
                //Inflating the Popup using xml file
                popup.getMenuInflater()
                        .inflate(R.menu.order_status_pickup_menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getItemId()==R.id.orderProcessing){
                            txtStatusLabel.setText(getString(R.string.statusProcessing));
                            txtStatusLabel.setBackground(ContextCompat.getDrawable(
                                    getContext(), R.drawable.order_preparing_round_text));
                            txtStatusLabel.setTextColor(ContextCompat.getColor(getContext(), R.color.orange));
                            OrderOfStoreViewModel.getInstance().getStatusPickup().setValue(getString(R.string.statusProcessing));
                        }else if(item.getItemId()==R.id.orderDelivering){
                            txtStatusLabel.setText(getString(R.string.statusReady));
                            txtStatusLabel.setBackground(ContextCompat.getDrawable(
                                    getContext(), R.drawable.order_delivering_round_text));
                            txtStatusLabel.setTextColor(ContextCompat.getColor(getContext(), R.color.blue));
                            OrderOfStoreViewModel.getInstance().getStatusPickup().setValue(getString(R.string.statusReady));
                        }else if(item.getItemId()==R.id.orderDelivered){
                            txtStatusLabel.setText(getString(R.string.statusComplete));
                            txtStatusLabel.setBackground(ContextCompat.getDrawable(
                                    getContext(), R.drawable.order_delivered_round_text));
                            txtStatusLabel.setTextColor(ContextCompat.getColor(getContext(), R.color.green));
                            OrderOfStoreViewModel.getInstance().getStatusPickup().setValue(getString(R.string.statusComplete));
                        }else if(item.getItemId()==R.id.orderCancelled){
                            txtStatusLabel.setText(getString(R.string.statusCancelled));
                            txtStatusLabel.setBackground(ContextCompat.getDrawable(
                                    getContext(), R.drawable.order_failed_round_text));
                            txtStatusLabel.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
                            OrderOfStoreViewModel.getInstance().getStatusPickup().setValue(getString(R.string.statusCancelled));
                        }else if(item.getItemId()==R.id.orderAll){
                            txtStatusLabel.setText(getString(R.string.statusAll));
                            txtStatusLabel.setBackground(ContextCompat.getDrawable(
                                    getContext(), R.drawable.order_all_status_round));
                            txtStatusLabel.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                            OrderOfStoreViewModel.getInstance().getStatusPickup().setValue(getString(R.string.statusAll));
                        }
                        return true;
                    }
                });
                popup.show(); //showing popup menu
            }
        });
    }
}