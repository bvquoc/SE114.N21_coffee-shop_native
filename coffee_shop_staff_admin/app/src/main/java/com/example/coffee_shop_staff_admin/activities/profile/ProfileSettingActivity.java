package com.example.coffee_shop_staff_admin.activities.profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.example.coffee_shop_staff_admin.R;
import com.example.coffee_shop_staff_admin.databinding.ActivityProfileSettingBinding;
import com.example.coffee_shop_staff_admin.models.User;
import com.example.coffee_shop_staff_admin.utils.StringConverter;
import com.example.coffee_shop_staff_admin.viewmodels.ProfileSettingViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ProfileSettingActivity extends AppCompatActivity {

    ActivityProfileSettingBinding activityProfileSettingBinding;
    ProfileSettingViewModel viewModel;

    final Calendar calendar = Calendar.getInstance();
    private BottomSheetDialog bottomSheetDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityProfileSettingBinding = DataBindingUtil.setContentView(this, R.layout.activity_profile_setting);
        viewModel = new ViewModelProvider(this).get(ProfileSettingViewModel.class);
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        activityProfileSettingBinding.setViewModel(viewModel);
        setFunctionButton();
        setOnChangeInfo();
        setOnChangePassword();
    }

    private void setOnChangeInfo(){
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                calendar.set(Calendar.YEAR, i);
                calendar.set(Calendar.MONTH, i1);
                calendar.set(Calendar.DAY_OF_MONTH, i2);
                updateDob();
            }
        };
        activityProfileSettingBinding.txtDob.setText(StringConverter.DateToString(viewModel.getUser().getDob()));

        activityProfileSettingBinding.btnChangeInfo.setOnClickListener((view) -> {
            activityProfileSettingBinding.txtName.setFocusableInTouchMode(true);
            activityProfileSettingBinding.txtPhone.setFocusableInTouchMode(true);

            activityProfileSettingBinding.ctnChangeInfo.setVisibility(View.VISIBLE);
            activityProfileSettingBinding.btnChangeInfo.setVisibility(View.GONE);

            activityProfileSettingBinding.txtDobLayout.setEndIconTintList(ColorStateList.valueOf(Color.BLACK));
            activityProfileSettingBinding.txtDobLayout.setEndIconOnClickListener((v -> {
                new DatePickerDialog(ProfileSettingActivity.this, R.style.DialogTheme, date, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();

            }));
        });
    }

    private void setOnChangePassword(){
        activityProfileSettingBinding.btnChangePassword.btnProfileFunction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog = new BottomSheetDialog(ProfileSettingActivity.this, R.style.BottomSheetTheme);

                bottomSheetDialog.setContentView(R.layout.change_password_bottom_sheet);

                EditText oldPass = bottomSheetDialog.findViewById(R.id.txt_pass_change);
                EditText newPass = bottomSheetDialog.findViewById(R.id.txt_newpass_change);
                EditText rePass = bottomSheetDialog.findViewById(R.id.txt_repass_change);
                Button changeButton = bottomSheetDialog.findViewById(R.id.btn_accept_change_pass);

                changeButton.setOnClickListener(v -> {
                    viewModel.onChangePassword(oldPass.getText().toString(), newPass.getText().toString(),
                            params -> {
                                String msg = "Thay đổi mật khẩu thành công!";
                                Snackbar snackbar = Snackbar
                                        .make(view, msg, Snackbar.LENGTH_LONG);
                                snackbar.show();
                                bottomSheetDialog.dismiss();
                            },
                            params -> {
                                String msg = "Mật khẩu sai hoặc đã xảy ra lỗi!";
                                Snackbar snackbar = Snackbar
                                        .make(view, msg, Snackbar.LENGTH_LONG);
                                snackbar.show();
                            });
                });

                bottomSheetDialog.findViewById(R.id.close_button).setOnClickListener(v -> {
                    bottomSheetDialog.dismiss();
                });

                // Set the behavior to STATE_EXPANDED
                View bottomSheetInternal = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
                BottomSheetBehavior.from(bottomSheetInternal).setState(BottomSheetBehavior.STATE_EXPANDED);
                bottomSheetDialog.show();
            }
        });
    }

    private void setFunctionButton(){
        activityProfileSettingBinding.btnCancelInfo.setOnClickListener(view -> {
            //UI
            setUIOnChangedInfo();
            //Logic
            onCancel();
        });

        activityProfileSettingBinding.btnSaveInfo.setOnClickListener(view -> {
            //UI
            setUIOnChangedInfo();
            //Logic
            onSave();
        });
    }
    private void updateDob(){
        String myFormat="dd/MM/yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat);
        activityProfileSettingBinding.txtDob.setText(dateFormat.format(calendar.getTime()));
    }

    private void setUIOnChangedInfo(){
        activityProfileSettingBinding.txtName.setFocusable(false);
        activityProfileSettingBinding.txtPhone.setFocusable(false);

        activityProfileSettingBinding.ctnChangeInfo.setVisibility(View.GONE);
        activityProfileSettingBinding.btnChangeInfo.setVisibility(View.VISIBLE);

        activityProfileSettingBinding.txtDobLayout.setEndIconTintList(ColorStateList.valueOf(Color.GRAY));
        activityProfileSettingBinding.txtDobLayout.setEndIconOnClickListener(null);
    }

    private void onCancel(){
        activityProfileSettingBinding.txtName.setText(viewModel.getUser().getName());
        activityProfileSettingBinding.txtDob.setText(StringConverter.DateToString(viewModel.getUser().getDob()));
        activityProfileSettingBinding.txtPhone.setText(viewModel.getUser().getPhoneNumber());
    }

    private void onSave(){
        User user = viewModel.getUser();
        user.setName(activityProfileSettingBinding.txtName.getText().toString());
        user.setDob(StringConverter.StringToDate(activityProfileSettingBinding.txtDob.getText().toString()));
        user.setPhoneNumber(activityProfileSettingBinding.txtPhone.getText().toString());
        viewModel.onSaveInfo(user);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }
}