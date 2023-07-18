package com.example.coffee_shop_staff_admin.repositories;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.coffee_shop_staff_admin.models.User;
import com.example.coffee_shop_staff_admin.utils.interfaces.CallBack;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;

import java.util.Map;

public class AuthRepository {
    private static AuthRepository instance;

    public static AuthRepository getInstance() {
        if (instance == null) {
            instance = new AuthRepository();
        }
        return instance;
    }

    public Boolean isFirstTime = true;

    MutableLiveData<User> currentUserLiveData;

    public MutableLiveData<User> getCurrentUserLiveData() {
        return currentUserLiveData;
    }

    public MutableLiveData<Boolean> getIsLoggedInLiveData() {
        return isLoggedInLiveData;
    }

    MutableLiveData<Boolean> isLoggedInLiveData;

    @Nullable
    public User getCurrentUser() {
        return currentUserLiveData.getValue();
    }

    FirebaseAuth firebaseAuth;
    FirebaseFirestore fireStore;

    public AuthRepository() {
        currentUserLiveData = new MutableLiveData<>();
        isLoggedInLiveData = new MutableLiveData<>();
        firebaseAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();

        FirebaseUser rawUser = firebaseAuth.getCurrentUser();

        if (rawUser != null && currentUserLiveData.getValue() == null) {
            getUser(rawUser.getUid(), params -> {
                currentUserLiveData.postValue(User.fromFireBase((DocumentSnapshot) params[0]));
            });
            isLoggedInLiveData.postValue(true);
        }
    }
    public void emailLogin(String email, String password, CallBack onSuccess, CallBack onFailed) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d("login", "Success");
                    FirebaseUser rawUser = firebaseAuth.getCurrentUser();
                    if (rawUser != null) {
                        getUser(rawUser.getUid(), param -> {
                            User post = User.fromFireBase((DocumentSnapshot) param[0]);
                            currentUserLiveData.postValue(post);
                            onSuccess.invoke(post);
                        });
                    }
                    isLoggedInLiveData.postValue(true);
                } else {
                    Log.d("login", "Failed");
                    currentUserLiveData.postValue(null);
                    isLoggedInLiveData.postValue(false);
                    onFailed.invoke();
                }
            }
        });
    }

    public void signOut() {
        firebaseAuth.signOut();
        currentUserLiveData.postValue(null);
        isLoggedInLiveData.postValue(false);
    }

    @Nullable
    public void getUser(String id, CallBack onSuccess) {
        DocumentReference userRef = fireStore.collection("users").document(id);
        userRef
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    onSuccess.invoke(documentSnapshot);
                })
                .addOnFailureListener(e -> {
                    Log.e("auth repository", "get user failed.");
                });
    }

    public void update(User user, CallBack onSuccess, CallBack onFailed){
        fireStore.collection("users").document(user.getId()).update(User.toFireStore(user)).addOnSuccessListener((temp) -> {
            currentUserLiveData.postValue(user);
            onSuccess.invoke();
        }).addOnFailureListener(e -> {
            Log.e("auth repository", "update user failed.");
            onFailed.invoke();
        });;
    }

    public void push(User user){
        Map<String, Object> data = User.toFireStore(user);
        fireStore.collection("users").document(user.getId()).set(data).addOnFailureListener((temp) -> {
            Log.e("auth repository", "push user failed.");
        });
    }
}