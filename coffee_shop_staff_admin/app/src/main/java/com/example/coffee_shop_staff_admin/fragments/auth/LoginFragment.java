package com.example.coffee_shop_staff_admin.fragments.auth;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.example.coffee_shop_staff_admin.R;
import com.example.coffee_shop_staff_admin.activities.AuthActivity;
import com.example.coffee_shop_staff_admin.activities.MainPageAdminActivity;
import com.example.coffee_shop_staff_admin.activities.StoreManageActivity;
import com.example.coffee_shop_staff_admin.models.Store;
import com.example.coffee_shop_staff_admin.models.User;
import com.example.coffee_shop_staff_admin.repositories.AuthRepository;
import com.example.coffee_shop_staff_admin.repositories.StoreRepository;
import com.example.coffee_shop_staff_admin.utils.StringConverter;
import com.example.coffee_shop_staff_admin.utils.interfaces.Validate;
import com.example.coffee_shop_staff_admin.utils.validation.EmailValidate;
import com.example.coffee_shop_staff_admin.utils.validation.PasswordValidate;
import com.example.coffee_shop_staff_admin.utils.validation.TextValidator;
import com.example.coffee_shop_staff_admin.viewmodels.AuthViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;
import java.util.List;

public class LoginFragment extends Fragment {

    private TextInputLayout emailLayout, passLayout;
    private EditText emailEdit, passEdit;
    private CheckBox rememberMe;
    private Button signInButton;
    private TextView goForgot;
    private AuthViewModel authVM;
    private NavController navController;
    private SavedStateHandle savedStateHandle;
    private BottomSheetDialog bottomSheetDialog;
    private MutableLiveData<Store> currentStore;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authVM = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
        currentStore = StoreRepository.getInstance().getCurrentStore();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        emailEdit = view.findViewById(R.id.txt_email_login);
        passEdit = view.findViewById(R.id.txt_password_login);
        emailLayout = view.findViewById(R.id.txt_email_layout_login);
        passLayout = view.findViewById(R.id.txt_pass_layout_login);
        signInButton = view.findViewById(R.id.btn_login);
        navController = Navigation.findNavController(view);
        rememberMe = view.findViewById(R.id.checkbox_remember);
        goForgot = view.findViewById(R.id.btn_go_forgot);

        setValidation();
        setOnSignIn(view);
        setOnGoForgot();
    }

    private void setOnGoForgot() {
        goForgot.setOnClickListener(v -> {
            navController.navigate(R.id.action_loginFragment_to_forgotPasswordFragment);
        });
    }

    private void setValidation() {
        Validate emailValidate = new EmailValidate();
        Validate passValidate = new PasswordValidate();

        emailEdit.addTextChangedListener(new TextValidator(emailEdit) {
            @Override
            public void validate(TextView textView, String text) {
                if (text == null || text.isEmpty()) {
                    emailLayout.setError(null);
                } else {
                    if (!emailValidate.validate(text)) {
                        emailLayout.setError(emailValidate.validator(text));
                    } else {
                        emailLayout.setError(null);
                    }
                }
            }
        });

        passEdit.addTextChangedListener(new TextValidator(passEdit) {
            @Override
            public void validate(TextView textView, String text) {
                if (text == null || text.isEmpty()) {
                    passLayout.setError(null);
                } else {
                    if (!passValidate.validate(text)) {
                        passLayout.setError(passValidate.validator(text));
                    } else {
                        passLayout.setError(null);
                    }
                }
            }
        });
    }

    private void setOnSignIn(View view) {
        signInButton.setOnClickListener(v -> {
            ProgressDialog loadingDialog = ProgressDialog.show(getContext(), "",
                    "Loading. Please wait...", true);

            String email = emailEdit.getText().toString();
            String password = passEdit.getText().toString();


            //SharedPrefs set
            SharedPreferences sharedPref = ((AppCompatActivity) requireActivity()).getSharedPreferences("com.example.com.example.coffee_shop_staff_admin", Context.MODE_PRIVATE);

            if(rememberMe.isChecked()){
                sharedPref.edit().putBoolean("isRememberMe", true).apply();
            }
            else {
                sharedPref.edit().putBoolean("isRememberMe", false).apply();
            }
            //end set

            if (canLogin(email, password)) {
                authVM.onEmailSignIn(email, password, params -> {
                    User temp = (User) params[0];
                    loadingDialog.dismiss();
                    if (!roleValidate(temp)) {
                        String msg = "Bạn không có quyền truy cập vào phần này!";
                        Snackbar snackbar = Snackbar
                                .make(view, msg, Snackbar.LENGTH_LONG);
                        snackbar.show();
                        authVM.onSignOut();
                    } else {
                        if (temp.getPhoneNumber().equals("No Phone Number")) {
                            openChangeInfoDialog(view, temp);
                        } else {
                            onGoToMainPage(temp);
                        }
                    }
                }, params -> {
                    loadingDialog.dismiss();
                    String msg = "Email hoặc mật khẩu sai";
                    Snackbar snackbar = Snackbar
                            .make(view, msg, Snackbar.LENGTH_LONG);
                    snackbar.show();
                });
            } else {
                loadingDialog.dismiss();
                String msg = "Vui lòng điền đầy đủ các trường";
                Snackbar snackbar = Snackbar
                        .make(view, msg, Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });

    }

    private void openChangeInfoDialog(View view, User temp) {
        bottomSheetDialog = new BottomSheetDialog(getContext(), R.style.BottomSheetTheme);

        bottomSheetDialog.setContentView(R.layout.change_info_bottom_sheet);

        EditText name = bottomSheetDialog.findViewById(R.id.txt_name_change);
        EditText dob = bottomSheetDialog.findViewById(R.id.txt_dob_change);
        EditText phone = bottomSheetDialog.findViewById(R.id.txt_phone_change);
        TextInputLayout dobLayout = bottomSheetDialog.findViewById(R.id.txt_dob_layout_change);

        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                calendar.set(Calendar.YEAR, i);
                calendar.set(Calendar.MONTH, i1);
                calendar.set(Calendar.DAY_OF_MONTH, i2);
                dob.setText(StringConverter.DateToString(calendar.getTime()));
            }
        };

        dobLayout.setEndIconOnClickListener((v -> {
            new DatePickerDialog(getContext(), R.style.DialogTheme, date, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();

        }));

        Button changeButton = bottomSheetDialog.findViewById(R.id.btn_accept_change_info);
        changeButton.setOnClickListener(v -> {
            ProgressDialog loadingDialog = ProgressDialog.show(getContext(), "",
                    "Loading. Please wait...", true);
            temp.setDob(StringConverter.StringToDate(dob.getText().toString()));
            temp.setName(name.getText().toString());
            temp.setPhoneNumber(phone.getText().toString());
            authVM.onUpdate(temp, params -> {
                loadingDialog.dismiss();
                bottomSheetDialog.dismiss();
                onGoToMainPage(temp);
            }, params -> {
                loadingDialog.dismiss();
                String msg = "Đã có lỗi xảy ra, vui lòng thử lại sau!";
                Snackbar snackbar = Snackbar
                        .make(view, msg, Snackbar.LENGTH_LONG);
                snackbar.show();
                bottomSheetDialog.cancel();
            });
        });

        bottomSheetDialog.findViewById(R.id.close_button).setOnClickListener(v -> {
            bottomSheetDialog.cancel();
        });

        bottomSheetDialog.setOnCancelListener(v -> {
            String msg = "Vui lòng cập nhật thông tin!";
            Snackbar snackbar = Snackbar
                    .make(view, msg, Snackbar.LENGTH_LONG);
            snackbar.show();
            AuthRepository.getInstance().signOut();
        });

        // Set the behavior to STATE_EXPANDED
        View bottomSheetInternal = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        BottomSheetBehavior.from(bottomSheetInternal).setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetDialog.show();
    }

    private Boolean roleValidate(User user) {
        if (!user.isActive() || (!user.isStaff() && !user.isAdmin())) {
            return false;
        }
        if(user.isStaff() && !user.isAdmin()){
            if(StoreRepository.getInstance().getStoreListMutableLiveData().getValue() == null){
                return false;
            }
            return StoreRepository.getInstance().getStoreListMutableLiveData().getValue().stream().anyMatch(item -> item.getId().equals(user.getStore()));
        }
        return true;
    }

    private void onGoToMainPage(User user) {
        if (user.getStore() != null && !user.getStore().isEmpty()) {
            List<Store> storeList = StoreRepository.getInstance().getStoreListMutableLiveData().getValue();
            for (Store item : storeList) {
                if (item.getId().equals(user.getStore())) {
                    currentStore.postValue(item);
                    break;
                }
            }
        }
        //need to adjust
        NavHostFragment.findNavController(this).popBackStack();
        Intent intent = new Intent(getContext(), user.isAdmin() ? MainPageAdminActivity.class : StoreManageActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private Boolean canLogin(String email, String password) {
        return new EmailValidate().validate(email) && new PasswordValidate().validate(password);
    }


}