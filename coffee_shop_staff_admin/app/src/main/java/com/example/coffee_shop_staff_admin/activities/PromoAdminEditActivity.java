package com.example.coffee_shop_staff_admin.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.coffee_shop_staff_admin.R;
import com.example.coffee_shop_staff_admin.adapters.PromoAdminStoreAdapter;
import com.example.coffee_shop_staff_admin.databinding.ActivityPromoAdminEditBinding;
import com.example.coffee_shop_staff_admin.models.Promo;
import com.example.coffee_shop_staff_admin.models.Store;
import com.example.coffee_shop_staff_admin.repositories.PromoRepository;
import com.example.coffee_shop_staff_admin.repositories.StoreRepository;
import com.example.coffee_shop_staff_admin.utils.ValidateHelper;
import com.example.coffee_shop_staff_admin.utils.interfaces.OnPromoAdminStoreCheckedChangedListener;
import com.example.coffee_shop_staff_admin.utils.keyboard.KeyboardHelper;
import com.example.coffee_shop_staff_admin.viewmodels.PromoAdminEditViewModel;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PromoAdminEditActivity extends AppCompatActivity {
    private final String TAG = "PromoAdminEditActivity";
    private ActivityPromoAdminEditBinding activityPromoAdminEditBinding;
    private final PromoAdminEditViewModel promoAdminEditViewModel = new PromoAdminEditViewModel();
    private AsyncTask<Void, Void, Void> updatePromoTask;
    private String promoId = null;
    private Toolbar toolbar;

    List<String> storeIds = new ArrayList<>();
    private final PromoAdminStoreAdapter promoAdminStoreAdapter = new PromoAdminStoreAdapter(true);

    private final OnPromoAdminStoreCheckedChangedListener listener = (promoId, isChecked) -> {
        if(isChecked)
        {
            storeIds.add(promoId);
        }
        else
        {
            storeIds.remove(promoId);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityPromoAdminEditBinding = DataBindingUtil.setContentView(this,R.layout.activity_promo_admin_edit);

        toolbar = findViewById(R.id.my_toolbar);
        toolbar.setTitle("Tạo mã giảm giá");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        Intent intent = getIntent();
        Promo promo = null;
        promoId = intent.getStringExtra("promoId");
        if(promoId != null)
        {
            String description = intent.getStringExtra("description");
            double percent = intent.getDoubleExtra("percent", 0);
            double minPrice = intent.getDoubleExtra("minPrice", 0);
            double maxPrice = intent.getDoubleExtra("maxPrice", 0);
            Date startDate = (Date)intent.getSerializableExtra("startDate");
            Date closeDate = (Date)intent.getSerializableExtra("closeDate");
            boolean isForNewCustomer = intent.getBooleanExtra("isForNewCustomer", false);
            List<String> stores = intent.getStringArrayListExtra("stores");

            promo = new Promo(
                    promoId,
                    closeDate,
                    startDate,
                    description,
                    isForNewCustomer,
                    maxPrice,
                    minPrice,
                    percent,
                    stores
            );
        }
        init(promo);
    }


    Observer<List<Store>> observer = new Observer<List<Store>>() {
        @Override
        public void onChanged(@Nullable List<Store> value) {
            promoAdminEditViewModel.setLoading(true);
            if(value != null)
            {
                promoAdminStoreAdapter.changeDataSet(value);
                promoAdminEditViewModel.setLoading(false);
                StoreRepository.getInstance().getStoreListMutableLiveData().removeObserver(observer);
            }
        }
    };

    void init(Promo promo){
        if(promo != null)
        {
            promoAdminEditViewModel.updateParameter(promo);
            promoAdminStoreAdapter.changeAvailableStores(promo.getStores());
            toolbar.setTitle("Thay đổi cửa hàng");
        }
        StoreRepository.getInstance().getStoreListMutableLiveData().observe(this, observer);

        promoAdminStoreAdapter.setOnPromoAdminStoreCheckedChangedListener(listener);
        activityPromoAdminEditBinding.recyclerStore.setLayoutManager(new LinearLayoutManager(this));
        activityPromoAdminEditBinding.recyclerStore.setAdapter(promoAdminStoreAdapter);

        activityPromoAdminEditBinding.setViewModel(promoAdminEditViewModel);

        activityPromoAdminEditBinding.loading.setOnTouchListener((v, event) -> {
            //Prevent any action when loading is visible
            return true;
        });

        KeyboardHelper.setKeyboardVisibilityListener(this, promoAdminEditViewModel::setKeyBoardShow);

        activityPromoAdminEditBinding.startDatePickerButton.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(PromoAdminEditActivity.this, (view, year1, month1, dayOfMonth1) -> {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth1);
                calendar.set(Calendar.MONTH, month1);
                calendar.set(Calendar.YEAR, year1);

                Date date = calendar.getTime();

                promoAdminEditViewModel.getStartDate().postValue(date);
            },year, month, dayOfMonth);

            datePickerDialog.show();
        });

        activityPromoAdminEditBinding.startTimePickerButton.setOnClickListener(v -> {
            // Get the current time
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and show it
            TimePickerDialog timePickerDialog = new TimePickerDialog(PromoAdminEditActivity.this,
                    (view, hourOfDay, minute12) -> {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute12);

                        Date date = calendar.getTime();

                        promoAdminEditViewModel.getStartTime().postValue(date);
                    }, hour, minute, true);
            timePickerDialog.show();
        });

        activityPromoAdminEditBinding.closeDatePickerButton.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(PromoAdminEditActivity.this, (view, year1, month1, dayOfMonth1) -> {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth1);
                calendar.set(Calendar.MONTH, month1);
                calendar.set(Calendar.YEAR, year1);

                Date date = calendar.getTime();

                promoAdminEditViewModel.getCloseDate().postValue(date);
            },year, month, dayOfMonth);

            datePickerDialog.show();
        });

        activityPromoAdminEditBinding.closeTimePickerButton.setOnClickListener(v -> {
            // Get the current time
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and show it
            TimePickerDialog timePickerDialog = new TimePickerDialog(PromoAdminEditActivity.this,
                    (view, hourOfDay, minute1) -> {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute1);

                        Date date = calendar.getTime();

                        promoAdminEditViewModel.getCloseTime().postValue(date);
                    }, hour, minute, true);
            timePickerDialog.show();
        });

        activityPromoAdminEditBinding.button.setOnClickListener(v -> {
            if(updatePromoTask!=null)
            {
                updatePromoTask.cancel(true);
            }
            updatePromoTask = new UpdatePromoTask();
            updatePromoTask.execute();
        });
    }
    private final class UpdatePromoTask extends AsyncTask<Void, Void, Void> {
        public Date combineDateAndTime(Date date, Date time) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            Calendar timeCalendar = Calendar.getInstance();
            timeCalendar.setTime(time);

            // Set the time components from the 'time' Date object
            calendar.set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY));
            calendar.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE));
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            return calendar.getTime();
        }
        @Override
        protected void onPreExecute() {
            promoAdminEditViewModel.setUpdating(true);
        }
        @Override
        protected Void doInBackground(Void... params) {
            boolean isValid = true;
            if(!ValidateHelper.validateText(promoAdminEditViewModel.getPromoCode()))
            {
                runOnUiThread(() -> activityPromoAdminEditBinding.promoCodeEditTextFrame.setError("Vui lòng nhập mã"));
                isValid = false;
            }
            else
            {
                runOnUiThread(() -> activityPromoAdminEditBinding.promoCodeEditTextFrame.setError(null));
            }
            if(!ValidateHelper.validatePercent(promoAdminEditViewModel.getPercent()))
            {
                runOnUiThread(() -> activityPromoAdminEditBinding.percentEditTextFrame.setError("Vui lòng nhập số từ 0 đến 100"));
                isValid = false;
            }
            else
            {
                runOnUiThread(() -> activityPromoAdminEditBinding.percentEditTextFrame.setError(null));
            }
            if(!ValidateHelper.validateDouble(promoAdminEditViewModel.getMinPrice()))
            {
                runOnUiThread(() -> activityPromoAdminEditBinding.minPriceEditTextFrame.setError("Vui lòng nhập số"));
                isValid = false;
            }
            else
            {
                runOnUiThread(() -> activityPromoAdminEditBinding.minPriceEditTextFrame.setError(null));
            }
            if(!ValidateHelper.validateDouble(promoAdminEditViewModel.getMaxPrice()))
            {
                runOnUiThread(() -> activityPromoAdminEditBinding.maxPriceEditTextFrame.setError("Vui lòng nhập số"));
                isValid = false;
            }
            else
            {
                runOnUiThread(() -> activityPromoAdminEditBinding.maxPriceEditTextFrame.setError(null));
            }
            if(promoAdminEditViewModel.getStartDate().getValue() == null)
            {
                runOnUiThread(() -> activityPromoAdminEditBinding.startDateEditTextFrame.setError("Vui lòng chọn ngày bắt đầu"));
                isValid = false;
            }
            else
            {
                runOnUiThread(() -> activityPromoAdminEditBinding.startDateEditTextFrame.setError(null));
            }
            if(promoAdminEditViewModel.getStartTime().getValue() == null)
            {
                runOnUiThread(() -> activityPromoAdminEditBinding.startTimeEditTextFrame.setError("Vui lòng chọn giờ bắt đầu"));
                isValid = false;
            }
            else
            {
                runOnUiThread(() -> activityPromoAdminEditBinding.startTimeEditTextFrame.setError(null));
            }
            if(promoAdminEditViewModel.getCloseDate().getValue() == null)
            {
                runOnUiThread(() -> activityPromoAdminEditBinding.closeDateEditTextFrame.setError("Vui lòng chọn ngày kết thúc"));
                isValid = false;
            }
            else
            {
                runOnUiThread(() -> activityPromoAdminEditBinding.closeDateEditTextFrame.setError(null));
            }
            if(promoAdminEditViewModel.getCloseTime().getValue() == null)
            {
                runOnUiThread(() -> activityPromoAdminEditBinding.closeTimeEditTextFrame.setError("Vui lòng chọn giờ kết thúc"));
                isValid = false;
            }
            else
            {
                runOnUiThread(() -> activityPromoAdminEditBinding.closeTimeEditTextFrame.setError(null));
            }
            if(!isValid)
            {
                promoAdminEditViewModel.setUpdating(false);
                return null;
            }

            //Check time
            Date startDate = combineDateAndTime(
                    promoAdminEditViewModel.getStartDate().getValue(),
                    promoAdminEditViewModel.getStartTime().getValue()
            );
            Date closeDate = combineDateAndTime(
                    promoAdminEditViewModel.getCloseDate().getValue(),
                    promoAdminEditViewModel.getCloseTime().getValue()
            );

            LocalDateTime startDateTime = LocalDateTime.ofInstant(
                    startDate.toInstant(),
                    ZoneId.systemDefault()
            );
            LocalDateTime closeDateTime = LocalDateTime.ofInstant(
                    closeDate.toInstant(),
                    ZoneId.systemDefault()
            );

            if(startDateTime.isAfter(closeDateTime) || startDateTime.isEqual(closeDateTime))
            {
                promoAdminEditViewModel.setUpdating(false);
                runOnUiThread(() -> Toast.makeText(
                        PromoAdminEditActivity.this,
                        "Chọn thời điểm bắt đầu nhỏ hơn thời điểm kết thúc.",
                        Toast.LENGTH_SHORT
                ).show());
                return null;
            }

            if(promoId != null)
            {
                Promo promo = new Promo(
                        promoId,
                        closeDate,
                        startDate,
                        promoAdminEditViewModel.getDescription(),
                        promoAdminEditViewModel.isForNewCustomerValue(),
                        Double.parseDouble(promoAdminEditViewModel.getMaxPrice()),
                        Double.parseDouble(promoAdminEditViewModel.getMinPrice()),
                        Double.parseDouble(promoAdminEditViewModel.getPercent()) / 100,
                        storeIds
                );

                PromoRepository.getInstance().updatePromo(promo, success -> {
                    if(success)
                    {
                        Log.e(TAG, "update promo successfully.");
                        promoAdminEditViewModel.setUpdating(false);
                        runOnUiThread(() -> Toast.makeText(
                                PromoAdminEditActivity.this,
                                "Đã chỉnh mã giảm giá thành công.",
                                Toast.LENGTH_SHORT
                        ).show());
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                    else
                    {
                        Log.e(TAG, "update promo failed.");
                        promoAdminEditViewModel.setUpdating(false);
                        runOnUiThread(() -> Toast.makeText(
                                PromoAdminEditActivity.this,
                                "Đã có lỗi xảy ra. Xin hãy thử lại sau.",
                                Toast.LENGTH_SHORT
                        ).show());
                    }
                });
            }
            else
            {
                Promo promo = new Promo(
                        promoAdminEditViewModel.getPromoCode(),
                        closeDate,
                        startDate,
                        promoAdminEditViewModel.getDescription(),
                        promoAdminEditViewModel.isForNewCustomerValue(),
                        Double.parseDouble(promoAdminEditViewModel.getMaxPrice()),
                        Double.parseDouble(promoAdminEditViewModel.getMinPrice()),
                        Double.parseDouble(promoAdminEditViewModel.getPercent()) / 100,
                        storeIds
                );
                PromoRepository.getInstance().insertPromo(promo, success -> {
                    if(success)
                    {
                        Log.e(TAG, "insert promo successfully.");
                        promoAdminEditViewModel.setUpdating(false);
                        runOnUiThread(() -> Toast.makeText(
                                PromoAdminEditActivity.this,
                                "Đã thêm mã giảm giá thành công.",
                                Toast.LENGTH_SHORT
                        ).show());
                        finish();
                    }
                    else
                    {
                        Log.e(TAG, "insert promo failed.");
                        promoAdminEditViewModel.setUpdating(false);
                        runOnUiThread(() -> Toast.makeText(
                                PromoAdminEditActivity.this,
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