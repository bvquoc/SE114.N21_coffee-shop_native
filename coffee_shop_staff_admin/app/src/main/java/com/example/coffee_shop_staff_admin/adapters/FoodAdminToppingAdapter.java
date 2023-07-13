package com.example.coffee_shop_staff_admin.adapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.coffee_shop_staff_admin.R;
import com.example.coffee_shop_staff_admin.models.Size;
import com.example.coffee_shop_staff_admin.models.Topping;
import com.example.coffee_shop_staff_admin.utils.interfaces.OnFoodAdminSizeCheckedChangedListener;
import com.example.coffee_shop_staff_admin.utils.interfaces.OnFoodAdminToppingCheckedChangedListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class FoodAdminToppingAdapter extends RecyclerView.Adapter<FoodAdminToppingAdapter.FoodAdminToppingViewHolder>{
    private List<Topping> toppings = new ArrayList<Topping>();
    private List<String> availableToppings = new ArrayList<>();
    private OnFoodAdminToppingCheckedChangedListener onFoodAdminToppingCheckedChangedListener;
    public FoodAdminToppingAdapter(){}
    public FoodAdminToppingAdapter(List<Topping> toppings, List<String> availableToppings ){
        this.toppings = toppings;
        this.availableToppings = availableToppings;
    }
    public void changeDataSet(List<Topping> toppings)
    {
        this.toppings = toppings;
        notifyDataSetChanged();
    }
    public void changeAvailableToppings(List<String> availableToppings)
    {
        this.availableToppings = availableToppings;
        notifyDataSetChanged();
    }

    public void setOnFoodAdminToppingCheckedChangedListener(OnFoodAdminToppingCheckedChangedListener onFoodAdminToppingCheckedChangedListener) {
        this.onFoodAdminToppingCheckedChangedListener = onFoodAdminToppingCheckedChangedListener;
    }

    @NonNull
    @Override
    public FoodAdminToppingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_admin_size_topping_item, parent, false);
        return new FoodAdminToppingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodAdminToppingViewHolder holder, int position) {
        Topping topping = toppings.get(position);

        holder.nameTextView.setText(topping.getName());

        DecimalFormat formatter = new DecimalFormat("#,##0.##");
        String formattedPrice = formatter.format(topping.getPrice());
        holder.priceTextView.setText(formattedPrice + "đ");

        if(onFoodAdminToppingCheckedChangedListener!=null)
        {
            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    onFoodAdminToppingCheckedChangedListener.onFoodAdminToppingCheckedChanged(topping.getId(), isChecked);
                }
            });
        }

        boolean isAvailable = availableToppings.contains(topping.getId());
        holder.checkBox.setChecked(isAvailable);
    }

    @Override
    public int getItemCount() {
        return toppings.size();
    }

    public static class FoodAdminToppingViewHolder extends RecyclerView.ViewHolder{
        private CheckBox checkBox;
        private TextView nameTextView;
        private TextView priceTextView;
        private View itemView;
        public FoodAdminToppingViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.check_box);
            nameTextView = itemView.findViewById(R.id.text_view_name);
            priceTextView = itemView.findViewById(R.id.text_view_price);
            this.itemView = itemView;
        }
    }
}
