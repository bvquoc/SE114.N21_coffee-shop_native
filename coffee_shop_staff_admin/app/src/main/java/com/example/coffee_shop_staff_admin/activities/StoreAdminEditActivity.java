package com.example.coffee_shop_staff_admin.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.coffee_shop_staff_admin.R;
import com.example.coffee_shop_staff_admin.adapters.ImageEditAdapter;
import com.example.coffee_shop_staff_admin.databinding.ActivityStoreAdminEditBinding;
import com.example.coffee_shop_staff_admin.models.MLocation;
import com.example.coffee_shop_staff_admin.models.Store;
import com.example.coffee_shop_staff_admin.repositories.StoreRepository;
import com.example.coffee_shop_staff_admin.utils.ValidateHelper;
import com.example.coffee_shop_staff_admin.utils.keyboard.KeyboardHelper;
import com.example.coffee_shop_staff_admin.viewmodels.StoreAdminEditViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

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

        activityStoreAdminEditBinding.loading.setOnTouchListener((v, event) -> {
            //Prevent any action when loading is visible
            return true;
        });

        KeyboardHelper.setKeyboardVisibilityListener(this, visible -> {
            if(visible){
                storeAdminEditViewModel.setKeyBoardShow(true);
            }
            else {
                storeAdminEditViewModel.setKeyBoardShow(false);
            }
        });

        activityStoreAdminEditBinding.openTimePickerButton.setOnClickListener(v -> {
            // Get the current time
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and show it
            TimePickerDialog timePickerDialog = new TimePickerDialog(StoreAdminEditActivity.this,
                    (view, hourOfDay, minute12) -> {
                        Calendar calendar = Calendar.getInstance();
                        calendar.add(Calendar.DAY_OF_MONTH, -1);
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute12);

                        Date date = calendar.getTime();

                        storeAdminEditViewModel.getOpenTime().postValue(date);
                    }, hour, minute, true);
            timePickerDialog.show();
        });

        activityStoreAdminEditBinding.closeTimePickerButton.setOnClickListener(v -> {
            // Get the current time
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and show it
            TimePickerDialog timePickerDialog = new TimePickerDialog(StoreAdminEditActivity.this,
                    (view, hourOfDay, minute1) -> {
                        Calendar calendar = Calendar.getInstance();
                        calendar.add(Calendar.DAY_OF_MONTH, -1);
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute1);

                        Date date = calendar.getTime();

                        storeAdminEditViewModel.getCloseTime().postValue(date);
                    }, hour, minute, true);
            timePickerDialog.show();
        });

        activityStoreAdminEditBinding.addressLocationPickerButton.setOnClickListener(v -> {
            Intent intent = new Intent(StoreAdminEditActivity.this, MapsActivity.class);
            activityGoogleMapResultLauncher.launch(intent);
        });
        activityStoreAdminEditBinding.addImageButton.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);

            chooseImageResultLauncher.launch(intent);
        });

        activityStoreAdminEditBinding.button.setOnClickListener(v -> {
            if(updateStoreTask!=null)
            {
                updateStoreTask.cancel(true);
            }
            updateStoreTask = new UpdateStoreTask();
            updateStoreTask.execute();
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
                runOnUiThread(() -> activityStoreAdminEditBinding.nameEditTextFrame.setError("Vui lòng nhập tên"));
                isValid = false;
            }
            else
            {
                runOnUiThread(() -> activityStoreAdminEditBinding.nameEditTextFrame.setError(null));
            }
            if(!ValidateHelper.validatePhone(storeAdminEditViewModel.getPhone()))
            {
                runOnUiThread(() -> activityStoreAdminEditBinding.phoneEditTextFrame.setError("Vui lòng nhập sđt"));
                isValid = false;
            }
            else
            {
                runOnUiThread(() -> activityStoreAdminEditBinding.phoneEditTextFrame.setError(null));
            }
            if(!ValidateHelper.validateText(storeAdminEditViewModel.getAddress()))
            {
                runOnUiThread(() -> activityStoreAdminEditBinding.addressEditTextFrame.setError("Vui lòng chọn hoặc nhập địa chỉ"));
                isValid = false;
            }
            else
            {
                runOnUiThread(() -> activityStoreAdminEditBinding.addressEditTextFrame.setError(null));
            }
            if(storeAdminEditViewModel.getSelectedLocation().getValue() == null)
            {
                runOnUiThread(() -> activityStoreAdminEditBinding.addressLocationEditTextFrame.setError("Vui lòng chọn vị trí"));
                isValid = false;
            }
            else
            {
                runOnUiThread(() -> activityStoreAdminEditBinding.addressLocationEditTextFrame.setError(null));
            }
            if(storeAdminEditViewModel.getCloseTime().getValue() == null)
            {
                runOnUiThread(() -> activityStoreAdminEditBinding.closeTimeEditTextFrame.setError("Vui lòng chọn giờ đóng cửa"));
                isValid = false;
            }
            else
            {
                runOnUiThread(() -> activityStoreAdminEditBinding.closeTimeEditTextFrame.setError(null));
            }
            if(storeAdminEditViewModel.getOpenTime().getValue() == null)
            {
                runOnUiThread(() -> activityStoreAdminEditBinding.openTimeEditTextFrame.setError("Vui lòng chọn giờ mở cửa"));
                isValid = false;
            }
            else
            {
                runOnUiThread(() -> activityStoreAdminEditBinding.openTimeEditTextFrame.setError(null));
            }
            if(!isValid)
            {
                storeAdminEditViewModel.setLoading(false);
                runOnUiThread(() -> Toast.makeText(
                        StoreAdminEditActivity.this,
                        "Vui lòng điền đầy đủ thông tin.",
                        Toast.LENGTH_SHORT
                ).show());
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
                runOnUiThread(() -> Toast.makeText(
                        StoreAdminEditActivity.this,
                        "Chọn giờ mở cửa nhỏ hơn giờ đóng cửa.",
                        Toast.LENGTH_SHORT
                ).show());
                return null;
            }

            //Check image
            if(imageEditAdapter.getImages().size() == 0)
            {
                storeAdminEditViewModel.setLoading(false);
                runOnUiThread(() -> Toast.makeText(
                        StoreAdminEditActivity.this,
                        "Chưa chọn hình ảnh.",
                        Toast.LENGTH_SHORT
                ).show());
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
                StoreRepository.getInstance().updateStoreWithoutUpdatingState(store, (success, message) -> {
                    if(success)
                    {
                        Log.e(TAG, "update store successfully.");
                        storeAdminEditViewModel.setLoading(false);
                        runOnUiThread(() -> Toast.makeText(
                                StoreAdminEditActivity.this,
                                "Đã chỉnh cửa hàng thành công.",
                                Toast.LENGTH_SHORT
                        ).show());
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                    else
                    {
                        Log.e(TAG, "update store failed.");
                        storeAdminEditViewModel.setLoading(false);
                        runOnUiThread(() -> Toast.makeText(
                                StoreAdminEditActivity.this,
                                "Đã có lỗi xảy ra. Xin hãy thử lại sau.",
                                Toast.LENGTH_SHORT
                        ).show());
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
                StoreRepository.getInstance().insertStore(store, (success, message) -> {
                    if(success)
                    {
                        Log.e(TAG, "insert store successfully.");
                        storeAdminEditViewModel.setLoading(false);
                        runOnUiThread(() -> Toast.makeText(
                                StoreAdminEditActivity.this,
                                "Đã thêm cửa hàng thành công.",
                                Toast.LENGTH_SHORT
                        ).show());
                        finish();
                    }
                    else
                    {
                        Log.e(TAG, "insert store failed.");
                        storeAdminEditViewModel.setLoading(false);
                        runOnUiThread(() -> Toast.makeText(
                                StoreAdminEditActivity.this,
                                "Đã có lỗi xảy ra. Xin hãy thử lại sau.",
                                Toast.LENGTH_SHORT
                        ).show());
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