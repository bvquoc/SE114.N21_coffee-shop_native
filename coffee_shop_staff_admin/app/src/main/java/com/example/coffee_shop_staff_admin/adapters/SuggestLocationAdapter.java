package com.example.coffee_shop_staff_admin.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coffee_shop_staff_admin.R;
import com.example.coffee_shop_staff_admin.models.MLocation;
import com.example.coffee_shop_staff_admin.utils.interfaces.OnSuggestLocationClickListener;

import java.util.List;

public class SuggestLocationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_EMPTY_STATE = 0;
    private static final int VIEW_TYPE_ITEM = 1;
    private List<MLocation> locationList;
    private OnSuggestLocationClickListener onSuggestLocationClickListener;

    public SuggestLocationAdapter(List<MLocation> locationList) {
        this.locationList = locationList;
    }

    public void changeDataSet(List<MLocation> locationList) {
        this.locationList = locationList;
        notifyDataSetChanged();
    }

    public void setOnSuggestLocationTouchListener(OnSuggestLocationClickListener listener) {
        onSuggestLocationClickListener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_EMPTY_STATE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.suggest_location_item, parent, false);
            return new NotFoundLocationViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.suggest_location_item, parent, false);
            return new SuggestLocationViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof NotFoundLocationViewHolder) {
            NotFoundLocationViewHolder notFoundLocationViewHolder = (NotFoundLocationViewHolder) holder;
            notFoundLocationViewHolder.formattedAddressTextView.setText("Không thể tìm thấy địa chỉ");
        } else {
            SuggestLocationViewHolder suggestLocationViewHolder = (SuggestLocationViewHolder) holder;
            MLocation location = locationList.get(position);
            suggestLocationViewHolder.formattedAddressTextView.setText(location.getFormattedAddress());
            suggestLocationViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onSuggestLocationClickListener != null) {
                        onSuggestLocationClickListener.onSuggestLocationClick(location);
                    }
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (locationList == null) {
            return VIEW_TYPE_EMPTY_STATE;
        } else {
            return VIEW_TYPE_ITEM;
        }
    }

    @Override
    public int getItemCount() {
        if (locationList == null) {
            return 1;
        }
        return locationList.size();
    }

    public static class NotFoundLocationViewHolder extends RecyclerView.ViewHolder {
        private final TextView formattedAddressTextView;

        public NotFoundLocationViewHolder(@NonNull View itemView) {
            super(itemView);
            formattedAddressTextView = itemView.findViewById(R.id.formatted_address_text_view);
        }
    }

    public static class SuggestLocationViewHolder extends RecyclerView.ViewHolder {
        private TextView formattedAddressTextView;
        private final View itemView;

        public SuggestLocationViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            formattedAddressTextView = itemView.findViewById(R.id.formatted_address_text_view);
        }
    }
}