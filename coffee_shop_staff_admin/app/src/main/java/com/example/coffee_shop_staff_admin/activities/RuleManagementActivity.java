package com.example.coffee_shop_staff_admin.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.coffee_shop_staff_admin.R;
import com.example.coffee_shop_staff_admin.databinding.ActivityRuleManagementBinding;
import com.example.coffee_shop_staff_admin.models.User;
import com.example.coffee_shop_staff_admin.repositories.UserRepository;
import com.example.coffee_shop_staff_admin.viewmodels.ChooseStoreViewModel;
import com.example.coffee_shop_staff_admin.viewmodels.RuleManagementViewModel;

public class RuleManagementActivity extends AppCompatActivity {
    private final String TAG = "RuleManagementActivity";
    private ActivityRuleManagementBinding activityRuleManagementBinding;
    private final RuleManagementViewModel ruleManagementViewModel = new RuleManagementViewModel();
    private AsyncTask<Void, Void, Void> searchTask;

    private AsyncTask<Void, Void, Void> updateTask;

    private User selectedUser = null;
    private final ActivityResultLauncher<Intent> activitychooseStoreResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        if(updateTask!=null)
                        {
                            updateTask.cancel(true);
                        }
                        updateTask = new UpdateTask(UpdateUserType.staff, !ruleManagementViewModel.isStaff(), data.getStringExtra("storeId"));
                        updateTask.execute();
                    }
                }
            }
    );
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityRuleManagementBinding = DataBindingUtil.setContentView(this, R.layout.activity_rule_management);

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        toolbar.setTitle("Quyền truy cập");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        init();
    }
    private void init()
    {
        activityRuleManagementBinding.setViewModel(ruleManagementViewModel);

        activityRuleManagementBinding.loading.setOnTouchListener((v, event) -> true);

        activityRuleManagementBinding.searchButton.setOnClickListener(v -> {
            if(searchTask!=null)
            {
                searchTask.cancel(true);
            }
            searchTask = new SearchTask();
            searchTask.execute();
        });

        activityRuleManagementBinding.normalButton.setOnClickListener(v -> {
            if(updateTask!=null)
            {
                updateTask.cancel(true);
            }
            updateTask = new UpdateTask(UpdateUserType.active, !ruleManagementViewModel.isActive());
            updateTask.execute();
        });

        activityRuleManagementBinding.staffButton.setOnClickListener(v -> {
            if(ruleManagementViewModel.isStaff())
            {
                if(updateTask!=null)
                {
                    updateTask.cancel(true);
                }
                updateTask = new UpdateTask(UpdateUserType.staff, !ruleManagementViewModel.isStaff());
                updateTask.execute();
            }
            else
            {
                Intent intent = new Intent(RuleManagementActivity.this, ChooseStoreActivity.class);
                activitychooseStoreResultLauncher.launch(intent);
            }
        });

        activityRuleManagementBinding.adminButton.setOnClickListener(v -> {
            if(updateTask!=null)
            {
                updateTask.cancel(true);
            }
            updateTask = new UpdateTask(UpdateUserType.admin, !ruleManagementViewModel.isAdmin());
            updateTask.execute();
        });
    }

    private final class SearchTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            ruleManagementViewModel.setLoading(true);
        }
        @Override
        protected Void doInBackground(Void... params) {
            UserRepository.getInstance().getUserByEmailFromFireStore(ruleManagementViewModel.getEmail(), user -> {
                if(user != null)
                {
                    Log.e(TAG, "find user success.");
                    selectedUser = user;
                    ruleManagementViewModel.setName(user.getName());
                    ruleManagementViewModel.setEmail(user.getEmail());

                    ruleManagementViewModel.setActive(user.isActive());
                    ruleManagementViewModel.setStaff(user.isStaff());
                    ruleManagementViewModel.setAdmin(user.isAdmin());

                    Glide.with(getApplicationContext())
                            .load(user.getAvatarUrl())
                            .into(activityRuleManagementBinding.imageView);

                    ruleManagementViewModel.setCanFind(true);
                    ruleManagementViewModel.setLoading(false);
                }
                else
                {
                    Log.e(TAG, "find user failed.");
                    selectedUser = null;
                    ruleManagementViewModel.setCanFind(false);
                    ruleManagementViewModel.setLoading(false);
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
        }
    }

    private final class UpdateTask extends AsyncTask<Void, Void, Void> {
        private final UpdateUserType type;
        private final boolean value;
        private String store = "";
        public UpdateTask(UpdateUserType type, boolean value)
        {
            this.type = type;
            this.value = value;
        }
        public UpdateTask(UpdateUserType type, boolean value, String store)
        {
            this.type = type;
            this.value = value;
            this.store = store;
        }
        @Override
        protected void onPreExecute() {
            ruleManagementViewModel.setLoading(true);
        }
        @Override
        protected Void doInBackground(Void... params) {
            if(type == UpdateUserType.active)
            {
                UserRepository.getInstance().updateUserActiveAccess(selectedUser.getId(), value, (success, message) -> {
                    if(success)
                    {
                        Log.e(TAG, "set user's active access success.");
                        ruleManagementViewModel.setActive(value);
                        ruleManagementViewModel.setLoading(false);
                        Toast.makeText(
                                RuleManagementActivity.this,
                                "Đã chỉnh quyền thành công.",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                    else
                    {
                        Log.e(TAG, "set user's active access failed.");
                        ruleManagementViewModel.setLoading(false);
                        Toast.makeText(
                                RuleManagementActivity.this,
                                "Đã có lỗi xãy ra, xin hãy thử lại sau.",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
            }
            else if(type == UpdateUserType.staff)
            {
                UserRepository.getInstance().updateUserStaffAccess(selectedUser.getId(), value, store, (success, message) -> {
                    if(success)
                    {
                        Log.e(TAG, "set user's staff access success.");
                        ruleManagementViewModel.setStaff(value);
                        ruleManagementViewModel.setLoading(false);
                        Toast.makeText(
                                RuleManagementActivity.this,
                                "Đã chỉnh quyền thành công.",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                    else
                    {
                        Log.e(TAG, "set user's staff access failed.");
                        ruleManagementViewModel.setLoading(false);
                        Toast.makeText(
                                RuleManagementActivity.this,
                                "Đã có lỗi xãy ra, xin hãy thử lại sau.",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
            }
            else
            {
                UserRepository.getInstance().updateUserAdminAccess(selectedUser.getId(), value, (success, message) -> {
                    if(success)
                    {
                        Log.e(TAG, "set user's admin access success.");
                        ruleManagementViewModel.setAdmin(value);
                        ruleManagementViewModel.setLoading(false);
                        Toast.makeText(
                                RuleManagementActivity.this,
                                "Đã chỉnh quyền thành công.",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                    else
                    {
                        Log.e(TAG, "set user's admin access failed.");
                        ruleManagementViewModel.setLoading(false);
                        Toast.makeText(
                                RuleManagementActivity.this,
                                "Đã có lỗi xãy ra, xin hãy thử lại sau.",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
        }
    }

    private enum UpdateUserType{
        active,
        staff,
        admin
    }
}