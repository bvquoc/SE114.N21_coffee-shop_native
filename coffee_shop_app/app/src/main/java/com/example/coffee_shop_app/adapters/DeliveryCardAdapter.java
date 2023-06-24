package com.example.coffee_shop_app.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coffee_shop_app.R;
import com.example.coffee_shop_app.utils.ItemClickedListener;

public class DeliveryCardAdapter extends RecyclerView.Adapter<DeliveryCardAdapter.ViewHolder> {

    Context mContext;

    public DeliveryCardAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public DeliveryCardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Initialize view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_card, parent,
                        false);
        // Pass holder view
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeliveryCardAdapter.ViewHolder holder, int position) {
        holder.setItemClickListener(new ItemClickedListener() {
            @Override
            public void onItemClick(int position) {
                Toast.makeText(holder.itemView.getContext(), "Navigate to order detail screen", Toast.LENGTH_SHORT).show();
            }
        });
        if(position%2==0){
            holder.txtStatusLabel.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.order_preparing_round_text));
        } else {
            holder.txtStatusLabel.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.blue));
            holder.txtStatusLabel.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.order_delivering_round_text));
        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ItemClickedListener itemClickListener;

        public void setItemClickListener(ItemClickedListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        TextView txtStatusLabel;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtStatusLabel=itemView.findViewById(R.id.txtStatusLabel);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onItemClick(getAdapterPosition());
        }
    }
}
