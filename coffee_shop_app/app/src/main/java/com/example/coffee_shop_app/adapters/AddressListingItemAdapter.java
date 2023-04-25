package com.example.coffee_shop_app.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coffee_shop_app.R;
import com.example.coffee_shop_app.activities.EditDeliveryAddressActivity;
import com.example.coffee_shop_app.models.AddressDelivery;

import java.util.ArrayList;
import java.util.List;

public class AddressListingItemAdapter extends RecyclerView.Adapter<AddressListingItemAdapter.AddressListingItemViewHolder> {
    private List<AddressDelivery> addressDeliveries = new ArrayList<AddressDelivery>();

    public AddressListingItemAdapter(List<AddressDelivery> addresses) {
        this.addressDeliveries = addresses;
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
        holder.addressTextView.setText(addressDelivery.getAddress().toString());
        holder.phoneTextView.setText(addressDelivery.getPhone());


    }

    @Override
    public int getItemCount() {
        return addressDeliveries.size();
    }

    public static class AddressListingItemViewHolder extends RecyclerView.ViewHolder{
        private TextView addressTextView;
        private TextView nameTextView;
        private TextView phoneTextView;
        public AddressListingItemViewHolder(@NonNull View itemView) {
            super(itemView);
            addressTextView = itemView.findViewById(R.id.address_text_view);
            nameTextView = itemView.findViewById(R.id.name_text_view);
            phoneTextView = itemView.findViewById(R.id.phone_text_view);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = itemView.getContext();
                    Intent intent = new Intent(context, EditDeliveryAddressActivity.class);
                    intent.putExtra("index", getAdapterPosition());
                    context.startActivity(intent);
                }
            });
        }
    }
}