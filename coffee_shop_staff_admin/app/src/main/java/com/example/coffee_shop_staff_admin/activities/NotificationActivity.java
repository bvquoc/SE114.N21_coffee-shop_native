package com.example.coffee_shop_staff_admin.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.coffee_shop_staff_admin.R;
import com.example.coffee_shop_staff_admin.databinding.ActivityNotificationBinding;
import com.example.coffee_shop_staff_admin.models.Notification;
import com.example.coffee_shop_staff_admin.repositories.NotificationRepository;
import com.example.coffee_shop_staff_admin.utils.ValidateHelper;
import com.example.coffee_shop_staff_admin.utils.keyboard.KeyboardHelper;
import com.example.coffee_shop_staff_admin.viewmodels.NotificationViewModel;

import java.io.IOException;

public class NotificationActivity extends AppCompatActivity {
    private final String TAG = "ToppingAdminEditActivity";
    private ActivityNotificationBinding activityNotificationBinding;
    private final NotificationViewModel notificationViewModel = new NotificationViewModel();
    private AsyncTask<Void, Void, Void> addNotificationTask;
    private final ActivityResultLauncher<Intent> chooseImageResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null && data.getData() != null) {
                        Uri selectedImageUri = data.getData();
                        notificationViewModel.getImageSource().postValue(selectedImageUri);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityNotificationBinding = DataBindingUtil.setContentView(this, R.layout.activity_notification);

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        toolbar.setTitle("Tạo thông báo");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        init();
    }

    private void init() {
        notificationViewModel.getImageSource().observe(this, s -> {
            String scheme = s.getScheme();
            if(scheme == null)
            {
                return;
            }
            try {
                Bitmap selectedImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), s);
                Glide.with(NotificationActivity.this)
                        .load(selectedImageBitmap)
                        .placeholder(R.drawable.img_placeholder)
                        .error(R.drawable.img_placeholder)
                        .into(activityNotificationBinding.imageView);
                notificationViewModel.setHasImage(true);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        });

        activityNotificationBinding.loading.setOnTouchListener((v, event) -> {
            //Prevent any action when loading is visible
            return true;
        });

        KeyboardHelper.setKeyboardVisibilityListener(this, notificationViewModel::setKeyBoardShow);

        activityNotificationBinding.button.setOnClickListener(v -> {
            if(addNotificationTask!=null)
            {
                addNotificationTask.cancel(true);
            }
            addNotificationTask = new AddNotificationTask();
            addNotificationTask.execute();
        });

        activityNotificationBinding.chooseImageButton.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);

            chooseImageResultLauncher.launch(intent);
        });

        activityNotificationBinding.setViewModel(notificationViewModel);
    }

    private final class AddNotificationTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            notificationViewModel.setLoading(true);
        }
        @Override
        protected Void doInBackground(Void... params) {
            boolean isValid = true;
            if(!ValidateHelper.validateText(notificationViewModel.getTitle()))
            {
                runOnUiThread(() -> activityNotificationBinding.titleEditTextFrame.setError("Vui lòng nhập tiêu đề"));
                isValid = false;
            }
            else
            {
                runOnUiThread(() -> activityNotificationBinding.titleEditTextFrame.setError(null));
            }
            if(!ValidateHelper.validateText(notificationViewModel.getContent()))
            {
                runOnUiThread(() -> activityNotificationBinding.contentEditTextFrame.setError("Vui lòng nhập nội dung"));
                isValid = false;
            }
            else
            {
                runOnUiThread(() -> activityNotificationBinding.contentEditTextFrame.setError(null));
            }
            if(!isValid)
            {
                notificationViewModel.setLoading(false);
                runOnUiThread(() -> Toast.makeText(
                        NotificationActivity.this,
                        "Vui lòng điền đầy đủ thông tin.",
                        Toast.LENGTH_SHORT
                ).show());
                return null;
            }
            if(notificationViewModel.getImageSource().getValue()==null)
            {
                notificationViewModel.setLoading(false);
                runOnUiThread(() -> Toast.makeText(
                        NotificationActivity.this,
                        "Chưa chọn hình ảnh.",
                        Toast.LENGTH_SHORT
                ).show());
                return null;
            }

            Notification notification = new Notification(
                    null,
                    notificationViewModel.getTitle(),
                    notificationViewModel.getContent(),
                    notificationViewModel.getImageSource().getValue().toString(),
                    null
            );
            NotificationRepository.getInstance().insertNotification(notification, (success, message) -> {
                if (success) {
                    Log.e(TAG, "insert notification successfully.");
                    notificationViewModel.setLoading(false);
                    runOnUiThread(() -> Toast.makeText(
                            NotificationActivity.this,
                            "Đã thêm thông báo thành công.",
                            Toast.LENGTH_SHORT
                    ).show());
                    finish();
                } else {
                    Log.e(TAG, "insert notification failed.");
                    notificationViewModel.setLoading(false);
                    runOnUiThread(() -> Toast.makeText(
                            NotificationActivity.this,
                            "Đã có lỗi xảy ra. Xin hãy thử lại sau.",
                            Toast.LENGTH_SHORT
                    ).show());
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {

        }
    }
}