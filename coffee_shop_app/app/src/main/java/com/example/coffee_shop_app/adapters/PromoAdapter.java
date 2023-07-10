package com.example.coffee_shop_app.adapters;

import android.view.LayoutInflater;
import android.view.PointerIcon;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coffee_shop_app.R;
import com.example.coffee_shop_app.models.AddressDelivery;
import com.example.coffee_shop_app.models.Promo;
import com.example.coffee_shop_app.utils.interfaces.OnEditAddressClickListener;
import com.example.coffee_shop_app.utils.interfaces.OnPromoClickListener;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class PromoAdapter extends RecyclerView.Adapter<PromoAdapter.PromoViewHolder>{
    private List<Promo> promos = new ArrayList<Promo>();
    private OnPromoClickListener onPromoClickListener;
    public PromoAdapter(List<Promo> promos)
    {
        this.promos = promos;
    }

    public void setOnPromoClickListener(OnPromoClickListener onPromoClickListener) {
        this.onPromoClickListener = onPromoClickListener;
    }
    public void changeDataSet(List<Promo> promos)
    {
        this.promos = promos;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public PromoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.promo_item, parent, false);
        return new PromoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PromoViewHolder holder, int position) {
        Promo promo = promos.get(position);

        if(promo.isForNewCustomer())
        {
            holder.promoImage.setImageResource(R.drawable.img_promo_new_customer);
        }
        else
        {
            holder.promoImage.setImageResource(R.drawable.img_promo_all);
        }

        DecimalFormat formatter = new DecimalFormat("#,##0.##");
        holder.percentText.setText(formatter.format(promo.getPercent() * 100)+"%");

        String formattedPrice = formatter.format(promo.getMinPrice());
        holder.titleText.setText("Giảm " + formatter.format(promo.getPercent() * 100) + "% đơn " + formattedPrice);

        DateFormat dateFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy");
        holder.dateText.setText("Hết hạn: " + dateFormat.format(promo.getDateEnd()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPromoClickListener.onPromoClick(promo);
            }
        });
    }

    @Override
    public int getItemCount() {
        return promos.size();
    }

    public static class PromoViewHolder extends RecyclerView.ViewHolder{
        private ImageView promoImage;
        private TextView percentText;
        private TextView titleText;
        private TextView dateText;
        private View itemView;

        public PromoViewHolder(@NonNull View itemView) {
            super(itemView);
            promoImage = itemView.findViewById(R.id.promo_image);
            percentText = itemView.findViewById(R.id.percent_text);
            titleText = itemView.findViewById(R.id.title_text);
            dateText = itemView.findViewById(R.id.date_text);
            this.itemView = itemView;
        }
    }
}
