package com.example.coffee_shop_app.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coffee_shop_app.R;
import com.example.coffee_shop_app.activities.order.OrderDetailActivity;
import com.example.coffee_shop_app.databinding.OrderCardBinding;
import com.example.coffee_shop_app.models.Order;
import com.example.coffee_shop_app.repository.OrderRepository;
import com.example.coffee_shop_app.viewmodels.CartPickupViewModel;

import org.joda.time.DateTime;

import java.text.DecimalFormat;
import java.util.List;

public class DeliveryCardAdapter extends RecyclerView.Adapter<DeliveryCardAdapter.ViewHolder> {

    private LayoutInflater layoutInflater;
    private List<Order> orders;

    public DeliveryCardAdapter(List<Order> orders) {
        this.orders = orders;
    }

    @NonNull
    @Override
    public DeliveryCardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(layoutInflater==null){
            layoutInflater=LayoutInflater.from(parent.getContext());
        }
        OrderCardBinding orderCardBinding = DataBindingUtil
                .inflate(layoutInflater, R.layout.order_card, parent, false);
        return new DeliveryCardAdapter.ViewHolder(orderCardBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull DeliveryCardAdapter.ViewHolder holder, int position) {
        holder.onBindOrderCard(orders.get(position));
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private OrderCardBinding orderCardBinding;

        TextView txtStatusLabel;
        public ViewHolder(OrderCardBinding orderCardBinding) {
            super(orderCardBinding.getRoot());
            txtStatusLabel=itemView.findViewById(R.id.txtStatusLabel);
            this.orderCardBinding=orderCardBinding;
        }

        private void onBindOrderCard(Order order){
            DecimalFormat formatter = new DecimalFormat("#,##0.##");
            Context context=orderCardBinding.getRoot().getContext();
            orderCardBinding.fromTxt.setText(order.getStore().getAddress().getFormattedAddress());
            orderCardBinding.toTxt.setText(order.getAddress().getAddress().getFormattedAddress());
            orderCardBinding.txtOrderDate.setText(
                    CartPickupViewModel.datetimeToPickup(new DateTime( order.getDateOrder())));
            orderCardBinding.txtProductList.setText(OrderRepository.orderFoodsToString(order));
            orderCardBinding.txtPrice.setText(formatter.format(order.getTotal()) + context.getString(R.string.vndUnit));

            orderCardBinding.txtStatusLabel.setText(order.getStatus());

            if(order.getStatus().equals(context
                    .getResources().getString(R.string.statusProcessing))){
                orderCardBinding.txtStatusLabel.setBackground(ContextCompat.getDrawable(
                                context, R.drawable.order_preparing_round_text));
                orderCardBinding.txtStatusLabel.setTextColor(ContextCompat.getColor(context, R.color.orange));
            } else if(order.getStatus().equals(context.getString(R.string.statusDelivering))){
                orderCardBinding.txtStatusLabel.setBackground(ContextCompat.getDrawable(
                        context, R.drawable.order_delivering_round_text));
                orderCardBinding.txtStatusLabel.setTextColor(ContextCompat.getColor(context, R.color.blue));
            } else if(order.getStatus().equals(context.getString(R.string.statusDelivered))){
                orderCardBinding.txtStatusLabel.setBackground(ContextCompat.getDrawable(
                        context, R.drawable.order_delivered_round_text));
                orderCardBinding.txtStatusLabel.setTextColor(ContextCompat.getColor(context, R.color.green));
            } else if(order.getStatus().equals(context.getString(R.string.statusDeliveryFailed))
            || order.getStatus().equals(context.getString(R.string.statusCancelled))){
                orderCardBinding.txtStatusLabel.setBackground(ContextCompat.getDrawable(
                        context, R.drawable.order_failed_round_text));
                orderCardBinding.txtStatusLabel.setTextColor(ContextCompat.getColor(context, R.color.red));
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(context, OrderDetailActivity.class);
                    intent.putExtra("orderId", order.getId());
                    context.startActivity(intent);
                }
            });
        }
    }
}
