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
import com.example.coffee_shop_staff_admin.models.Size;
import com.example.coffee_shop_staff_admin.utils.interfaces.OnSizeAdminClickListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class SizeAdminAdapter extends RecyclerView.Adapter implements Filterable {
    private static final int VIEW_TYPE_EMPTY_STATE = 0;
    private static final int VIEW_TYPE_ITEM = 1;
    private List<Size> sizes = new ArrayList<Size>();
    private List<Size> sizeFilter = new ArrayList<Size>();

    private OnSizeAdminClickListener onSizeAdminClickListener;

    public void setOnSizeAdminClickListener(OnSizeAdminClickListener onSizeAdminClickListener) {
        this.onSizeAdminClickListener = onSizeAdminClickListener;
    }
    public void changeDataSet(List<Size> sizeList)
    {
        sizes = sizeList;
        sizeFilter = sizeList;
        notifyDataSetChanged();
    }
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String query = charSequence.toString();

                if (query.isEmpty()) {
                    sizeFilter = sizes;
                } else {
                    List<Size> filteredList = new ArrayList<>();
                    for (Size model : sizes) {
                        if (model.getName().toLowerCase().contains(query.toLowerCase())) {
                            filteredList.add(model);
                        }
                    }
                    sizeFilter = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = sizeFilter;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                sizeFilter = (List<Size>) filterResults.values;
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
            View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.size_admin_item, parent, false);
            return new SizeItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof SizeItemViewHolder) {
            SizeItemViewHolder sizeItemViewHolder = (SizeItemViewHolder) holder;
            Size size = sizeFilter.get(position);

            sizeItemViewHolder.nameTextView.setText(size.getName());

            DecimalFormat formatter = new DecimalFormat("#,##0.##");
            String formattedPrice = formatter.format(size.getPrice());
            sizeItemViewHolder.priceTextView.setText(formattedPrice + "đ");

            Glide.with(holder.itemView.getContext())
                    .load(size.getImage())
                    .into(sizeItemViewHolder.sizeImageView);

            sizeItemViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onSizeAdminClickListener.onSizeAdminClickListener(size.getId());
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(sizeFilter.size() == 0) {
            return VIEW_TYPE_EMPTY_STATE;
        }
        else
        {
            return VIEW_TYPE_ITEM;
        }
    }

    @Override
    public int getItemCount() {
        if(sizeFilter.size() == 0)
        {
            return 1;
        }
        else
        {
            return sizeFilter.size();
        }
    }
    public static class SizeItemViewHolder extends RecyclerView.ViewHolder{
        private TextView nameTextView;
        private TextView priceTextView;
        private ImageView sizeImageView;

        private View itemView;
        public SizeItemViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.name_text_view);
            priceTextView = itemView.findViewById(R.id.price_text_view);
            sizeImageView = itemView.findViewById(R.id.size_image);
            this.itemView = itemView;
        }
    }
    public static class EmptyProductStateViewHolder extends RecyclerView.ViewHolder{
        public EmptyProductStateViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
