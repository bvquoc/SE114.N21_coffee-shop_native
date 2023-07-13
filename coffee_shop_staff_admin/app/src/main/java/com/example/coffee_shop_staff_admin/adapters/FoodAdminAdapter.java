package com.example.coffee_shop_staff_admin.adapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.coffee_shop_staff_admin.R;
import com.example.coffee_shop_staff_admin.models.Food;
import com.example.coffee_shop_staff_admin.utils.interfaces.OnFoodAdminClickListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class FoodAdminAdapter extends RecyclerView.Adapter implements Filterable {
    private static final int VIEW_TYPE_EMPTY_STATE = 0;
    private static final int VIEW_TYPE_ITEM = 1;
    private List<Food> foods = new ArrayList<Food>();
    private List<Food> foodFilter = new ArrayList<>();

    private OnFoodAdminClickListener onFoodAdminClickListener;

    public void setOnFoodAdminClickListener(OnFoodAdminClickListener onFoodAdminClickListener) {
        this.onFoodAdminClickListener = onFoodAdminClickListener;
    }

    public void changeDataSet(List<Food> foodList)
    {
        foods = foodList;
        foodFilter = foodList;
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String query = charSequence.toString();

                if (query.isEmpty()) {
                    foodFilter = foods;
                } else {
                    List<Food> filteredList = new ArrayList<>();
                    for (Food model : foods) {
                        if (model.getName().toLowerCase().contains(query.toLowerCase())) {
                            filteredList.add(model);
                        }
                    }
                    foodFilter = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = foodFilter;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                foodFilter = (List<Food>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_EMPTY_STATE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.can_not_find_state, parent, false);
            return new EmptyProductStateViewHolder(view);
        } else{
            View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_admin_item, parent, false);
            return new FoodItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof FoodItemViewHolder) {
            FoodItemViewHolder foodItemViewHolder = (FoodItemViewHolder) holder;
            Food food = foodFilter.get(position);

            foodItemViewHolder.nameTextView.setText(food.getName());

            DecimalFormat formatter = new DecimalFormat("#,##0.##");
            String formattedPrice = formatter.format(food.getPrice());

            foodItemViewHolder.priceTextView.setText(formattedPrice + "đ");

            Glide.with(holder.itemView.getContext())
                    .load(Uri.parse(food.getImages().get(0)))
                    .into(foodItemViewHolder.productImageView);

            foodItemViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onFoodAdminClickListener.onFoodAdminClick(food.getId());
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(foodFilter.size() == 0) {
            return VIEW_TYPE_EMPTY_STATE;
        }
        else
        {
            return VIEW_TYPE_ITEM;
        }
    }

    @Override
    public int getItemCount() {
        if(foodFilter.size() == 0)
        {
            return 1;
        }
        else
        {
            return foodFilter.size();
        }
    }

    public static class FoodItemViewHolder extends RecyclerView.ViewHolder{
        private TextView nameTextView;
        private TextView priceTextView;
        private ImageView productImageView;

        private View itemView;
        public FoodItemViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.name_text_view);
            priceTextView = itemView.findViewById(R.id.price_text_view);
            productImageView = itemView.findViewById(R.id.product_image);
            this.itemView = itemView;
        }
    }
    public static class EmptyProductStateViewHolder extends RecyclerView.ViewHolder{
        public EmptyProductStateViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
