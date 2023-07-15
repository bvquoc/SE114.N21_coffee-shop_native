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
import android.text.InputType;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.coffee_shop_staff_admin.R;
import com.example.coffee_shop_staff_admin.databinding.ActivityToppingAdminDetailBinding;
import com.example.coffee_shop_staff_admin.fragments.ConfirmDialog;
import com.example.coffee_shop_staff_admin.models.Topping;
import com.example.coffee_shop_staff_admin.repositories.ToppingRepository;
import com.example.coffee_shop_staff_admin.viewmodels.ToppingAdminDetailViewModel;

import java.text.DecimalFormat;
public class ToppingAdminDetailActivity extends AppCompatActivity {
    private String TAG = "ToppingAdminDetailActivity";
    private ActivityToppingAdminDetailBinding activityToppingAdminDetailBinding;
    private String toppingId;
    private Topping selectedTopping;
    private AsyncTask<Void, Void, Void> deleteToppingTask;
    private final ToppingAdminDetailViewModel toppingAdminDetailViewModel = new ToppingAdminDetailViewModel();
    private final ActivityResultLauncher<Intent> activityEditToppingResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    finish();
                } else {
                    //User do nothing
                }
            }
    );
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityToppingAdminDetailBinding = DataBindingUtil.setContentView(this,R.layout.activity_topping_admin_detail);

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        toolbar.setTitle("Chi tiết topping");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        Intent intent = getIntent();
        toppingId = intent.getStringExtra("toppingId");

        init(toppingId);
    }
    void init(String toppingId){
        ToppingRepository.getInstance().getToppingListMutableLiveData().observe(this, toppings -> {
            toppingAdminDetailViewModel.setLoading(true);
            selectedTopping = null;
            for (Topping topping: toppings) {
                if(topping.getId().equals(toppingId))
                {
                    selectedTopping = topping;
                    break;
                }
            }
            if(selectedTopping != null)
            {
                toppingAdminDetailViewModel.setName(selectedTopping.getName());
                DecimalFormat formatter = new DecimalFormat("###0.##");
                String formattedPrice = formatter.format(selectedTopping.getPrice());
                toppingAdminDetailViewModel.setPrice(formattedPrice);

                Glide.with(getApplicationContext())
                        .load(selectedTopping.getImage())
                        .into(activityToppingAdminDetailBinding.imageView);

                toppingAdminDetailViewModel.setLoading(false);
            }
        });

        activityToppingAdminDetailBinding.setViewModel(toppingAdminDetailViewModel);

        activityToppingAdminDetailBinding.loading.setOnTouchListener((v, event) -> {
            //Prevent any action when loading is visible
            return true;
        });

        //Set the edittext can't be editable
        activityToppingAdminDetailBinding.nameEditText.setInputType(InputType.TYPE_NULL);
        activityToppingAdminDetailBinding.priceEditText.setInputType(InputType.TYPE_NULL);

        activityToppingAdminDetailBinding.editButton.setOnClickListener(v -> {
            Intent intent = new Intent(ToppingAdminDetailActivity.this, ToppingAdminEditActivity.class);
            intent.putExtra("toppingId", toppingId);
            intent.putExtra("toppingImage", selectedTopping.getImage().toString());
            intent.putExtra("toppingName", selectedTopping.getName());
            intent.putExtra("toppingPrice", selectedTopping.getPrice());
            activityEditToppingResultLauncher.launch(intent);
        });

        activityToppingAdminDetailBinding.deleteButton.setOnClickListener(v -> {
            ConfirmDialog dialog = new ConfirmDialog(
                    "Thông báo",
                    "Bạn có chắc muốn xóa topping này",
                    v1 -> {
                        if(deleteToppingTask!=null)
                        {
                            deleteToppingTask.cancel(true);
                        }
                        deleteToppingTask = new DeleteToppingTask();
                        deleteToppingTask.execute();
                    },
                    null
            );
            dialog.show(getSupportFragmentManager(), "confirmDialog");
        });
    }
    private final class DeleteToppingTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            toppingAdminDetailViewModel.setUpdating(true);
        }
        @Override
        protected Void doInBackground(Void... params) {
            ToppingRepository.getInstance().deleteTopping(toppingId, success -> {
                if(success)
                {
                    toppingAdminDetailViewModel.setUpdating(false);
                    Log.e(TAG, "delete topping successfully.");
                    runOnUiThread(() -> Toast.makeText(
                            ToppingAdminDetailActivity.this,
                            "Bạn đã xóa topping thành công",
                            Toast.LENGTH_SHORT
                    ).show());
                    finish();
                }
                else
                {
                    toppingAdminDetailViewModel.setUpdating(false);
                    Log.e(TAG, "delete topping failed.");
                    runOnUiThread(() -> Toast.makeText(
                            ToppingAdminDetailActivity.this,
                            "Đã có lỗi xảy ra. Xin hãy thử lại sau.",
                            Toast.LENGTH_SHORT
                    ).show());
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
        }
    }
}