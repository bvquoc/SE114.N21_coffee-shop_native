package com.example.coffee_shop_staff_admin.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.ArrayMap;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.coffee_shop_staff_admin.R;
import com.example.coffee_shop_staff_admin.adapters.ImageEditAdapter;
import com.example.coffee_shop_staff_admin.databinding.ActivityStoreAdminDetailBinding;
import com.example.coffee_shop_staff_admin.databinding.ActivityStoreAdminEditBinding;
import com.example.coffee_shop_staff_admin.databinding.ActivityToppingAdminEditBinding;
import com.example.coffee_shop_staff_admin.models.MLocation;
import com.example.coffee_shop_staff_admin.models.Store;
import com.example.coffee_shop_staff_admin.models.Topping;
import com.example.coffee_shop_staff_admin.repositories.StoreRepository;
import com.example.coffee_shop_staff_admin.repositories.ToppingRepository;
import com.example.coffee_shop_staff_admin.utils.ValidateHelper;
import com.example.coffee_shop_staff_admin.utils.interfaces.UpdateDataListener;
import com.example.coffee_shop_staff_admin.utils.keyboard.KeyboardHelper;
import com.example.coffee_shop_staff_admin.utils.keyboard.OnKeyboardVisibilityListener;
import com.example.coffee_shop_staff_admin.viewmodels.StoreAdminEditViewModel;
import com.example.coffee_shop_staff_admin.viewmodels.ToppingAdminEditViewModel;
import com.google.android.gms.maps.GoogleMap;
import com.google.common.collect.Maps;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;

public class StoreAdminEditActivity extends AppCompatActivity {
    private final String TAG = "StoreAdminEditActivity";
    private ActivityStoreAdminEditBinding activityStoreAdminEditBinding;
    private final StoreAdminEditViewModel storeAdminEditViewModel = new StoreAdminEditViewModel();
    private AsyncTask<Void, Void, Void> updateStoreTask;
    private String storeId = null;
    private Toolbar toolbar;
    private ImageEditAdapter imageEditAdapter;
    private final ActivityResultLauncher<Intent> chooseImageResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null && data.getData() != null) {
                        imageEditAdapter.insert(data.getData().toString());
                    }
                }
            });
    private final ActivityResultLauncher<Intent> activityGoogleMapResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent intent = result.getData();
                    if (intent != null) {
                        String formattedAddress = intent.getStringExtra("formattedAddress");
                        double lat = intent.getDoubleExtra("lat", 0);
                        double lng = intent.getDoubleExtra("lng",  0);
                        MLocation mLocation = new MLocation(formattedAddress, lat, lng);
                        storeAdminEditViewModel.getSelectedLocation().postValue(mLocation);
                    }
                } else {
                    //User do nothing
                }
            }
    );
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityStoreAdminEditBinding = DataBindingUtil.setContentView(this,R.layout.activity_store_admin_edit);

        toolbar = findViewById(R.id.my_toolbar);
        toolbar.setTitle("Tạo cửa hàng");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Intent intent = getIntent();
        Store store = null;
        storeId = intent.getStringExtra("storeId");
        if(storeId != null)
        {
            String shortName = intent.getStringExtra("shortName");
            String formattedAddress = intent.getStringExtra("formattedAddress");
            double lat = intent.getDoubleExtra("lat", 0);
            double lng = intent.getDoubleExtra("lng", 0);
            String phone = intent.getStringExtra("phone");
            Date timeOpen = (Date)intent.getSerializableExtra("timeOpen");
            Date timeClose = (Date)intent.getSerializableExtra("timeClose");
            List<String> images = intent.getStringArrayListExtra("images");
            store = new Store(
                    storeId,
                    shortName,
                    new MLocation(
                            formattedAddress,
                            lat,
                            lng
                    ),
                    phone,
                    timeOpen,
                    timeClose,
                    images,
                    new HashMap<>(),
                    new ArrayList<>());
        }
        init(store);
    }

    void init(Store store){
        if(store != null)
        {
            storeAdminEditViewModel.updateParameter(store);
            imageEditAdapter = new ImageEditAdapter(store.getImages());
            toolbar.setTitle("Thay đổi cửa hàng");
        }
        else
        {
            imageEditAdapter = new ImageEditAdapter(new ArrayList<>());
        }

        activityStoreAdminEditBinding.imageRecycler.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
        activityStoreAdminEditBinding.imageRecycler.setAdapter(imageEditAdapter);

        activityStoreAdminEditBinding.setViewModel(storeAdminEditViewModel);

        activityStoreAdminEditBinding.loading.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //Prevent any action when loading is visible
                return true;
            }
        });

        KeyboardHelper.setKeyboardVisibilityListener(this, new OnKeyboardVisibilityListener() {
            @Override
            public void onVisibilityChanged(boolean visible) {
                if(visible){
                    storeAdminEditViewModel.setKeyBoardShow(true);
                }
                else {
                    storeAdminEditViewModel.setKeyBoardShow(false);
                }
            }
        });

        activityStoreAdminEditBinding.openTimePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the current time
                final Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

                // Create a new instance of TimePickerDialog and show it
                TimePickerDialog timePickerDialog = new TimePickerDialog(StoreAdminEditActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                Calendar calendar = Calendar.getInstance();
                                calendar.add(Calendar.DAY_OF_MONTH, -1);
                                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                calendar.set(Calendar.MINUTE, minute);

                                Date date = calendar.getTime();

                                storeAdminEditViewModel.getOpenTime().postValue(date);
                            }
                        }, hour, minute, true);
                timePickerDialog.show();
            }
        });

        activityStoreAdminEditBinding.closeTimePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the current time
                final Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

                // Create a new instance of TimePickerDialog and show it
                TimePickerDialog timePickerDialog = new TimePickerDialog(StoreAdminEditActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                Calendar calendar = Calendar.getInstance();
                                calendar.add(Calendar.DAY_OF_MONTH, -1);
                                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                calendar.set(Calendar.MINUTE, minute);

                                Date date = calendar.getTime();

                                storeAdminEditViewModel.getCloseTime().postValue(date);
                            }
                        }, hour, minute, true);
                timePickerDialog.show();
            }
        });

        activityStoreAdminEditBinding.addressLocationPickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StoreAdminEditActivity.this, MapsActivity.class);
                activityGoogleMapResultLauncher.launch(intent);
            }
        });
        activityStoreAdminEditBinding.addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);

                chooseImageResultLauncher.launch(intent);
            }
        });

        activityStoreAdminEditBinding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(updateStoreTask!=null)
                {
                    updateStoreTask.cancel(true);
                }
                updateStoreTask = new UpdateStoreTask();
                updateStoreTask.execute();
            }
        });
    }
    private final class UpdateStoreTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            storeAdminEditViewModel.setLoading(true);
        }
        @Override
        protected Void doInBackground(Void... params) {
            boolean isValid = true;
            if(!ValidateHelper.validateText(storeAdminEditViewModel.getName()))
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activityStoreAdminEditBinding.nameEditTextFrame.setError("Vui lòng nhập tên");
                    }
                });
                isValid = false;
            }
            else
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activityStoreAdminEditBinding.nameEditTextFrame.setError(null);
                    }
                });
            }
            if(!ValidateHelper.validatePhone(storeAdminEditViewModel.getPhone()))
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activityStoreAdminEditBinding.phoneEditTextFrame.setError("Vui lòng nhập sđt");
                    }
                });
                isValid = false;
            }
            else
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activityStoreAdminEditBinding.phoneEditTextFrame.setError(null);
                    }
                });
            }
            if(!ValidateHelper.validateText(storeAdminEditViewModel.getAddress()))
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activityStoreAdminEditBinding.addressEditTextFrame.setError("Vui lòng chọn hoặc nhập địa chỉ");
                    }
                });
                isValid = false;
            }
            else
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activityStoreAdminEditBinding.addressEditTextFrame.setError(null);
                    }
                });
            }
            if(storeAdminEditViewModel.getSelectedLocation().getValue() == null)
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activityStoreAdminEditBinding.addressLocationEditTextFrame.setError("Vui lòng chọn vị trí");
                    }
                });
                isValid = false;
            }
            else
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activityStoreAdminEditBinding.addressLocationEditTextFrame.setError(null);
                    }
                });
            }
            if(storeAdminEditViewModel.getCloseTime().getValue() == null)
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activityStoreAdminEditBinding.closeTimeEditTextFrame.setError("Vui lòng chọn giờ đóng cửa");
                    }
                });
                isValid = false;
            }
            else
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activityStoreAdminEditBinding.closeTimeEditTextFrame.setError(null);
                    }
                });
            }
            if(storeAdminEditViewModel.getOpenTime().getValue() == null)
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activityStoreAdminEditBinding.openTimeEditTextFrame.setError("Vui lòng chọn giờ mở cửa");
                    }
                });
                isValid = false;
            }
            else
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activityStoreAdminEditBinding.openTimeEditTextFrame.setError(null);
                    }
                });
            }
            if(!isValid)
            {
                storeAdminEditViewModel.setLoading(false);
                return null;
            }

            //Check openTime and closeTime
            Calendar openTimeCalendar = Calendar.getInstance();
            openTimeCalendar.setTime(storeAdminEditViewModel.getOpenTime().getValue());

            Calendar closeTimeCalendar = Calendar.getInstance();
            closeTimeCalendar.setTime(storeAdminEditViewModel.getCloseTime().getValue());

            if(openTimeCalendar.get(Calendar.HOUR_OF_DAY) * 60 + openTimeCalendar.get(Calendar.MINUTE)
                    >= closeTimeCalendar.get(Calendar.HOUR_OF_DAY) * 60 + closeTimeCalendar.get(Calendar.MINUTE))
            {
                storeAdminEditViewModel.setLoading(false);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(
                                StoreAdminEditActivity.this,
                                "Chọn giờ mở cửa nhỏ hơn giờ đóng cửa.",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
                return null;
            }

            //Check image
            if(imageEditAdapter.getImages().size() == 0)
            {
                storeAdminEditViewModel.setLoading(false);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(
                                StoreAdminEditActivity.this,
                                "Chưa chọn hình ảnh.",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
                return null;
            }
            if(storeId != null)
            {
                MLocation location = storeAdminEditViewModel.getSelectedLocation().getValue();
                Store store = new Store(
                        storeId,
                        storeAdminEditViewModel.getName(),
                        new MLocation(
                                storeAdminEditViewModel.getAddress(),
                                location.getLat(),
                                location.getLng()),
                        storeAdminEditViewModel.getPhone(),
                        storeAdminEditViewModel.getOpenTime().getValue(),
                        storeAdminEditViewModel.getCloseTime().getValue(),
                        imageEditAdapter.getImages(),
                        new HashMap<>(),
                        new ArrayList<>());
                StoreRepository.getInstance().updateStoreWithoutUpdatingState(store, new UpdateDataListener() {
                    @Override
                    public void onUpdateData(boolean success) {
                        if(success)
                        {
                            Log.e(TAG, "update store successfully.");
                            storeAdminEditViewModel.setLoading(false);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(
                                            StoreAdminEditActivity.this,
                                            "Đã chỉnh cửa hàng thành công.",
                                            Toast.LENGTH_SHORT
                                    ).show();
                                }
                            });
                            Intent intent = new Intent();
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                        else
                        {
                            Log.e(TAG, "update store failed.");
                            storeAdminEditViewModel.setLoading(false);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(
                                            StoreAdminEditActivity.this,
                                            "Đã có lỗi xảy ra. Xin hãy thử lại sau.",
                                            Toast.LENGTH_SHORT
                                    ).show();
                                }
                            });
                        }
                    }
                });
            }
            else
            {
                MLocation location = storeAdminEditViewModel.getSelectedLocation().getValue();
                Store store = new Store(
                        null,
                        storeAdminEditViewModel.getName(),
                        new MLocation(
                                storeAdminEditViewModel.getAddress(),
                                location.getLat(),
                                location.getLng()),
                        storeAdminEditViewModel.getPhone(),
                        storeAdminEditViewModel.getOpenTime().getValue(),
                        storeAdminEditViewModel.getCloseTime().getValue(),
                        imageEditAdapter.getImages(),
                        new HashMap<>(),
                        new ArrayList<>());
                StoreRepository.getInstance().insertStore(store, new UpdateDataListener() {
                    @Override
                    public void onUpdateData(boolean success) {
                        if(success)
                        {
                            Log.e(TAG, "insert store successfully.");
                            storeAdminEditViewModel.setLoading(false);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(
                                            StoreAdminEditActivity.this,
                                            "Đã thêm cửa hàng thành công.",
                                            Toast.LENGTH_SHORT
                                    ).show();
                                }
                            });
                            finish();
                        }
                        else
                        {
                            Log.e(TAG, "insert store failed.");
                            storeAdminEditViewModel.setLoading(false);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(
                                            StoreAdminEditActivity.this,
                                            "Đã có lỗi xảy ra. Xin hãy thử lại sau.",
                                            Toast.LENGTH_SHORT
                                    ).show();
                                }
                            });
                        }
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {

        }
    }
}