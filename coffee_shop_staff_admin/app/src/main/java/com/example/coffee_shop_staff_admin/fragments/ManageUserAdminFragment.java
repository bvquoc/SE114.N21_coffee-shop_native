package com.example.coffee_shop_staff_admin.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.coffee_shop_staff_admin.R;
import com.example.coffee_shop_staff_admin.activities.AddNewUserActivity;
import com.example.coffee_shop_staff_admin.activities.RuleManagementActivity;
import com.example.coffee_shop_staff_admin.databinding.FragmentManageUserAdminBinding;

public class ManageUserAdminFragment extends Fragment {
    private FragmentManageUserAdminBinding fragmentManageUserAdminBinding;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Toolbar toolbar = requireActivity().findViewById(R.id.my_toolbar);
        toolbar.setTitle("Quản lý người dùng");

        fragmentManageUserAdminBinding = FragmentManageUserAdminBinding.inflate(inflater, container, false);

        return fragmentManageUserAdminBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fragmentManageUserAdminBinding.ruleCardView.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), RuleManagementActivity.class);
            startActivity(intent);
        });

        fragmentManageUserAdminBinding.addUserCardView.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AddNewUserActivity.class);
            startActivity(intent);
        });

        //fragmentManageUserAdminBinding.notificationCardView.setOnClickListener(v -> Toast.makeText(getContext(), "Nav to notification", Toast.LENGTH_SHORT).show());
    }
}