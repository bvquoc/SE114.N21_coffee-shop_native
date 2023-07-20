package com.example.coffee_shop_app.fragments.auth;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.coffee_shop_app.R;
import com.example.coffee_shop_app.activities.MainPageActivity;
import com.example.coffee_shop_app.models.User;
import com.example.coffee_shop_app.repository.AuthRepository;
import com.example.coffee_shop_app.utils.StringConverter;
import com.example.coffee_shop_app.utils.interfaces.Validate;
import com.example.coffee_shop_app.utils.validation.ConfirmPWValidate;
import com.example.coffee_shop_app.utils.validation.EmailValidate;
import com.example.coffee_shop_app.utils.validation.PasswordValidate;
import com.example.coffee_shop_app.utils.validation.TextValidator;
import com.example.coffee_shop_app.viewmodels.AuthViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;

public class SignUpFragment extends Fragment {

    private TextInputLayout emailLayout, passLayout, confirmPassLayout;
    private EditText emailEdit, passEdit, confirmPassEdit;
    private TextView moveToLoginText;
    private Button signUpButton;
    private AuthViewModel authVM;
    private NavController navController;
    private CardView googleBtn, facebookBtn;
    private BottomSheetDialog bottomSheetDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authVM = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
        setGoogleLauncher();
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
        googleBtn = view.findViewById(R.id.google_button);
        facebookBtn = view.findViewById(R.id.facebook_button);

        signUpButton = view.findViewById(R.id.btn_signup);
        navController = Navigation.findNavController(view);

        if (getActivity().getIntent().getExtras() != null
        ) {
            navController.navigate(R.id.action_signUpFragment_to_loginFragment);
        }
        moveToLoginText.setOnClickListener(v -> {
            navController.navigate(R.id.action_signUpFragment_to_loginFragment);
        });

        setOnSignUp(view);
        setValidation();
        setOnLoginGoogle();
        setOnLoginFacebook();
    }

    private void setOnLoginFacebook() {
    }

    //Google
    private GoogleSignInClient googleSignInClient;
    ActivityResultLauncher<Intent> googleResultLauncher;
    static final String TAG = "GOOGLE";
    private void setOnLoginGoogle() {
        String default_web_client_id = "922590350887-rh70buq5aeh98sqocffa4p4136vh4444.apps.googleusercontent.com";

        //Google configure
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                //if this show an error, just ignore it
                //it happen because of firebase security method
                .requestIdToken(default_web_client_id)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(getActivity(), googleSignInOptions);
        googleBtn.setOnClickListener(v -> {
            googleSignInClient.signOut().addOnCompleteListener(e -> {
                Log.e(TAG, "setOnLoginGoogle: go");
                Intent intent = googleSignInClient.getSignInIntent();
                googleResultLauncher.launch(intent);
            });
        });
    }

    private void setGoogleLauncher(){
        googleResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getData().getExtras() != null){
                            Intent data = result.getData();
                            onGoogleSignInEnd(data);
                        }
                    }
                }
        );
    }

    private void onGoogleSignInEnd(Intent data){
        Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
        ProgressDialog loadingDialog = ProgressDialog.show(getContext(), "",
                "Loading. Please wait...", true);
        try {
            GoogleSignInAccount account = accountTask.getResult();
            authVM.onGoogleSignIn(account, params -> {
                Log.e(TAG, "onGoogleSignInEnd: SUCCESS");
                loadingDialog.dismiss();
                User temp = (User) params[0];
                if(!temp.getActive()){
                    String msg = "Tài khoản của bạn đã bị chặn";
                    Snackbar snackbar = Snackbar
                            .make(getView(), msg, Snackbar.LENGTH_LONG);
                    snackbar.show();
                    authVM.onSignOut();
                } else {
                    if (temp.getPhoneNumber().equals("No Phone Number")) {
                        openChangeInfoDialog(getView(), temp);
                    } else {
                        onGoToMainPage();
                    }
                }
            }, params -> {
                Log.e(TAG, "onGoogleSignInEnd: Failed");
                loadingDialog.dismiss();
                String msg = "Đã có lỗi xảy ra, vui lòng thử lại sau";
                Snackbar snackbar = Snackbar
                        .make(getView(), msg, Snackbar.LENGTH_LONG);
                snackbar.show();
            });
        } catch (Exception e){
            Log.e(TAG, "onGoogleSignInEnd: Failed VM");
            loadingDialog.dismiss();
            String msg = "Đã có lỗi xảy ra, vui lòng thử lại sau";
            Snackbar snackbar = Snackbar
                    .make(getView(), msg, Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }

    private void openChangeInfoDialog(View view, User temp){

        bottomSheetDialog = new BottomSheetDialog(getContext(), R.style.BottomSheetTheme);

        bottomSheetDialog.setContentView(R.layout.change_info_bottom_sheet);

        EditText name = bottomSheetDialog.findViewById(R.id.txt_name_change);
        EditText dob = bottomSheetDialog.findViewById(R.id.txt_dob_change);
        EditText phone = bottomSheetDialog.findViewById(R.id.txt_phone_change);
        TextInputLayout dobLayout = bottomSheetDialog.findViewById(R.id.txt_dob_layout_change);

        if(temp.getName() != "No Name"){
            name.setText(temp.getName());
        }

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
                onGoToMainPage();
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

    private void onGoToMainPage(){
        NavHostFragment.findNavController(this).popBackStack();
        Intent intent = new Intent(getContext(), MainPageActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
    //end google

    private void setValidation() {
        Validate emailValidate = new EmailValidate();
        Validate passValidate = new PasswordValidate();
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
    }

    private void setOnSignUp(View view) {
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProgressDialog loadingDialog = ProgressDialog.show(getContext(), "",
                        "Loading. Please wait...", true);
                String email = emailEdit.getText().toString();
                String password = passEdit.getText().toString();
                String repass = confirmPassEdit.getText().toString();
                if (canSignUp(email, password, repass)) {
                    authVM.onSignUp(email, password, params -> {
                        loadingDialog.dismiss();
                        navController.navigate(R.id.action_signUpFragment_to_loginFragment);
                    }, params -> {
                        loadingDialog.dismiss();
                        Snackbar snackbar = Snackbar
                                .make(view, "Tài khoản đã tồn tại hoặc đã có lỗi xảy ra, vui lòng thử lại!", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    });
                } else {
                    loadingDialog.dismiss();
                    String msg;
                    if (email.isEmpty() || password.isEmpty() || repass.isEmpty()) {
                        msg = "Vui lòng điền đầy đủ các trường";
                    } else {
                        msg = "Có gì đó không ổn, hãy thử lại!";
                    }
                    Snackbar snackbar = Snackbar
                            .make(view, msg, Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });
    }

    private Boolean canSignUp(String email, String pass, String repass) {
        Boolean repassOK = new ConfirmPWValidate(pass).validate(repass);
        Boolean emailOK = new EmailValidate().validate(email);
        Boolean passOK = new PasswordValidate().validate(pass);
        return repassOK && emailOK && passOK;
    }
}