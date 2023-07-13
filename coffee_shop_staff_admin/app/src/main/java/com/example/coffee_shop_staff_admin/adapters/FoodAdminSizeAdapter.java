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
import com.example.coffee_shop_staff_admin.utils.interfaces.OnFoodAdminSizeCheckedChangedListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class FoodAdminSizeAdapter extends RecyclerView.Adapter<FoodAdminSizeAdapter.FoodAdminSizeViewHolder> {
    private List<Size> sizes = new ArrayList<Size>();
    private List<String> availableSizes = new ArrayList<>();
    private OnFoodAdminSizeCheckedChangedListener onFoodAdminSizeCheckedChangedListener;
    public FoodAdminSizeAdapter(){}
    public FoodAdminSizeAdapter(List<Size> sizes, List<String> availableSizes ){
        this.sizes = sizes;
        this.availableSizes = availableSizes;
    }
    public void changeDataSet(List<Size> sizes)
    {
        this.sizes = sizes;
        notifyDataSetChanged();
    }
    public void changeAvailableSizes(List<String> availableSizes)
    {
        this.availableSizes = availableSizes;
        notifyDataSetChanged();
    }
    public void setOnFoodAdminSizeCheckedChangedListener(OnFoodAdminSizeCheckedChangedListener onFoodAdminSizeCheckedChangedListener)
    {
        this.onFoodAdminSizeCheckedChangedListener = onFoodAdminSizeCheckedChangedListener;
    }
    @NonNull
    @Override
    public FoodAdminSizeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_admin_size_topping_item, parent, false);
        return new FoodAdminSizeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodAdminSizeViewHolder holder, int position) {
        Size size = sizes.get(position);

        holder.nameTextView.setText(size.getName());

        DecimalFormat formatter = new DecimalFormat("#,##0.##");
        String formattedPrice = formatter.format(size.getPrice());
        holder.priceTextView.setText(formattedPrice + "đ");

        if(onFoodAdminSizeCheckedChangedListener!=null)
        {
            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    onFoodAdminSizeCheckedChangedListener.onFoodAdminSizeCheckedChanged(size.getId(), isChecked);
                }
            });
        }

        boolean isAvailable = availableSizes.contains(size.getId());
        holder.checkBox.setChecked(isAvailable);
    }

    @Override
    public int getItemCount() {
        return sizes.size();
    }

    public static class FoodAdminSizeViewHolder extends RecyclerView.ViewHolder{
        private CheckBox checkBox;
        private TextView nameTextView;
        private TextView priceTextView;
        private View itemView;
        public FoodAdminSizeViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.check_box);
            nameTextView = itemView.findViewById(R.id.text_view_name);
            priceTextView = itemView.findViewById(R.id.text_view_price);
            this.itemView = itemView;
        }
    }
}
