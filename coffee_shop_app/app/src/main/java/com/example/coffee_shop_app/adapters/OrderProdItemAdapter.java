package com.example.coffee_shop_app.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.coffee_shop_app.R;
import com.example.coffee_shop_app.activities.ProductDetailActivity;
import com.example.coffee_shop_app.databinding.OrderProdItemBinding;
import com.example.coffee_shop_app.models.OrderFood;
import com.example.coffee_shop_app.utils.ItemClickedListener;

import java.text.DecimalFormat;
import java.util.List;

public class OrderProdItemAdapter extends RecyclerView.Adapter<OrderProdItemAdapter.ViewHolder> {
    private LayoutInflater layoutInflater;
    private List<OrderFood> orderFoods;

    public OrderProdItemAdapter(List<OrderFood> orderFoods) {
        this.orderFoods = orderFoods;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(layoutInflater==null){
            layoutInflater=LayoutInflater.from(parent.getContext());
        }
        OrderProdItemBinding orderProdItemBinding = DataBindingUtil
                .inflate(layoutInflater, R.layout.order_prod_item, parent, false);
        return new OrderProdItemAdapter.ViewHolder(orderProdItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindOrderPickupCardItem(orderFoods.get(position));
    }

    @Override
    public int getItemCount() {
        return orderFoods.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private OrderProdItemBinding orderProdItemBinding;

        public void setItemClickListener(ItemClickedListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        private ItemClickedListener itemClickListener;

        public ViewHolder(OrderProdItemBinding orderProdItemBinding) {
            super(orderProdItemBinding.getRoot());
//            itemView.setOnClickListener(this);
            this.orderProdItemBinding=orderProdItemBinding;
        }
        public void bindOrderPickupCardItem(OrderFood orderFood){
            Context context=orderProdItemBinding.getRoot().getContext();

            orderProdItemBinding.txtNameProd.setText(orderFood.getName());
            orderProdItemBinding.txtSize.setText(context
                    .getString(R.string.size_text) +" " +orderFood.getSize());
            if(orderFood.getTopping()!=null && !orderFood.getTopping().isEmpty()){
                orderProdItemBinding.txtTopping.setVisibility(View.VISIBLE);
                orderProdItemBinding.txtTopping.setText(context
                        .getString(R.string.topping_text) +" " +orderFood.getTopping());
            }
            if(orderFood.getNote()!=null && !orderFood.getNote().isEmpty()){
                orderProdItemBinding.txtNote.setVisibility(View.VISIBLE);
                orderProdItemBinding.txtNote.setText(orderFood.getNote());
            }
            DecimalFormat formatter = new DecimalFormat("#,##0.##");
            orderProdItemBinding.txtPriceQuantity.setText(
                    formatter.format(orderFood.getUnitPrice())
                            + context.getString(R.string.vndUnit)
                            +" x" +orderFood.getQuantity());
            Glide.with(context)
                    .load(Uri.parse(orderFood.getImage()))
                    .placeholder(R.drawable.img_placeholder)
                    .error(R.drawable.img_placeholder)
                    .fitCenter()
                    .into(orderProdItemBinding.imgProduct);
            orderProdItemBinding.imgProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(context, ProductDetailActivity.class);
                    intent.putExtra("productId", orderFood.getIdFood());
                    context.startActivity(intent);
                }
            });
        }
        @Override
        public void onClick(View v) {
            itemClickListener.onItemClick(getAdapterPosition());
        }
    }
}
