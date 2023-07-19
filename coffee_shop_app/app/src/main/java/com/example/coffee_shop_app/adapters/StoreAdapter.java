package com.example.coffee_shop_app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coffee_shop_app.R;
import com.example.coffee_shop_app.models.Store;
import com.example.coffee_shop_app.utils.CompareHelper;
import com.example.coffee_shop_app.utils.LocationHelper;
import com.example.coffee_shop_app.utils.interfaces.OnStoreClickListener;
import com.google.android.gms.maps.model.LatLng;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class StoreAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {
    private static final int VIEW_TYPE_EMPTY_STATE = 0;
    private static final int VIEW_TYPE_ITEM = 1;
    private  boolean isNeedSearch = false;
    private List<Store> stores;
    private List<Store> storeFilter;

    //Properties handle touch
    private OnStoreClickListener onStoreClickListener;

    public void setOnClickListener(OnStoreClickListener onStoreClickListener) {
        this.onStoreClickListener = onStoreClickListener;
    }

    public StoreAdapter(List<Store> stores, boolean isNeedSearch)
    {
        this.stores = stores;
        storeFilter = new ArrayList<>(stores);
        this.isNeedSearch = isNeedSearch;
    }
    public StoreAdapter(List<Store> stores)
    {
        this.stores = stores;
        storeFilter = new ArrayList<>(stores);
    }

    public void changeDataSet(List<Store> allStores)
    {
        stores = new ArrayList<>(allStores);
        storeFilter = new ArrayList<>(allStores);
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_EMPTY_STATE)
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.can_not_find_state, parent, false);
            return new StoreAdapter.EmptyProductStateViewHolder(view);
        }
        else
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.store_item, parent, false);
            return new StoreAdapter.StoreAdapterViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof  StoreAdapterViewHolder)
        {
            StoreAdapterViewHolder viewHolder = (StoreAdapterViewHolder) holder;
            Store store = storeFilter.get(position);
            if(store.isFavorite())
            {
                viewHolder.storeStatusImage.setImageResource(R.drawable.ic_store_favorite_24);
            }
            else
            {
                viewHolder.storeStatusImage.setImageResource(R.drawable.ic_store_24);
            }

            viewHolder.storeName.setText(store.getShortName());
            viewHolder.storeAddress.setText(store.getAddress().getFormattedAddress());

            LatLng currentLocation = LocationHelper.getInstance().getCurrentLocation();
            if(currentLocation == null)
            {
                viewHolder.storeDistance.setVisibility(View.GONE);
            }
            else
            {
                double distance = LocationHelper.calculateDistance(
                    store.getAddress().getLat(),
                    store.getAddress().getLng(),
                    currentLocation.latitude,
                    currentLocation.longitude);

                DecimalFormat decimalFormat = new DecimalFormat("#0.00");
                String formattedNumber = decimalFormat.format(distance);

                viewHolder.storeDistance.setText(formattedNumber + " km");
            }
            if(onStoreClickListener!=null)
            {
                viewHolder.itemView.setOnClickListener(v -> onStoreClickListener.onStoreClick(store.getId()));
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(isNeedSearch) {
            if(storeFilter.size() == 0) {
                return VIEW_TYPE_EMPTY_STATE;
            }
        }
        return VIEW_TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        if(storeFilter.size() == 0 && isNeedSearch)
        {
            return 1;
        }
        else
        {
            return storeFilter.size();
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String query = charSequence.toString();

                List<Store> filteredList = new ArrayList<>();
                if (!query.isEmpty()) {
                    for (Store model : stores) {
                        if(
                            CompareHelper.compareContainTextUnicode(model.getShortName().toLowerCase(), query.toLowerCase()) ||
                            CompareHelper.compareContainTextUnicode(model.getAddress().getFormattedAddress().toLowerCase(), query.toLowerCase())
                        )
                        {
                            filteredList.add(model);
                        }
                    }
                }
                else {
                    filteredList = new ArrayList<>(stores);
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                storeFilter = (List<Store>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public static class StoreAdapterViewHolder extends RecyclerView.ViewHolder{
        private final ImageView storeStatusImage;
        private final TextView storeName;
        private final TextView storeAddress;
        private final TextView storeDistance;
        public StoreAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            storeStatusImage = itemView.findViewById(R.id.image_status_store);
            storeName =  itemView.findViewById(R.id.name_text_view);
            storeAddress = itemView.findViewById(R.id.address_text_view);
            storeDistance = itemView.findViewById(R.id.distance_text_view);
        }
    }
    public static class EmptyProductStateViewHolder extends RecyclerView.ViewHolder{
        public EmptyProductStateViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
