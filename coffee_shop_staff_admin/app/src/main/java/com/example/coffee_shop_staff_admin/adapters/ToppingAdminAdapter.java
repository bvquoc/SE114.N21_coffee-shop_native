package com.example.coffee_shop_staff_admin.adapters;

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
import com.example.coffee_shop_staff_admin.models.Topping;
import com.example.coffee_shop_staff_admin.utils.interfaces.OnToppingAdminClickListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ToppingAdminAdapter extends RecyclerView.Adapter implements Filterable {
    private static final int VIEW_TYPE_EMPTY_STATE = 0;
    private static final int VIEW_TYPE_ITEM = 1;
    private List<Topping> toppings = new ArrayList<>();
    private List<Topping> toppingFilter = new ArrayList<>();

    private OnToppingAdminClickListener onToppingAdminClickListener;

    public void setOnToppingAdminClickListener(OnToppingAdminClickListener onToppingAdminClickListener) {
        this.onToppingAdminClickListener = onToppingAdminClickListener;
    }
    public void changeDataSet(List<Topping> toppingList)
    {
        toppings = toppingList;
        toppingFilter = toppingList;
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String query = charSequence.toString();

                if (query.isEmpty()) {
                    toppingFilter = toppings;
                } else {
                    List<Topping> filteredList = new ArrayList<>();
                    for (Topping model : toppings) {
                        if (model.getName().toLowerCase().contains(query.toLowerCase())) {
                            filteredList.add(model);
                        }
                    }
                    toppingFilter = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = toppingFilter;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                toppingFilter = (List<Topping>) filterResults.values;
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
            View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.topping_admin_item, parent, false);
            return new ToppingItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ToppingItemViewHolder) {
            ToppingItemViewHolder toppingItemViewHolder = (ToppingItemViewHolder) holder;
            Topping topping = toppingFilter.get(position);

            toppingItemViewHolder.nameTextView.setText(topping.getName());

            DecimalFormat formatter = new DecimalFormat("#,##0.##");
            String formattedPrice = formatter.format(topping.getPrice());
            toppingItemViewHolder.priceTextView.setText(formattedPrice + "đ");

            Glide.with(holder.itemView.getContext())
                    .load(topping.getImage())
                    .placeholder(R.drawable.img_placeholder)
                    .error(R.drawable.img_placeholder)
                    .into(toppingItemViewHolder.toppingImageView);

            toppingItemViewHolder.itemView.setOnClickListener(
                    v -> onToppingAdminClickListener.onToppingAdminClickListener(topping.getId()));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(toppingFilter.size() == 0) {
            return VIEW_TYPE_EMPTY_STATE;
        }
        else
        {
            return VIEW_TYPE_ITEM;
        }
    }

    @Override
    public int getItemCount() {
        if(toppingFilter.size() == 0)
        {
            return 1;
        }
        else
        {
            return toppingFilter.size();
        }
    }
    public static class ToppingItemViewHolder extends RecyclerView.ViewHolder{
        private final TextView nameTextView;
        private final TextView priceTextView;
        private final ImageView toppingImageView;

        public ToppingItemViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.name_text_view);
            priceTextView = itemView.findViewById(R.id.price_text_view);
            toppingImageView = itemView.findViewById(R.id.topping_image);
        }
    }
    public static class EmptyProductStateViewHolder extends RecyclerView.ViewHolder{
        public EmptyProductStateViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
