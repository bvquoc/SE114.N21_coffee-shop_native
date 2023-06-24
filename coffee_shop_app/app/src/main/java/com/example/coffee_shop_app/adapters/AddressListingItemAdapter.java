package com.example.coffee_shop_app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coffee_shop_app.R;
import com.example.coffee_shop_app.models.AddressDelivery;
import com.example.coffee_shop_app.utils.interfaces.OnAddressClickListener;
import com.example.coffee_shop_app.utils.interfaces.OnEditAddressClickListener;

import java.util.ArrayList;
import java.util.List;

public class AddressListingItemAdapter extends RecyclerView.Adapter<AddressListingItemAdapter.AddressListingItemViewHolder> {
    private List<AddressDelivery> addressDeliveries = new ArrayList<AddressDelivery>();
    private OnEditAddressClickListener onEditAddressClickListener;
    private OnAddressClickListener onAddressTouchListener;
    public AddressListingItemAdapter(List<AddressDelivery> addresses) {
        this.addressDeliveries = addresses;
    }
    public void setOnEditAddressTouchListener(OnEditAddressClickListener onEditAddressClickListener) {
        this.onEditAddressClickListener = onEditAddressClickListener;
    }
    public void setOnAddressTouchListener(OnAddressClickListener onAddressTouchListener) {
        this.onAddressTouchListener = onAddressTouchListener;
    }
    public void changeDataSet(List<AddressDelivery> addressDeliveries)
    {
        this.addressDeliveries = addressDeliveries;
        notifyDataSetChanged();
    }
    public void insert(AddressDelivery addressDelivery)
    {
        addressDeliveries.add(addressDelivery);
        notifyItemInserted(addressDeliveries.size() - 1);
    }
    public void update(int index, AddressDelivery addressDelivery)
    {
        addressDeliveries.set(index, addressDelivery);
        notifyItemChanged(index);
    }
    public void delete(int index)
    {
        addressDeliveries.remove(index);
        notifyItemRemoved(index);
    }
    @Override
    public AddressListingItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.address_listing_item, parent, false);
        return new AddressListingItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressListingItemViewHolder holder, int position) {
        AddressDelivery addressDelivery = addressDeliveries.get(position);

        holder.nameTextView.setText(addressDelivery.getNameReceiver());
        holder.addressTextView.setText(addressDelivery.getAddress().getFormattedAddress());
        holder.phoneTextView.setText(addressDelivery.getPhone());
        if(onAddressTouchListener!=null)
        {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onAddressTouchListener.onAddressClick(addressDelivery);
                }
            });
        }
        if(onEditAddressClickListener !=null)
        {
            holder.editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onEditAddressClickListener.onEditAddressClick(holder.getAdapterPosition(),addressDelivery);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return addressDeliveries.size();
    }

    public static class AddressListingItemViewHolder extends RecyclerView.ViewHolder{
        private TextView addressTextView;
        private TextView nameTextView;
        private TextView phoneTextView;
        private Button editButton;
        private View itemView;
        public AddressListingItemViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            addressTextView = itemView.findViewById(R.id.address_text_view);
            nameTextView = itemView.findViewById(R.id.name_text_view);
            phoneTextView = itemView.findViewById(R.id.phone_text_view);
            editButton = itemView.findViewById(R.id.btnPen);
        }
    }
}