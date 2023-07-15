package com.example.coffee_shop_staff_admin.repositories;
import com.example.coffee_shop_staff_admin.models.User;
import com.example.coffee_shop_staff_admin.utils.interfaces.OnRequestUserDataListener;
import com.example.coffee_shop_staff_admin.utils.interfaces.UpdateDataListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class UserRepository{
    //singleton
    private static UserRepository instance;
    private final FirebaseFirestore fireStore;
    private UserRepository() {
        //define fireStore
        fireStore = FirebaseFirestore.getInstance();
    }
    public static synchronized UserRepository getInstance() {
        if (instance == null) {
            instance = new UserRepository();
        }
        return instance;
    }

    public void getUserByEmailFromFireStore(String email, OnRequestUserDataListener listener) {
        Query query = fireStore.collection("users").whereEqualTo("email", email);

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null && !querySnapshot.isEmpty()) {
                    DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                    User user = User.fromFireBase(document);
                    listener.onRequestUserData(user);
                } else {
                    listener.onRequestUserData(null);
                }
            } else {
                listener.onRequestUserData(null);
            }
        });
    }
    public void updateUserActiveAccess(String userId, boolean isActive, UpdateDataListener listener)
    {
        DocumentReference userRef = fireStore.collection("users").document(userId);
        Map<String, Object> newData = new HashMap<>();
        newData.put("isActive", isActive);
        userRef.update(newData)
                .addOnSuccessListener(aVoid -> listener.onUpdateData(true))
                .addOnFailureListener(e -> listener.onUpdateData(false));
    }
    public void updateUserStaffAccess(String userId, boolean isStaff, String store, UpdateDataListener listener)
    {
        DocumentReference userRef = fireStore.collection("users").document(userId);
        Map<String, Object> newData = new HashMap<>();
        newData.put("isStaff", isStaff);
        if(isStaff)
        {
            newData.put("store", store);
        }
        userRef.update(newData)
                .addOnSuccessListener(aVoid -> listener.onUpdateData(true))
                .addOnFailureListener(e -> listener.onUpdateData(false));
    }
    public void updateUserAdminAccess(String userId, boolean isAdmin, UpdateDataListener listener)
    {
        DocumentReference userRef = fireStore.collection("users").document(userId);
        Map<String, Object> newData = new HashMap<>();
        newData.put("isAdmin", isAdmin);
        userRef.update(newData)
                .addOnSuccessListener(aVoid -> listener.onUpdateData(true))
                .addOnFailureListener(e -> listener.onUpdateData(false));
    }
}
