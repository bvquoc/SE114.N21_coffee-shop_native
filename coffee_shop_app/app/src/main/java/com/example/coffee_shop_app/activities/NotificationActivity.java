package com.example.coffee_shop_app.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.coffee_shop_app.R;
import com.example.coffee_shop_app.adapters.NotificationAdapter;
import com.example.coffee_shop_app.databinding.ActivityNotificationBinding;
import com.example.coffee_shop_app.models.Notification;
import com.example.coffee_shop_app.repository.NotificationRepository;
import com.example.coffee_shop_app.utils.interfaces.OnNotificationClickListener;
import com.example.coffee_shop_app.viewmodels.NotificationViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.Date;

public class NotificationActivity extends AppCompatActivity {
    private ActivityNotificationBinding activityNotificationBinding;
    private BottomSheetDialog bottomSheetDialog;
    private final NotificationAdapter notificationAdapter = new NotificationAdapter();
    private final OnNotificationClickListener listener = new OnNotificationClickListener() {
        @Override
        public void onNotificationClick(Notification notification) {
            bottomSheetDialog = new BottomSheetDialog(NotificationActivity.this, R.style.BottomSheetTheme);
            View sheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.notification_item_bottom_sheet, null);

            ((TextView)sheetView.findViewById(R.id.title_text_view)).setText(notification.getTitle());

            ((TextView)sheetView.findViewById(R.id.content_text_view)).setText(notification.getContent());

            ImageView imageView = sheetView.findViewById(R.id.image_view);

            Glide.with(getApplicationContext())
                    .load(Uri.parse(notification.getImage()))
                    .placeholder(R.drawable.img_placeholder)
                    .error(R.drawable.img_placeholder)
                    .fitCenter()
                    .into(imageView);

            ((ImageView)sheetView.findViewById(R.id.close_button)).setOnClickListener(v -> bottomSheetDialog.dismiss());
            bottomSheetDialog.setContentView(sheetView);
            // Set the behavior to STATE_EXPANDED
            View bottomSheetInternal = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheetInternal != null) {
                BottomSheetBehavior.from(bottomSheetInternal).setState(BottomSheetBehavior.STATE_EXPANDED);
                bottomSheetDialog.show();
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityNotificationBinding = DataBindingUtil.setContentView(this, R.layout.activity_notification);

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        toolbar.setTitle("Thông báo");
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        init();
    }

    private void init() {
        notificationAdapter.setOnNotificationClickListener(listener);

        activityNotificationBinding.notificationRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        activityNotificationBinding.notificationRecyclerView.setAdapter(notificationAdapter);

        activityNotificationBinding.setViewModel(NotificationViewModel.getInstance());

        NotificationViewModel.getInstance().getListNotificationMutableLiveData().observe(this, notifications -> {
            if(notifications!=null)
            {
                notificationAdapter.setNumberNotRead(NotificationViewModel.getInstance().getNumberNotiNotRead());
                notificationAdapter.changeDateSet(notifications);
            }
        });

        activityNotificationBinding.refreshLayout.setOnRefreshListener(() -> {
            NotificationRepository.getInstance().registerSnapshotListener();
            activityNotificationBinding.refreshLayout.setRefreshing(false);
        });
    }
}