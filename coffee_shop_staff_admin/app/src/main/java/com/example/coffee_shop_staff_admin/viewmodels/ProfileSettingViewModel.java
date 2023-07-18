package com.example.coffee_shop_staff_admin.viewmodels;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.example.coffee_shop_staff_admin.models.User;
import com.example.coffee_shop_staff_admin.repositories.AuthRepository;
import com.example.coffee_shop_staff_admin.utils.interfaces.CallBack;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileSettingViewModel extends ViewModel {

    public void setUser(User user) {
        this.user = user;
    }
    AuthRepository authRepository;

    User user;

    public User getUser() {
        return user;
    }

    public ProfileSettingViewModel() {
        user = AuthRepository.getInstance().getCurrentUserLiveData().getValue();
        authRepository = AuthRepository.getInstance();
    }

    public void onSaveInfo(User user){
        authRepository.update(user, params -> {
            //do nothing
        }, params -> {
            //do nothing
        });
        this.user = user;
    }

    public void onChangePassword(String oldPass, String newPass, CallBack onSuccess, CallBack onFailed){
        FirebaseUser rawuser = FirebaseAuth.getInstance().getCurrentUser();

        AuthCredential credential = EmailAuthProvider.getCredential(rawuser.getEmail(), oldPass);

        rawuser.reauthenticate(credential).addOnSuccessListener(e -> {
            rawuser.updatePassword(newPass).addOnSuccessListener(ev -> {
                Log.d("Profile", "Password Changed");
                onSuccess.invoke();
            }).addOnFailureListener(ev -> {
                Log.d("Profile", "Change password failed");
                onFailed.invoke();
            });
        }).addOnFailureListener(e -> {
            Log.d("Profile", "Change password failed");
            onFailed.invoke();
        });
    }
}