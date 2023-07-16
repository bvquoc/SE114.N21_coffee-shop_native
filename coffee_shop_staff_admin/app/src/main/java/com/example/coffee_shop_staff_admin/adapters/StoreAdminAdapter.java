package com.example.coffee_shop_staff_admin.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coffee_shop_staff_admin.R;
import com.example.coffee_shop_staff_admin.models.Store;
import com.example.coffee_shop_staff_admin.utils.interfaces.OnStoreAdminClickListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class StoreAdminAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {
    private static final int VIEW_TYPE_EMPTY_STATE = 0;
    private static final int VIEW_TYPE_ITEM = 1;
    private List<Store> stores = new ArrayList<>();
    private List<Store> storeFilter = new ArrayList<>();

    private OnStoreAdminClickListener onStoreAdminClickListener;

    public void setOnStoreAdminClickListener(OnStoreAdminClickListener onStoreAdminClickListener) {
        this.onStoreAdminClickListener = onStoreAdminClickListener;
    }

    public void changeDataSet(List<Store> storeList)
    {
        stores = storeList;
        storeFilter = storeList;
        notifyDataSetChanged();
    }
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String query = charSequence.toString();

                if (query.isEmpty()) {
                    storeFilter = stores;
                } else {
                    List<Store> filteredList = new ArrayList<>();
                    for (Store model : stores) {
                        if (model.getShortName().toLowerCase().contains(query.toLowerCase()) ||
                            model.getAddress().getFormattedAddress().toLowerCase().contains(query.toLowerCase())) {
                            filteredList.add(model);
                        }
                    }
                    storeFilter = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = storeFilter;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                storeFilter = (List<Store>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_EMPTY_STATE)
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.can_not_find_state, parent, false);
            return new EmptyProductStateViewHolder(view);
        }
        else
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.store_admin_item, parent, false);
            return new StoreAdapterViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof StoreAdapterViewHolder)
        {
            StoreAdapterViewHolder viewHolder = (StoreAdapterViewHolder) holder;
            Store store = storeFilter.get(position);

            viewHolder.storeName.setText(store.getShortName());
            viewHolder.storeAddress.setText(store.getAddress().getFormattedAddress());

            if(onStoreAdminClickListener!=null)
            {
                viewHolder.itemView.setOnClickListener(v -> onStoreAdminClickListener.onStoreAdminClick(store.getId(), store.getShortName()));
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(storeFilter.size() == 0) {
            return VIEW_TYPE_EMPTY_STATE;
        }
        else
        {
            return VIEW_TYPE_ITEM;
        }
    }


    @Override
    public int getItemCount() {
        if(storeFilter.size() == 0)
        {
            return 1;
        }
        else
        {
            return storeFilter.size();
        }
    }
    public static class StoreAdapterViewHolder extends RecyclerView.ViewHolder{
        private final TextView storeName;
        private final TextView storeAddress;
        public StoreAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            storeName =  itemView.findViewById(R.id.name_text_view);
            storeAddress = itemView.findViewById(R.id.address_text_view);
        }
    }
    public static class EmptyProductStateViewHolder extends RecyclerView.ViewHolder{
        public EmptyProductStateViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
