package com.example.coffee_shop_app.viewmodels;

import android.app.Application;
import android.telecom.Call;

import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.example.coffee_shop_app.models.User;
import com.example.coffee_shop_app.repository.AuthRepository;
import com.example.coffee_shop_app.utils.interfaces.CallBack;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;

public class AuthViewModel extends ViewModel {


    public MutableLiveData<User> getUserData() {
        return userData;
    }

    public MutableLiveData<Boolean> getIsLoggedIn() {
        return isLoggedIn;
    }

    MutableLiveData<User> userData;
    MutableLiveData<Boolean> isLoggedIn;
    AuthRepository repository;

    public AuthViewModel(){
        repository = AuthRepository.getInstance();
        userData = repository.getCurrentUserLiveData();
        isLoggedIn = repository.getIsLoggedInLiveData();
    }

    public void onSignUp(String email, String password, CallBack onSuccess, CallBack onFailed){
        repository.emailSignUp(email, password, onSuccess, onFailed);
    }

    public void onEmailSignIn(String email, String password, CallBack onSuccess, CallBack onFailed){
        repository.emailLogin(email, password, onSuccess, onFailed);
    }

    public void onGoogleSignIn(GoogleSignInAccount account, CallBack onSuccess, CallBack onFailed){
        repository.googleLogin(account, onSuccess, onFailed);
    }

    public void onFacebookSignIn(){

    }

    public void onForgotPassword(String email){

    }

    public void onSignOut(){
        repository.signOut();
    }

    public void onUpdate(User user, CallBack onSuccess, CallBack onFailed){
        repository.update(user, onSuccess, onFailed);
    }
}
