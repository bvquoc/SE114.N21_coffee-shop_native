package com.example.coffee_shop_app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coffee_shop_app.R;
import com.example.coffee_shop_app.databinding.OrderDetailItemBinding;
import com.example.coffee_shop_app.fragments.ConfirmDialog;
import com.example.coffee_shop_app.models.CartFood;
import com.example.coffee_shop_app.utils.SqliteHelper;

import java.text.DecimalFormat;
import java.util.List;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.ViewHolder> {
    private List<CartFood> cartFoods;
    private LayoutInflater layoutInflater;
    private OnCartQuantityUpdate onCartQuantityUpdate;

    public void setOnCartQuantityUpdate(OnCartQuantityUpdate onCartQuantityUpdate) {
        this.onCartQuantityUpdate = onCartQuantityUpdate;
    }

    public OrderDetailAdapter(List<CartFood> cartFoods) {
        this.cartFoods=cartFoods;
    }

    public void setCartFoods(List<CartFood> cartFoods) {
        this.cartFoods = cartFoods;
    }


    @NonNull
    @Override
    public OrderDetailAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(layoutInflater==null){
            layoutInflater=LayoutInflater.from(parent.getContext());
        }
        OrderDetailItemBinding orderDetailItemBinding= DataBindingUtil.inflate(layoutInflater, R.layout.order_detail_item, parent, false);
        return new OrderDetailAdapter.ViewHolder(orderDetailItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderDetailAdapter.ViewHolder holder, int position) {
        holder.bindCartFoodItem(cartFoods.get(position));
    }

    @Override
    public int getItemCount() {
        return cartFoods.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private OrderDetailItemBinding orderDetailItemBinding;

        public ViewHolder(OrderDetailItemBinding orderDetailItemBinding) {
            super(orderDetailItemBinding.getRoot());
            this.orderDetailItemBinding = orderDetailItemBinding;
        }

        public void bindCartFoodItem(CartFood cartFood) {
            DecimalFormat formatter = new DecimalFormat("#,##0.##");
            orderDetailItemBinding.setCartFood(cartFood);
            orderDetailItemBinding.executePendingBindings();
            orderDetailItemBinding.txtPrice.setText(formatter.format(cartFood.getTotalPrice()) + orderDetailItemBinding.getRoot().getContext().getString(R.string.vndUnit));
            orderDetailItemBinding.btnMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SqliteHelper repo = new SqliteHelper(orderDetailItemBinding.getRoot().getContext());
                    int quantity = cartFood.getQuantity() - 1;
                    if (quantity == 0) {
                        ConfirmDialog dialog = new ConfirmDialog(
                                "Confirmation",
                                "Do you want to remove this product?",
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        repo.deleteCartFood(cartFood.getId());
                                        cartFoods.remove(cartFood);
                                        notifyItemRemoved(getAdapterPosition());
                                        onCartQuantityUpdate.onItemClick(cartFood,true);
                                    }
                                },
                                null
                        );
                        dialog.show(((AppCompatActivity) orderDetailItemBinding.getRoot().getContext()).getSupportFragmentManager(), "confirmDialog");
                    } else {
                        cartFood.setQuantity(quantity);
                        repo.updateCartFood(cartFood);
                        onCartQuantityUpdate.onItemClick(cartFood,false);
                    }
                }
            });

            orderDetailItemBinding.btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SqliteHelper repo = new SqliteHelper(orderDetailItemBinding.getRoot().getContext());
                    int quantity = cartFood.getQuantity() + 1;

                    cartFood.setQuantity(quantity);
                    repo.updateCartFood(cartFood);
                    onCartQuantityUpdate.onItemClick(cartFood,false);
                }
            });
        }
    }
    public interface OnCartQuantityUpdate {
        void onItemClick(CartFood cartFood, boolean isRemoved);
    }
}

