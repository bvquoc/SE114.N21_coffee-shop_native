package com.example.coffee_shop_app.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.coffee_shop_app.models.User;
import com.example.coffee_shop_app.utils.interfaces.CallBack;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
                currentUserLiveData.postValue(User.fromFireStore((DocumentSnapshot) params[0]));
            });
            isLoggedInLiveData.postValue(true);
        }
    }

    public void emailSignUp(String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d("sign up", "Success");
                    String id = task.getResult().getUser().getUid();
                    FirebaseUser rawUser = task.getResult().getUser();
                    getUser(id, params -> {
                        User user = User.fromFireStore((DocumentSnapshot) params[0]);
                        if(user != null){
                            return;
                        }
                        user = new User(id, "No name", "No Phone Number", rawUser.getEmail(), DateTime.now().toDate(), true, false, false, "https://st3.depositphotos.com/6672868/13701/v/450/depositphotos_137014128-stock-illustration-user-profile-icon.jpg", "https://img.freepik.com/free-vector/restaurant-mural-wallpaper_23-2148703851.jpg?w=740&t=st=1680897435~exp=1680898035~hmac=8f6c47b6646a831c4a642b560cf9b10f1ddf80fda5d9d997299e1b2f71fe4cb9", DateTime.now().toDate());
                        if(user != null){
                            push(user);
                        }
                    });
                } else {
                    Log.d("sign up", "Failed");
                }
            }
        });
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
                            User post = User.fromFireStore((DocumentSnapshot) param[0]);
                            currentUserLiveData.postValue(post);
                        });
                    }
                    isLoggedInLiveData.postValue(true);
                    onSuccess.invoke();
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

    public void update(User user){
        fireStore.collection("users").document(user.getId()).update(User.toFireStore(user)).addOnSuccessListener((temp) -> {
            currentUserLiveData.postValue(user);
        }).addOnFailureListener(e -> {
            Log.e("auth repository", "update user failed.");
        });;
    }

    public void push(User user){
        Map<String, Object> data = User.toFireStore(user);
        fireStore.collection("users").document(user.getId()).set(data).addOnFailureListener((temp) -> {
            Log.e("auth repository", "push user failed.");
        });
    }
}
