package com.example.coffee_shop_app.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavHostController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.coffee_shop_app.R;
import com.example.coffee_shop_app.models.User;
import com.example.coffee_shop_app.repository.AuthRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

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
                    currentUser.postValue(User.fromFireStore((DocumentSnapshot) params[0]));
                });
                AuthRepository.getInstance().getIsLoggedInLiveData().postValue(true);
                Intent intent = new Intent(getApplicationContext(), MainPageActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }
}