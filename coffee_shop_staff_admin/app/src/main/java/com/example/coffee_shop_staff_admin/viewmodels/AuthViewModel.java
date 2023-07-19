package com.example.coffee_shop_staff_admin.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.coffee_shop_staff_admin.models.User;
import com.example.coffee_shop_staff_admin.repositories.AuthRepository;
import com.example.coffee_shop_staff_admin.utils.interfaces.CallBack;

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

    public void onEmailSignIn(String email, String password, CallBack onSuccess, CallBack onFailed){
        repository.emailLogin(email, password, onSuccess, onFailed);
    }

    public void onGoogleSignIn(){

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
