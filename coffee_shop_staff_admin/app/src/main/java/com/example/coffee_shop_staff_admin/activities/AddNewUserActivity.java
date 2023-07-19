package com.example.coffee_shop_staff_admin.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import com.example.coffee_shop_staff_admin.R;
import com.example.coffee_shop_staff_admin.databinding.ActivityAddNewUserBinding;
import com.example.coffee_shop_staff_admin.utils.ValidateHelper;
import com.example.coffee_shop_staff_admin.utils.keyboard.KeyboardHelper;
import com.example.coffee_shop_staff_admin.viewmodels.AddNewUserViewModel;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddNewUserActivity extends AppCompatActivity {
    private ActivityAddNewUserBinding activityAddNewUserBinding;
    private final AddNewUserViewModel addNewUserViewModel = new AddNewUserViewModel();
    private AsyncTask<Void, Void, Void> addNewUserTask;
    private final OkHttpClient client =
            new OkHttpClient()
                    .newBuilder()
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();

    private final MediaType mediaType = MediaType.parse("application/json");
    private final ActivityResultLauncher<Intent> activityChooseStoreResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if(data!=null)
                    {
                        addNewUserViewModel.setStoreName(data.getStringExtra("storeName"));
                        addNewUserViewModel.setStoreID(data.getStringExtra("storeId"));
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityAddNewUserBinding = DataBindingUtil.setContentView(this,R.layout.activity_add_new_user);

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        toolbar.setTitle("Thêm người dùng");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        init();
    }
    private void init()
    {
        activityAddNewUserBinding.setViewModel(addNewUserViewModel);

        activityAddNewUserBinding.loading.setOnTouchListener((v, event) -> {
            //Prevent any action when loading is visible
            return true;
        });

        KeyboardHelper.setKeyboardVisibilityListener(this, addNewUserViewModel::setKeyBoardShow);

        activityAddNewUserBinding.storePickerButton.setOnClickListener(v -> {
            Intent intent = new Intent(AddNewUserActivity.this, ChooseStoreActivity.class);
            activityChooseStoreResultLauncher.launch(intent);
        });

        activityAddNewUserBinding.button.setOnClickListener(v -> {
            if(addNewUserTask!=null)
            {
                addNewUserTask.cancel(true);
            }
            addNewUserTask = new AddNewUserTask();
            addNewUserTask.execute();
        });
    }
    private final class AddNewUserTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            addNewUserViewModel.setLoading(true);
        }
        @Override
        protected Void doInBackground(Void... params) {
            boolean isValid = true;
            if(!ValidateHelper.validateText(addNewUserViewModel.getName()))
            {
                runOnUiThread(() -> activityAddNewUserBinding.nameEditTextFrame.setError("Vui lòng nhập tên"));
                isValid = false;
            }
            else
            {
                runOnUiThread(() -> activityAddNewUserBinding.nameEditTextFrame.setError(null));
            }
            if(!ValidateHelper.validateEmail(addNewUserViewModel.getEmail()))
            {
                runOnUiThread(() -> activityAddNewUserBinding.emailEditTextFrame.setError("Vui lòng nhập email"));
                isValid = false;
            }
            else
            {
                runOnUiThread(() -> activityAddNewUserBinding.emailEditTextFrame.setError(null));
            }
            if(addNewUserViewModel.isAddStaff())
            {
                if(!ValidateHelper.validateText(addNewUserViewModel.getStoreName()))
                {
                    runOnUiThread(() -> activityAddNewUserBinding.storeEditTextFrame.setError("Vui lòng chọn cửa hàng"));
                    isValid = false;
                }
                else
                {
                    runOnUiThread(() -> activityAddNewUserBinding.storeEditTextFrame.setError(null));
                }
            }
            if(!isValid)
            {
                addNewUserViewModel.setLoading(false);
                return null;
            }

            String requestBody =
                    "{\"email\":\"" +
                            addNewUserViewModel.getEmail() +
                            "\",\"name\":\"" +
                            addNewUserViewModel.getName() +
                            "\",\"role\":\"" +
                            (addNewUserViewModel.isAddStaff()?"staff":"admin") +
                            "\",\"store\":\"" +
                            (addNewUserViewModel.isAddStaff()?addNewUserViewModel.getStoreID():"") +
                            "\"}";
            RequestBody body = RequestBody.create(requestBody, mediaType);
            Request request =
                    new Request.Builder()
                            .url("http://103.166.182.58/users")
                            .post(body)
                            .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    addNewUserViewModel.setLoading(false);
                    runOnUiThread(() -> Toast.makeText(
                            AddNewUserActivity.this,
                            "Đã có lỗi xảy ra. Xin hãy thử lại sau.",
                            Toast.LENGTH_SHORT
                    ).show());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    String responseBody = response.body().string();
                    Gson gson = new Gson();
                    JsonObject jsonObject = gson.fromJson(responseBody, JsonObject.class);
                    if (response.code() == 201) {
                        String password = jsonObject.get("password").getAsString();
                        runOnUiThread(() ->{
                            AlertDialog alertDialog = new AlertDialog.Builder(AddNewUserActivity.this)
                                    .setTitle("Mật khẩu")
                                    .setMessage("Mật khẩu của bạn là: " + password)
                                    .setPositiveButton("COPY TO CLIPBOARD", (dialog, which) -> {
                                        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                        ClipData clip = ClipData.newPlainText("password", password);
                                        clipboard.setPrimaryClip(clip);
                                        finish();
                                    })
                                    .setNegativeButton("OK", (dialog, which) -> finish()) .create();
                            addNewUserViewModel.setLoading(false);
                            alertDialog.show();
                        });
                    } else if(response.code() == 400) {
                        if(jsonObject.get("message") == null)
                        {
                            addNewUserViewModel.setLoading(false);
                            runOnUiThread(() -> Toast.makeText(
                                    AddNewUserActivity.this,
                                    "Email đã tồn tại.",
                                    Toast.LENGTH_SHORT
                            ).show());
                        }
                        else {
                            addNewUserViewModel.setLoading(false);
                            runOnUiThread(() -> Toast.makeText(
                                    AddNewUserActivity.this,
                                    "Cần chọn cửa hàng cho nhân viên.",
                                    Toast.LENGTH_SHORT
                            ).show());
                        }
                    }
                    response.close();
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {

        }
    }
}