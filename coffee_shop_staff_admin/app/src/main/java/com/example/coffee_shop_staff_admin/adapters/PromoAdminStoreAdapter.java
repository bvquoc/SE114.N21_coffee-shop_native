package com.example.coffee_shop_staff_admin.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coffee_shop_staff_admin.R;
import com.example.coffee_shop_staff_admin.models.Store;
import com.example.coffee_shop_staff_admin.utils.interfaces.OnPromoAdminStoreCheckedChangedListener;

import java.util.ArrayList;
import java.util.List;

public class PromoAdminStoreAdapter extends RecyclerView.Adapter<PromoAdminStoreAdapter.PromoAdminStoreViewHolder>{
    private List<Store> stores = new ArrayList<>();
    private List<String> availableStores = new ArrayList<>();
    private boolean isCanEdit = true;
    private OnPromoAdminStoreCheckedChangedListener onPromoAdminStoreCheckedChangedListener;

    public void setOnPromoAdminStoreCheckedChangedListener(OnPromoAdminStoreCheckedChangedListener onPromoAdminStoreCheckedChangedListener) {
        this.onPromoAdminStoreCheckedChangedListener = onPromoAdminStoreCheckedChangedListener;
    }
    public PromoAdminStoreAdapter(boolean isCanEdit){
        this.isCanEdit = isCanEdit;
    }
    public void changeDataSet(List<Store> stores)
    {
        this.stores = stores;
        notifyDataSetChanged();
    }
    public void changeAvailableStores(List<String> availableStores)
    {
        this.availableStores = availableStores;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public PromoAdminStoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.promo_admin_store_item, parent, false);
        return new PromoAdminStoreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PromoAdminStoreViewHolder holder, int position) {
        Store store = stores.get(position);

        holder.nameTextView.setText(store.getShortName());

        if(isCanEdit)
        {
            if(onPromoAdminStoreCheckedChangedListener!=null)
            {
                holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> onPromoAdminStoreCheckedChangedListener.onPromoAdminStoreCheckedChanged(store.getId(), isChecked));
            }
        }
        else
        {
            holder.checkBox.setEnabled(false);
        }
        boolean isAvailable = availableStores.contains(store.getId());
        holder.checkBox.setChecked(isAvailable);
    }

    @Override
    public int getItemCount() {
        return stores.size();
    }

    public static class PromoAdminStoreViewHolder extends RecyclerView.ViewHolder {
        private final CheckBox checkBox;
        private final TextView nameTextView;
        public PromoAdminStoreViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.check_box);
            nameTextView = itemView.findViewById(R.id.text_view_name);
        }
    }
}
