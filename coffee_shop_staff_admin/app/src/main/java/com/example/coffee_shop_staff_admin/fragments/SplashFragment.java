package com.example.coffee_shop_staff_admin.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.coffee_shop_staff_admin.R;
import com.example.coffee_shop_staff_admin.activities.AuthActivity;
import com.example.coffee_shop_staff_admin.activities.MainPageAdminActivity;
import com.example.coffee_shop_staff_admin.activities.StoreManageActivity;
import com.example.coffee_shop_staff_admin.models.User;
import com.example.coffee_shop_staff_admin.repositories.AuthRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;


public class SplashFragment extends Fragment {

    MutableLiveData<User> currentUser;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        currentUser = AuthRepository.getInstance().getCurrentUserLiveData();
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FirebaseUser raw = firebaseAuth.getCurrentUser();
        if (raw != null && currentUser.getValue() == null) {
            SharedPreferences pref = getActivity().getPreferences(Context.MODE_PRIVATE);
            Boolean val = pref.getBoolean(getString(R.string.is_remember_me), true);
            if(!val){
                AuthRepository.getInstance().signOut();
                Intent intent = new Intent(getContext(), AuthActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
            else {
                AuthRepository.getInstance().getUser(raw.getUid(), params -> {
                    User temp = User.fromFireBase((DocumentSnapshot) params[0]);

                    if(!temp.isActive() || (!temp.isAdmin() && !temp.isStaff())){
                        AuthRepository.getInstance().signOut();
                        Intent intent = new Intent(getContext(), AuthActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                    else {
                        currentUser.postValue(temp);
                        AuthRepository.getInstance().getIsLoggedInLiveData().postValue(true);
                        Intent intent = new Intent(getContext(), temp.isAdmin() ? MainPageAdminActivity.class : StoreManageActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                });
            }
        }
        else {
            Intent intent = new Intent(getContext(), AuthActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
}