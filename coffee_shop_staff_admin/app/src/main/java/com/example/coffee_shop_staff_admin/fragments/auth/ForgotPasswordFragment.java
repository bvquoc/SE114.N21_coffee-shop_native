package com.example.coffee_shop_staff_admin.fragments.auth;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.coffee_shop_staff_admin.R;
import com.example.coffee_shop_staff_admin.repositories.AuthRepository;
import com.example.coffee_shop_staff_admin.utils.interfaces.Validate;
import com.example.coffee_shop_staff_admin.utils.validation.EmailValidate;
import com.example.coffee_shop_staff_admin.utils.validation.PasswordValidate;
import com.example.coffee_shop_staff_admin.utils.validation.TextValidator;
import com.google.android.material.textfield.TextInputLayout;

public class ForgotPasswordFragment extends Fragment {
    private static String TAG = "Forgot Fragment";
    EditText emailText;
    TextInputLayout emailLayout;
    Button sendEmail;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forgot_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        Toolbar toolbar = view.findViewById(R.id.my_toolbar);
        toolbar.setTitle("");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        
        emailText = view.findViewById(R.id.txt_email_forgot);
        emailLayout = view.findViewById(R.id.txt_email_layout_forgot);
        sendEmail = view.findViewById(R.id.btn_forgot);
        setValidation();
        
        sendEmail.setOnClickListener(v -> {
            AuthRepository.getInstance().sendForgotPassword(emailText.getText().toString(), params -> {
                Log.d(TAG, "success");
            }, params -> {
                Log.d(TAG, "Failed");
            });
        });
    }

    private void setValidation() {
        Validate emailValidate = new EmailValidate();
        emailText.addTextChangedListener(new TextValidator(emailText) {
            @Override
            public void validate(TextView textView, String text) {
                if (text.isEmpty()) {
                    emailLayout.setError(null);
                }
                if (!emailValidate.validate(text)) {
                    emailLayout.setError(emailValidate.validator(text));
                } else {
                    emailLayout.setError(null);
                }
            }
        });
    }
}