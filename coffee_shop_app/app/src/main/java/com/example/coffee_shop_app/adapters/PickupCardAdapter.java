package com.example.coffee_shop_app.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coffee_shop_app.R;
import com.example.coffee_shop_app.activities.order.OrderDetailActivity;
import com.example.coffee_shop_app.databinding.OrderPickupCardBinding;
import com.example.coffee_shop_app.models.Order;
import com.example.coffee_shop_app.repository.OrderRepository;
import com.example.coffee_shop_app.utils.ItemClickedListener;
import com.example.coffee_shop_app.viewmodels.CartPickupViewModel;

import org.joda.time.DateTime;

import java.text.DecimalFormat;
import java.util.List;

public class PickupCardAdapter extends RecyclerView.Adapter<PickupCardAdapter.ViewHolder> {
    private LayoutInflater layoutInflater;
    private List<Order> orders;
    public PickupCardAdapter(List<Order> orders) {
        this.orders=orders;
    }


    @NonNull
    @Override
    public PickupCardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(layoutInflater==null){
            layoutInflater=LayoutInflater.from(parent.getContext());
        }
        OrderPickupCardBinding orderPickupCardBinding = DataBindingUtil
                .inflate(layoutInflater, R.layout.order_pickup_card, parent, false);
        return new PickupCardAdapter.ViewHolder(orderPickupCardBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull PickupCardAdapter.ViewHolder holder, int position) {
        holder.bindOrderPickupCardItem(orders.get(position));
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private OrderPickupCardBinding orderPickupCardBinding;

        public void setItemClickListener(ItemClickedListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        private ItemClickedListener itemClickListener;
        public ViewHolder(OrderPickupCardBinding orderPickupCardBinding) {
            super(orderPickupCardBinding.getRoot());
            this.orderPickupCardBinding=orderPickupCardBinding;
        }
        public void bindOrderPickupCardItem(Order order){
            DecimalFormat formatter = new DecimalFormat("#,##0.##");
            Context context=orderPickupCardBinding.getRoot().getContext();

            orderPickupCardBinding.fromTxt.setText(order.getStore().getAddress().getFormattedAddress());
            orderPickupCardBinding.txtOrderDate.setText(CartPickupViewModel
                    .datetimeToPickup(new DateTime(order.getDateOrder())));
            orderPickupCardBinding.txtTimePickup.setText(CartPickupViewModel
                    .datetimeToPickup(new DateTime(order.getPickupTime())));
            orderPickupCardBinding.txtProductList.setText(OrderRepository.orderFoodsToString(order));
            orderPickupCardBinding.txtPrice.setText(formatter.format(order.getTotal()) + context.getString(R.string.vndUnit));

            orderPickupCardBinding.txtStatusLabel.setText(order.getStatus());
            if(order.getStatus().equals(context
                    .getResources().getString(R.string.statusProcessing))){
                orderPickupCardBinding.txtStatusLabel.setBackground(ContextCompat.getDrawable(
                        context, R.drawable.order_preparing_round_text));
                orderPickupCardBinding.txtStatusLabel.setTextColor(ContextCompat.getColor(context, R.color.orange));
            } else if(order.getStatus().equals(context.getString(R.string.statusReady))){
                orderPickupCardBinding.txtStatusLabel.setBackground(ContextCompat.getDrawable(
                        context, R.drawable.order_delivering_round_text));
                orderPickupCardBinding.txtStatusLabel.setTextColor(ContextCompat.getColor(context, R.color.blue));
            } else if(order.getStatus().equals(context.getString(R.string.statusComplete))){
                orderPickupCardBinding.txtStatusLabel.setBackground(ContextCompat.getDrawable(
                        context, R.drawable.order_delivered_round_text));
                orderPickupCardBinding.txtStatusLabel.setTextColor(ContextCompat.getColor(context, R.color.green));
            } else if(order.getStatus().equals(context.getString(R.string.statusDeliveryFailed))
                    || order.getStatus().equals(context.getString(R.string.statusCancelled))){
                orderPickupCardBinding.txtStatusLabel.setBackground(ContextCompat.getDrawable(
                        context, R.drawable.order_failed_round_text));
                orderPickupCardBinding.txtStatusLabel.setTextColor(ContextCompat.getColor(context, R.color.red));
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
