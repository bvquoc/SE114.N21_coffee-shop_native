package com.example.coffee_shop_staff_admin.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.coffee_shop_staff_admin.R;
import com.example.coffee_shop_staff_admin.models.User;
import com.example.coffee_shop_staff_admin.repositories.AuthRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

public class AuthActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    MutableLiveData<User> currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        currentUser = AuthRepository.getInstance().getCurrentUserLiveData();

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser raw = firebaseAuth.getCurrentUser();
        if (raw != null && currentUser.getValue() == null) {
            SharedPreferences pref = getPreferences(Context.MODE_PRIVATE);
            Boolean val = pref.getBoolean(getString(R.string.is_remember_me), true);
            if(!val){
                AuthRepository.getInstance().signOut();
            }
            else {
                AuthRepository.getInstance().getUser(raw.getUid(), params -> {
                    currentUser.postValue(User.fromFireBase((DocumentSnapshot) params[0]));
                });
                AuthRepository.getInstance().getIsLoggedInLiveData().postValue(true);
                Intent intent = new Intent(getApplicationContext(), MainPageAdminActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        }
    }
}