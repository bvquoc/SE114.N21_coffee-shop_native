package com.example.coffee_shop_app.fragments.auth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.example.coffee_shop_app.R;
import com.example.coffee_shop_app.activities.MainPageActivity;
import com.example.coffee_shop_app.models.User;
import com.example.coffee_shop_app.utils.interfaces.CallBack;
import com.example.coffee_shop_app.utils.interfaces.Validate;
import com.example.coffee_shop_app.utils.validation.EmailValidate;
import com.example.coffee_shop_app.utils.validation.PasswordValidate;
import com.example.coffee_shop_app.utils.validation.TextValidator;
import com.example.coffee_shop_app.viewmodels.AuthViewModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {
    private TextInputLayout emailLayout, passLayout;
    private EditText emailEdit, passEdit;
    private TextView signInText;
    private CheckBox rememberMe;
    private Button signInButton;
    private AuthViewModel authVM;
    private NavController navController;
    private SavedStateHandle savedStateHandle;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authVM = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

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

//        savedStateHandle = NavHostFragment.findNavController(this).getPreviousBackStackEntry().getSavedStateHandle();
//        savedStateHandle.set("IS_LOGGED_IN", false);

        Toolbar toolbar = view.findViewById(R.id.my_toolbar);
        toolbar.setTitle("");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_loginFragment_to_signUpFragment);
            }
        });

        Validate emailValidate = new EmailValidate();
        Validate passValidate = new PasswordValidate();

        signInButton.setOnClickListener(v -> {
            String email = emailEdit.getText().toString();
            String password = passEdit.getText().toString();
            SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            if(rememberMe.isChecked()){
                editor.putBoolean(getString(R.string.is_remember_me), true);
            }
            else {
                editor.putBoolean(getString(R.string.is_remember_me), false);
            }
            editor.apply();

            if (canLogin(email, password)) {
                authVM.onEmailSignIn(email, password, params -> {
//                    savedStateHandle.set("IS_LOGGED_IN", true);
                    NavHostFragment.findNavController(this).popBackStack();
                    Intent intent = new Intent(getContext(), MainPageActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }, params -> {
                    String msg = "Email hoặc mật khẩu sai";
                    Snackbar snackbar = Snackbar
                            .make(view, msg, Snackbar.LENGTH_LONG);
                    snackbar.show();
                });
            } else {
                String msg = "Vui lòng điền đầy đủ các trường";
                Snackbar snackbar = Snackbar
                        .make(view, msg, Snackbar.LENGTH_LONG);
                snackbar.show();
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
    }

    private Boolean canLogin(String email, String password) {
        return new EmailValidate().validate(email) && new PasswordValidate().validate(password);
    }
}