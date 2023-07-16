package com.example.coffee_shop_staff_admin.adapters;

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


import com.example.coffee_shop_staff_admin.R;
import com.example.coffee_shop_staff_admin.activities.OrderDetailActivity;
import com.example.coffee_shop_staff_admin.databinding.OrderManageCartBinding;
import com.example.coffee_shop_staff_admin.models.Order;
import com.example.coffee_shop_staff_admin.repositories.OrderRepository;

import org.joda.time.DateTime;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class DeliveryCardAdapter extends RecyclerView.Adapter<DeliveryCardAdapter.ViewHolder> {

    private LayoutInflater layoutInflater;
    private List<Order> orders;

    public DeliveryCardAdapter(List<Order> orders) {
        this.orders = orders;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(layoutInflater==null){
            layoutInflater=LayoutInflater.from(parent.getContext());
        }
        OrderManageCartBinding orderCardBinding = DataBindingUtil
                .inflate(layoutInflater, R.layout.order_manage_cart, parent, false);
        return new ViewHolder(orderCardBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.onBindOrderCard(orders.get(position));
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private OrderManageCartBinding orderCardBinding;

        TextView txtStatusLabel;
        public ViewHolder(OrderManageCartBinding orderCardBinding) {
            super(orderCardBinding.getRoot());
            txtStatusLabel=itemView.findViewById(R.id.txtStatusLabel);
            this.orderCardBinding=orderCardBinding;
        }

        private void onBindOrderCard(Order order){
            DecimalFormat formatter = new DecimalFormat("#,##0.##");
            Context context=orderCardBinding.getRoot().getContext();
            orderCardBinding.fromTxt.setText(order.getId());
            if(order.getPickupTime()!=null){
                orderCardBinding.toTxt.setText(datetimeToPickup(new DateTime(order.getPickupTime())));
                orderCardBinding.iconLocation.setImageDrawable(ContextCompat.getDrawable(
                        context, R.drawable.ic_clock));
            } else {
                orderCardBinding.toTxt.setText(order.getAddress().getAddress().getFormattedAddress());
            }
            orderCardBinding.txtOrderDate.setText(datetimeToPickup(new DateTime( order.getDateOrder())));
            orderCardBinding.txtProductList.setText(OrderRepository.orderFoodsToString(order));
            orderCardBinding.txtPrice.setText(formatter.format(order.getTotal()) + context.getString(R.string.vndUnit));

            orderCardBinding.txtStatusLabel.setText(order.getStatus());

            if(order.getStatus().equals(context
                    .getResources().getString(R.string.statusProcessing))){
                orderCardBinding.txtStatusLabel.setBackground(ContextCompat.getDrawable(
                                context, R.drawable.order_preparing_round_text));
                orderCardBinding.txtStatusLabel.setTextColor(ContextCompat.getColor(context, R.color.orange));
            } else if(order.getStatus().equals(context.getString(R.string.statusDelivering))
            || order.getStatus().equals(context.getString(R.string.statusReady))){
                orderCardBinding.txtStatusLabel.setBackground(ContextCompat.getDrawable(
                        context, R.drawable.order_delivering_round_text));
                orderCardBinding.txtStatusLabel.setTextColor(ContextCompat.getColor(context, R.color.blue));
            } else if(order.getStatus().equals(context.getString(R.string.statusDelivered))
            ||order.getStatus().equals(context.getString(R.string.statusComplete))){
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
    public static String datetimeToPickup(DateTime dateTime) {
        DateTime now = DateTime.now();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy") ;
        String day=dateFormat.format(dateTime.toDate());
        String hour=datetimeToHour(dateTime);
        if (now.getMonthOfYear() == dateTime.getMonthOfYear()
                && dateTime.getYear() == now.getYear()) {
            if (now.getDayOfMonth()== dateTime.getDayOfMonth()) {
                day = "Hôm nay";
            } else if (now.getDayOfMonth() == dateTime.getDayOfMonth() - 1) {
                day = "Ngày mai";
            }
        }

        return hour+", "+day;
    }
    public static String datetimeToHour(DateTime dateTime) {
        int hour = dateTime.getHourOfDay();
        int minute=dateTime.getMinuteOfHour();
        String hourString= hour < 10 ? "0"+hour : hour+"";
        String minuteString=minute<10? "0"+minute:minute+"";
        return hourString+ ":"+minuteString;
    }
}
