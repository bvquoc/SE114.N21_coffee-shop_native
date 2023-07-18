package com.example.coffee_shop_app.fragments.auth;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.coffee_shop_app.R;
import com.example.coffee_shop_app.models.User;
import com.example.coffee_shop_app.utils.interfaces.Validate;
import com.example.coffee_shop_app.utils.validation.ConfirmPWValidate;
import com.example.coffee_shop_app.utils.validation.EmailValidate;
import com.example.coffee_shop_app.utils.validation.PasswordValidate;
import com.example.coffee_shop_app.utils.validation.TextValidator;
import com.example.coffee_shop_app.viewmodels.AuthViewModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

public class SignUpFragment extends Fragment {

    private TextInputLayout emailLayout, passLayout, confirmPassLayout;
    private EditText emailEdit, passEdit, confirmPassEdit;
    private TextView moveToLoginText;
    private Button signUpButton;
    private AuthViewModel authVM;
    private NavController navController;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authVM = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        emailEdit = view.findViewById(R.id.txt_email_signup);
        passEdit = view.findViewById(R.id.txt_password_signup);
        confirmPassEdit = view.findViewById(R.id.txt_repassword_signup);
        emailLayout = view.findViewById(R.id.txt_email_layout_signup);
        passLayout = view.findViewById(R.id.txt_pass_layout_signup);
        confirmPassLayout = view.findViewById(R.id.txt_repass_layout_signup);
        moveToLoginText = view.findViewById(R.id.btn_move_to_login);

        signUpButton = view.findViewById(R.id.btn_signup);
        navController = Navigation.findNavController(view);

        Validate emailValidate = new EmailValidate();
        Validate passValidate = new PasswordValidate();

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEdit.getText().toString();
                String password = passEdit.getText().toString();
                String repass = confirmPassEdit.getText().toString();
                if(canSignUp(email, password, repass)) {
                    authVM.onSignUp(email, password, params -> {
                        navController.navigate(R.id.action_signUpFragment_to_loginFragment);
                    }, params -> {
                        Snackbar snackbar = Snackbar
                                .make(view, "Đã có lỗi xảy ra, vui lòng thử lại!", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    });
                }
                else {
                    String msg;
                    if (email.isEmpty() || password.isEmpty() || repass.isEmpty()) {
                        msg = "Vui lòng điền đầy đủ các trường";
                    }
                    else {
                        msg = "Có gì đó không ổn, hãy thử lại!";
                    }
                    Snackbar snackbar = Snackbar
                            .make(view, msg, Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });

        emailEdit.addTextChangedListener(new TextValidator(emailEdit) {
            @Override
            public void validate(TextView textView, String text) {
                if (!emailValidate.validate(text)) {
                    emailLayout.setError(emailValidate.validator(text));
                } else {
                    emailLayout.setError(null);
                }
            }
        });

        passEdit.addTextChangedListener(new TextValidator(passEdit) {
            @Override
            public void validate(TextView textView, String text) {
                if (!passValidate.validate(text)) {
                    passLayout.setError(passValidate.validator(text));
                } else {
                    passLayout.setError(null);
                }
            }
        });

        confirmPassEdit.addTextChangedListener(new TextValidator(confirmPassEdit) {
            @Override
            public void validate(TextView textView, String text) {
                Validate validate = new ConfirmPWValidate(passEdit.getText().toString());
                if (!validate.validate(text)) {
                    confirmPassLayout.setError(validate.validator(text));
                } else {
                    confirmPassLayout.setError(null);
                }
            }
        });

        moveToLoginText.setOnClickListener(v -> {
            navController.navigate(R.id.action_signUpFragment_to_loginFragment);
        });
    }

    private Boolean canSignUp(String email, String pass, String repass){
        Boolean repassOK = new ConfirmPWValidate(pass).validate(repass);
        Boolean emailOK = new EmailValidate().validate(email);
        Boolean passOK = new PasswordValidate().validate(pass);
        return  repassOK && emailOK && passOK;
    }
}