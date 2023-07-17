package com.example.coffee_shop_app.activities.profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.example.coffee_shop_app.R;
import com.example.coffee_shop_app.databinding.ActivityProfileSettingBinding;
import com.example.coffee_shop_app.databinding.ChangePasswordBottomSheetBinding;
import com.example.coffee_shop_app.databinding.OrderTypeBottomSheetBinding;
import com.example.coffee_shop_app.models.User;
import com.example.coffee_shop_app.repository.AuthRepository;
import com.example.coffee_shop_app.viewmodels.CartButtonViewModel;
import com.example.coffee_shop_app.viewmodels.ProfileSettingViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;

import android.app.DatePickerDialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import org.joda.time.DateTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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


        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                calendar.set(Calendar.YEAR, i);
                calendar.set(Calendar.MONTH, i1);
                calendar.set(Calendar.DAY_OF_MONTH, i2);
                updateDob();
            }
        };
        activityProfileSettingBinding.txtDob.setText(formatDate(viewModel.getUser().getDob()));

        activityProfileSettingBinding.btnChangeInfo.setOnClickListener((view) -> {
            activityProfileSettingBinding.txtName.setFocusableInTouchMode(true);
            activityProfileSettingBinding.txtName.setFocusableInTouchMode(true);

            activityProfileSettingBinding.ctnChangeInfo.setVisibility(View.VISIBLE);
            activityProfileSettingBinding.btnChangeInfo.setVisibility(View.GONE);

            activityProfileSettingBinding.txtDobLayout.setEndIconTintList(ColorStateList.valueOf(Color.BLACK));
            activityProfileSettingBinding.txtDobLayout.setEndIconOnClickListener((v -> {
                new DatePickerDialog(ProfileSettingActivity.this, R.style.DialogTheme, date, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();

            }));
        });

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

        activityProfileSettingBinding.btnGotoAddress.btnProfileFunction.setOnClickListener(view -> {
            onGoAddress(view);
        });

        activityProfileSettingBinding.btnChangePassword.btnProfileFunction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("Hello", "dsadsad");
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
    private void updateDob(){
        String myFormat="dd/MM/yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat);
        activityProfileSettingBinding.txtDob.setText(dateFormat.format(calendar.getTime()));
    }

    private void setUIOnChangedInfo(){
        activityProfileSettingBinding.txtName.setFocusable(false);
        activityProfileSettingBinding.txtName.setFocusable(false);

        activityProfileSettingBinding.ctnChangeInfo.setVisibility(View.GONE);
        activityProfileSettingBinding.btnChangeInfo.setVisibility(View.VISIBLE);

        activityProfileSettingBinding.txtDobLayout.setEndIconTintList(ColorStateList.valueOf(Color.GRAY));
        activityProfileSettingBinding.txtDobLayout.setEndIconOnClickListener(null);
    }

    private void onCancel(){
        activityProfileSettingBinding.txtName.setText(viewModel.getUser().getName());
        activityProfileSettingBinding.txtDob.setText(formatDate(viewModel.getUser().getDob()));
        activityProfileSettingBinding.txtPhone.setText(viewModel.getUser().getPhoneNumber());
    }

    private void onSave(){
        User user = viewModel.getUser();
        user.setName(activityProfileSettingBinding.txtName.getText().toString());
        user.setDob(convertDate(activityProfileSettingBinding.txtDob.getText().toString()));
        user.setPhoneNumber(activityProfileSettingBinding.txtPhone.getText().toString());
        viewModel.onSaveInfo(user);
    }

    private String formatDate(Date date){
        String pattern = "dd/MM/yyyy";
        DateFormat df = new SimpleDateFormat(pattern);
        return  df.format(date);
    }

    private Date convertDate(String s){
        String pattern = "dd/MM/yyyy";
        DateFormat df = new SimpleDateFormat(pattern);
        Date startDate;
        try{
            startDate = df.parse(s);
        } catch (Exception e) {
            startDate = DateTime.now().toDate();
        }
        return startDate;
    }

    public void onGoAddress(View view){
        String msg = "Nav to support";
        Snackbar snackbar = Snackbar
                .make(view, msg, Snackbar.LENGTH_LONG);
        snackbar.show();
    }
}