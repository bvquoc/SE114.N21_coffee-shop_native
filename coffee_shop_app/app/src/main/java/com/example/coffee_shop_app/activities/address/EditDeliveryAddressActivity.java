package com.example.coffee_shop_app.activities.address;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.coffee_shop_app.databinding.ActivityEditDeliveryAddressBinding;
import com.example.coffee_shop_app.R;
import com.example.coffee_shop_app.fragments.Dialog.ConfirmDialog;
import com.example.coffee_shop_app.models.AddressDelivery;
import com.example.coffee_shop_app.models.MLocation;
import com.example.coffee_shop_app.repository.AddressRepository;
import com.example.coffee_shop_app.utils.ValidateHelper;
import com.example.coffee_shop_app.utils.keyboard.KeyboardHelper;
import com.example.coffee_shop_app.viewmodels.EditDeliveryAddressViewModel;

import java.util.Objects;

public class EditDeliveryAddressActivity extends AppCompatActivity{
    enum EditAddressResult{
        Insert,
        Delete,
        Update
    }
    private final String TAG = "EditDeliveryAddressActivity";
    private ActivityEditDeliveryAddressBinding activityEditDeliveryAddressBinding;
    int index = -1;
    final EditDeliveryAddressViewModel editDeliveryAddressViewModel = new EditDeliveryAddressViewModel();
    private final ActivityResultLauncher<Intent> activityGoogleMapResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent intent = result.getData();
                    if (intent != null) {
                        String formattedAddress = intent.getStringExtra("formattedAddress");
                        double lat = intent.getDoubleExtra("lat", 0);
                        double lng = intent.getDoubleExtra("lng",  0);
                        editDeliveryAddressViewModel.setAddress(new MLocation(formattedAddress, lat, lng));
                    }
                }
            }
    );
    private AsyncTask<Void, Void, Void> updateAddressTask;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(index != -1)
        {
            getMenuInflater().inflate(R.menu.menu_edit_address_page, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_delete) {
            ConfirmDialog dialog = new ConfirmDialog(
                    "Thông báo",
                    "Bạn có chắc muốn xóa địa chỉ này",
                    v -> {
                        if (updateAddressTask != null) {
                            updateAddressTask.cancel(true);
                        }
                        updateAddressTask = new UpdateAddressTask(true);
                        updateAddressTask.execute();
                    },
                    null
            );
            dialog.show(getSupportFragmentManager(), "confirmDialog");
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityEditDeliveryAddressBinding = DataBindingUtil.setContentView(this, R.layout.activity_edit_delivery_address);
        index = getIntent().getIntExtra("index", -1);
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        toolbar.setTitle("Thay đổi địa chỉ");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        init(index);
    }
    @SuppressLint("ClickableViewAccessibility")
    void init(int index){
        if(index != -1)
        {
            Intent intent = getIntent();
            String formattedAddress = intent.getStringExtra("formattedAddress");
            double lat = intent.getDoubleExtra("lat", 0);
            double lng = intent.getDoubleExtra("lng", 0);
            String nameReceiver = intent.getStringExtra("nameReceiver");
            String phone = intent.getStringExtra("phone");
            String addressNote = intent.getStringExtra("addressNote");
            AddressDelivery addressDelivery = new AddressDelivery(
                    new MLocation(formattedAddress, lat, lng),
                    nameReceiver,
                    phone,
                    addressNote
            );
            editDeliveryAddressViewModel.updateParameter(addressDelivery);
            activityEditDeliveryAddressBinding.setViewModel(editDeliveryAddressViewModel);
        }
        else
        {
            activityEditDeliveryAddressBinding.setViewModel(editDeliveryAddressViewModel);

            //change title of toolbar
            Objects.requireNonNull(getSupportActionBar()).setTitle("Thêm địa chỉ mới");
        }
        KeyboardHelper.setKeyboardVisibilityListener(this, editDeliveryAddressViewModel::setKeyBoardShow);
        activityEditDeliveryAddressBinding.addressPickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditDeliveryAddressActivity.this, MapsActivity.class);
                if(editDeliveryAddressViewModel.getAddress() != null)
                {
                    MLocation location = editDeliveryAddressViewModel.getAddress();
                    intent.putExtra("lat", location.getLat());
                    intent.putExtra("lng", location.getLng());
                }
                activityGoogleMapResultLauncher.launch(intent);
            }
        });
        activityEditDeliveryAddressBinding.loading.setOnTouchListener((v, event) -> true);
        activityEditDeliveryAddressBinding.saveButton.setOnClickListener(v -> {
            if(updateAddressTask!=null)
            {
                updateAddressTask.cancel(true);
            }
            updateAddressTask = new UpdateAddressTask();
            updateAddressTask.execute();
        });
    }
    private final class UpdateAddressTask extends AsyncTask<Void, Void, Void> {
        private final boolean isForDeleteAddress;
        public UpdateAddressTask(boolean isForDeleteAddress)
        {
            this.isForDeleteAddress = isForDeleteAddress;
        }
        public UpdateAddressTask()
        {
            this.isForDeleteAddress = false;
        }
        @Override
        protected void onPreExecute() {
            editDeliveryAddressViewModel.setUpdatingAddress(true);
        }
        @Override
        protected Void doInBackground(Void... params) {
            if(isForDeleteAddress)
            {
                AddressRepository.getInstance().deleteAddress(index, success -> {
                    if(success)
                    {
                        Log.e(TAG, "delete address successfully.");
                        Intent intent = new Intent();
                        intent.putExtra("resultType", EditAddressResult.Delete);
                        editDeliveryAddressViewModel.setUpdatingAddress(false);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                    else
                    {
                        Log.e(TAG, "delete address failed.");
                        editDeliveryAddressViewModel.setUpdatingAddress(false);
                        runOnUiThread(() -> Toast.makeText(
                                EditDeliveryAddressActivity.this,
                                "Đã có lỗi xảy ra. Xin hãy thử lại sau.",
                                Toast.LENGTH_SHORT
                        ).show());
                    }
                });
            }
            else
            {
                boolean isValid = true;
                if(!ValidateHelper.validateText(editDeliveryAddressViewModel.getNameReceiver()))
                {
                    runOnUiThread(() -> activityEditDeliveryAddressBinding.nameEditTextFrame.setError("Vui lòng nhập tên"));
                    isValid = false;
                }
                else
                {
                    runOnUiThread(() -> activityEditDeliveryAddressBinding.nameEditTextFrame.setError(null));
                }
                if(!ValidateHelper.validatePhone(editDeliveryAddressViewModel.getPhone()))
                {
                    runOnUiThread(() -> activityEditDeliveryAddressBinding.phoneEditTextFrame.setError("Vui lòng nhập số điện thoại 10 số"));
                    isValid = false;
                }
                else
                {
                    runOnUiThread(() -> activityEditDeliveryAddressBinding.phoneEditTextFrame.setError(null));
                }
                if(editDeliveryAddressViewModel.getAddress() == null)
                {
                    runOnUiThread(() -> activityEditDeliveryAddressBinding.addressEditTextFrame.setError("Vui lòng chọn địa chỉ"));
                    isValid = false;
                }
                else
                {
                    runOnUiThread(() -> activityEditDeliveryAddressBinding.addressEditTextFrame.setError(null));
                }
                if(!isValid)
                {
                    editDeliveryAddressViewModel.setUpdatingAddress(false);
                    return null;
                }
                AddressDelivery addressDelivery = new AddressDelivery(
                        editDeliveryAddressViewModel.getAddress(),
                        editDeliveryAddressViewModel.getNameReceiver(),
                        editDeliveryAddressViewModel.getPhone(),
                        editDeliveryAddressViewModel.getAddressNote()
                );
                if(index == -1)
                {
                    AddressRepository.getInstance().insertAddress(addressDelivery, success -> {
                        if(success)
                        {
                            Log.e(TAG, "insert address successfully.");
                            Intent intent = new Intent();
                            intent.putExtra("resultType", EditAddressResult.Insert);
                            intent.putExtra("index", index);
                            intent.putExtra("formattedAddress", addressDelivery.getAddress().getFormattedAddress());
                            intent.putExtra("lat", addressDelivery.getAddress().getLat());
                            intent.putExtra("lng", addressDelivery.getAddress().getLng());
                            intent.putExtra("addressNote", addressDelivery.getAddressNote());
                            intent.putExtra("nameReceiver", addressDelivery.getNameReceiver());
                            intent.putExtra("phone", addressDelivery.getPhone());
                            editDeliveryAddressViewModel.setUpdatingAddress(false);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                        else
                        {
                            Log.e(TAG, "insert address failed.");
                            editDeliveryAddressViewModel.setUpdatingAddress(false);
                            runOnUiThread(() -> Toast.makeText(
                                    EditDeliveryAddressActivity.this,
                                    "Đã có lỗi xảy ra. Xin hãy thử lại sau.",
                                    Toast.LENGTH_SHORT
                            ).show());
                        }
                    });
                }
                else
                {
                    AddressRepository.getInstance().updateAddress(addressDelivery, index, success -> {
                        if(success)
                        {
                            Log.e(TAG, "update address successfully.");
                            Intent intent = new Intent();
                            intent.putExtra("resultType", EditAddressResult.Update);
                            intent.putExtra("index", index);
                            intent.putExtra("formattedAddress", addressDelivery.getAddress().getFormattedAddress());
                            intent.putExtra("lat", addressDelivery.getAddress().getLat());
                            intent.putExtra("lng", addressDelivery.getAddress().getLng());
                            intent.putExtra("addressNote", addressDelivery.getAddressNote());
                            intent.putExtra("nameReceiver", addressDelivery.getNameReceiver());
                            intent.putExtra("phone", addressDelivery.getPhone());
                            editDeliveryAddressViewModel.setUpdatingAddress(false);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                        else
                        {
                            Log.e(TAG, "update address failed.");
                            editDeliveryAddressViewModel.setUpdatingAddress(false);
                            runOnUiThread(() -> Toast.makeText(
                                    EditDeliveryAddressActivity.this,
                                    "Đã có lỗi xảy ra. Xin hãy thử lại sau.",
                                    Toast.LENGTH_SHORT
                            ).show());
                        }
                    });
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
        }
    }
}