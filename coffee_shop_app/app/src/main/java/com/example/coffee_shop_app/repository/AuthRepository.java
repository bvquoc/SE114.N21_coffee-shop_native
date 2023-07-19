package com.example.coffee_shop_app.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.coffee_shop_app.models.User;
import com.example.coffee_shop_app.utils.interfaces.CallBack;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
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

    public void emailSignUp(String email, String password, CallBack onSuccess, CallBack onFailed) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d("sign up", "Success");
                    String id = task.getResult().getUser().getUid();
                    FirebaseUser rawUser = task.getResult().getUser();
                    getUser(id, params -> {
                        User user = User.fromFireStore((DocumentSnapshot) params[0]);
                        if (user != null) {
                            return;
                        }
                        user = new User(id, "No name", "No Phone Number", rawUser.getEmail(), DateTime.now().toDate(), true, false, false, "https://st3.depositphotos.com/6672868/13701/v/450/depositphotos_137014128-stock-illustration-user-profile-icon.jpg", "https://img.freepik.com/free-vector/restaurant-mural-wallpaper_23-2148703851.jpg?w=740&t=st=1680897435~exp=1680898035~hmac=8f6c47b6646a831c4a642b560cf9b10f1ddf80fda5d9d997299e1b2f71fe4cb9", DateTime.now().toDate());
                        if (user != null) {
                            push(user);
                        }
                        onSuccess.invoke();
                    });
                } else {
                    Log.d("sign up", "Failed");
                    onFailed.invoke();
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

    public void googleLogin(GoogleSignInAccount account, CallBack onSuccess, CallBack onFailed) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        Log.e("TEST", "googleLogin: " + account.getEmail() );
        firebaseAuth.signInWithCredential(credential).addOnSuccessListener(v -> {
            Log.e("GOOGLE LOGIN SUCCESS", v.getUser().getUid());
            FirebaseUser rawUser = v.getUser();
            if (rawUser != null) {
                getUser(rawUser.getUid(), params -> {
                    User user = User.fromFireStore((DocumentSnapshot) params[0]);
                    if (user == null) {
                        String name = rawUser.getDisplayName() != null ? rawUser.getDisplayName() : "No Name";
                        String avatarUrl = rawUser.getPhotoUrl() != null ? rawUser.getPhotoUrl().toString() : "https://st3.depositphotos.com/6672868/13701/v/450/depositphotos_137014128-stock-illustration-user-profile-icon.jpg";

                        user = new User(rawUser.getUid(), name, "No Phone Number", rawUser.getEmail(), DateTime.now().toDate(), true, false, false, avatarUrl, "https://img.freepik.com/free-vector/restaurant-mural-wallpaper_23-2148703851.jpg?w=740&t=st=1680897435~exp=1680898035~hmac=8f6c47b6646a831c4a642b560cf9b10f1ddf80fda5d9d997299e1b2f71fe4cb9", DateTime.now().toDate());

                        push(user);
                    }

                    currentUserLiveData.postValue(user);
                    if (onSuccess != null) {
                        onSuccess.invoke(user);
                    }
                });
            }
            isLoggedInLiveData.postValue(true);

        }).addOnFailureListener(v -> {
            if (onFailed != null) {
                onFailed.invoke();
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

    public void update(User user, CallBack onSuccess, CallBack onFailed) {
        fireStore.collection("users").document(user.getId()).update(User.toFireStore(user)).addOnSuccessListener((temp) -> {
            currentUserLiveData.postValue(user);
            onSuccess.invoke();
        }).addOnFailureListener(e -> {
            Log.e("auth repository", "update user failed.");
            onFailed.invoke();
        });
        ;
    }

    public void push(User user) {
        Map<String, Object> data = User.toFireStore(user);
        fireStore.collection("users").document(user.getId()).set(data).addOnFailureListener((temp) -> {
            Log.e("auth repository", "push user failed.");
        });
    }

    public void sendForgotPassword(String email, CallBack onSuccess, CallBack onFailed) {
        firebaseAuth.fetchSignInMethodsForEmail(email)
                .addOnSuccessListener(v -> {
                    if (v.getSignInMethods().isEmpty()) {
                        onFailed.invoke();
                    } else {
                        firebaseAuth.sendPasswordResetEmail(email)
                                .addOnSuccessListener(d -> {
                                    onSuccess.invoke();
                                })
                                .addOnFailureListener(d -> {
                                    onFailed.invoke();
                                });
                    }
                })
                .addOnFailureListener(v -> {
                    onFailed.invoke();
                });

    }
}
