package com.example.coffee_shop_app.adapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.coffee_shop_app.R;
import com.example.coffee_shop_app.models.Notification;
import com.example.coffee_shop_app.utils.interfaces.OnNotificationClickListener;

import java.text.SimpleDateFormat;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {
    private List<Notification> notifications;
    private OnNotificationClickListener onNotificationClickListener;

    int numberNotRead = 0;

    public void setOnNotificationClickListener(OnNotificationClickListener onNotificationClickListener) {
        this.onNotificationClickListener = onNotificationClickListener;
    }

    public void setNumberNotRead(int numberNotRead) {
        this.numberNotRead = numberNotRead;
    }

    public void changeDateSet(List<Notification> notifications)
    {
        this.notifications = notifications;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification notification = notifications.get(position);

        holder.titleTextView.setText(notification.getTitle());

        holder.contentTextView.setText(notification.getContent());

        String dateFormatPattern = "dd/MM/yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormatPattern);
        holder.dateTextView.setText(dateFormat.format(notification.getDateNoti()));

        Glide.with(holder.itemView.getContext())
                .load(Uri.parse(notification.getImage()))
                .placeholder(R.drawable.img_placeholder)
                .error(R.drawable.img_placeholder)
                .fitCenter()
                .into(holder.imageView);

        if(position < numberNotRead)
        {
            int blueColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.blueBackground);
            holder.itemView.setBackgroundColor(blueColor);
        }

        if(onNotificationClickListener!=null)
        {
            holder.itemView.setOnClickListener(v -> {
                onNotificationClickListener.onNotificationClick(notification);
            });
        }
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleTextView;
        private final TextView contentTextView;
        private final TextView dateTextView;
        private final ImageView imageView;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.title_text_view);
            contentTextView = itemView.findViewById(R.id.content_text_view);
            dateTextView = itemView.findViewById(R.id.date_text_view);
            imageView = itemView.findViewById(R.id.image_view);
        }
    }
}
