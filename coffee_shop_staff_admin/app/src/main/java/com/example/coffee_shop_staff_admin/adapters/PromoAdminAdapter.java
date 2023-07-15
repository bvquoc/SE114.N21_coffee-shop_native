package com.example.coffee_shop_staff_admin.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coffee_shop_staff_admin.R;
import com.example.coffee_shop_staff_admin.models.Promo;
import com.example.coffee_shop_staff_admin.utils.interfaces.OnPromoAdminClickListener;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PromoAdminAdapter extends RecyclerView.Adapter implements Filterable {
    private static final int VIEW_TYPE_EMPTY_STATE = 0;
    private static final int VIEW_TYPE_ITEM = 1;
    private List<Promo> promos = new ArrayList<>();
    private List<Promo> promoFilter = new ArrayList<>();
    private OnPromoAdminClickListener onPromoClickListener;
    private Date date = new Date();
    public PromoAdminAdapter(){}

    public void setOnPromoClickListener(OnPromoAdminClickListener onPromoClickListener) {
        this.onPromoClickListener = onPromoClickListener;
    }
    public void changeDataSet(List<Promo> promos)
    {
        this.promos = promos;
        this.promoFilter = promos;
        date = new Date();
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_EMPTY_STATE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.can_not_find_state, parent, false);
            return new EmptyPromoStateViewHolder(view);
        } else{
            View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.promo_admin_item, parent, false);
            return new PromoViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof PromoViewHolder) {
            Promo promo = promos.get(position);

            PromoViewHolder promoViewHolder = (PromoViewHolder) holder;

            DecimalFormat formatter = new DecimalFormat("#,##0.##");
            String minPrice = formatter.format(promo.getMinPrice());
            String maxPrice = formatter.format(promo.getMaxPrice());
            promoViewHolder.titleText.setText(
                    "Giảm "
                    + formatter.format(promo.getPercent() * 100)
                    + "% đơn "
                    + minPrice
                    + "đ (tối đa "
                    + maxPrice + "đ)");

            DateFormat dateFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy");
            promoViewHolder.startDateText.setText("Bắt đầu: " + dateFormat.format(promo.getDateStart()));
            promoViewHolder.endDateText.setText("Hết hạn: " + dateFormat.format(promo.getDateEnd()));

            int colorCard;
            int colorText;
            String text;
            if(promo.getDateEnd().compareTo(date) < 0)
            {
                colorCard = ContextCompat.getColor(holder.itemView.getContext(), R.color.pinkBackground);
                colorText = ContextCompat.getColor(holder.itemView.getContext(), R.color.red);
                text = "Hết hạn";
            }
            else if (promo.getDateStart().compareTo(date) > 0)
            {
                colorCard = ContextCompat.getColor(holder.itemView.getContext(), R.color.grey_border);
                colorText = ContextCompat.getColor(holder.itemView.getContext(), R.color.grey_text);
                text = "Chưa tới ngày bắt đầu";
            }
            else
            {
                colorCard = ContextCompat.getColor(holder.itemView.getContext(), R.color.greenBackground);
                colorText = ContextCompat.getColor(holder.itemView.getContext(), R.color.green);
                text = "Đang trong hoạt động";
            }
            promoViewHolder.statusCard.setCardBackgroundColor(colorCard);
            promoViewHolder.statusText.setTextColor(colorText);
            promoViewHolder.statusText.setText(text);

            promoViewHolder.itemView.setOnClickListener(v ->
                    onPromoClickListener.onPromoAdminClick(promo.getPromoCode()));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(promoFilter.size() == 0) {
            return VIEW_TYPE_EMPTY_STATE;
        }
        else
        {
            return VIEW_TYPE_ITEM;
        }
    }

    @Override
    public int getItemCount() {
        if(promoFilter.size() == 0)
        {
            return 1;
        }
        else
        {
            return promoFilter.size();
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String query = charSequence.toString();

                if (query.isEmpty()) {
                    promoFilter = promos;
                } else {
                    List<Promo> filteredList = new ArrayList<>();
                    for (Promo model : promos) {
                        if (model.getPromoCode().toLowerCase().contains(query.toLowerCase())) {
                            filteredList.add(model);
                        }
                    }
                    promoFilter = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = promoFilter;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                promoFilter = (List<Promo>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public static class PromoViewHolder extends RecyclerView.ViewHolder{
        private final TextView titleText;
        private final TextView startDateText;
        private final TextView endDateText;
        private final TextView statusText;
        private final CardView statusCard;

        public PromoViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.title_text);
            startDateText = itemView.findViewById(R.id.start_date_text);
            endDateText = itemView.findViewById(R.id.end_date_text);
            statusCard = itemView.findViewById(R.id.status_card_view);
            statusText = itemView.findViewById(R.id.status_text_view);
        }
    }
    public static class EmptyPromoStateViewHolder extends RecyclerView.ViewHolder{
        public EmptyPromoStateViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}