package com.example.coffee_shop_staff_admin.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.coffee_shop_staff_admin.R;
import com.example.coffee_shop_staff_admin.adapters.FoodAdminSizeAdapter;
import com.example.coffee_shop_staff_admin.adapters.FoodAdminToppingAdapter;
import com.example.coffee_shop_staff_admin.adapters.ImageEditAdapter;
import com.example.coffee_shop_staff_admin.databinding.ActivityFoodAdminEditBinding;
import com.example.coffee_shop_staff_admin.models.Food;
import com.example.coffee_shop_staff_admin.models.Size;
import com.example.coffee_shop_staff_admin.models.Topping;
import com.example.coffee_shop_staff_admin.repositories.FoodRepository;
import com.example.coffee_shop_staff_admin.repositories.SizeRepository;
import com.example.coffee_shop_staff_admin.repositories.ToppingRepository;
import com.example.coffee_shop_staff_admin.utils.ValidateHelper;
import com.example.coffee_shop_staff_admin.utils.interfaces.OnFoodAdminSizeCheckedChangedListener;
import com.example.coffee_shop_staff_admin.utils.interfaces.OnFoodAdminToppingCheckedChangedListener;
import com.example.coffee_shop_staff_admin.utils.keyboard.KeyboardHelper;
import com.example.coffee_shop_staff_admin.viewmodels.FoodAdminEditViewModel;

import java.util.ArrayList;
import java.util.List;

public class FoodAdminEditActivity extends AppCompatActivity {
    private final String TAG = "FoodAdminEditActivity";
    private ActivityFoodAdminEditBinding activityFoodAdminEditBinding;
    private final FoodAdminEditViewModel foodAdminEditViewModel = new FoodAdminEditViewModel();
    private String foodId = null;
    private Toolbar toolbar;

    private final List<String> toppingIds = new ArrayList<>();
    private final List<String> sizeIds = new ArrayList<>();
    private AsyncTask<Void, Void, Void> updateFoodTask;
    private final FoodAdminToppingAdapter foodAdminToppingAdapter = new FoodAdminToppingAdapter();
    private final FoodAdminSizeAdapter foodAdminSizeAdapter = new FoodAdminSizeAdapter();
    private ImageEditAdapter imageEditAdapter;

    private final OnFoodAdminToppingCheckedChangedListener toppingCheckedListener = (toppingId, isChecked) -> {
        if(!isChecked)
        {
            toppingIds.remove(toppingId);
        }
        else
        {
            toppingIds.add(toppingId);
        }
    };

    private final OnFoodAdminSizeCheckedChangedListener sizeCheckedListener = (sizeId, isChecked) -> {
        if(!isChecked)
        {
            sizeIds.remove(sizeId);
        }
        else
        {
            sizeIds.add(sizeId);
        }
    };
    private final ActivityResultLauncher<Intent> chooseImageResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null && data.getData() != null) {
                        imageEditAdapter.insert(data.getData().toString());
                    }
                }
            });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityFoodAdminEditBinding = DataBindingUtil.setContentView(this,R.layout.activity_food_admin_edit);

        toolbar = findViewById(R.id.my_toolbar);
        toolbar.setTitle("Tạo đồ uống");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        Intent intent = getIntent();
        Food food = null;
        foodId = intent.getStringExtra("foodId");
        if(foodId != null)
        {
            String name = intent.getStringExtra("name");
            String description = intent.getStringExtra("description");
            double price = intent.getDoubleExtra("price", 0);
            List<String> images = intent.getStringArrayListExtra("images");
            List<String> sizes = intent.getStringArrayListExtra("sizes");
            List<String> toppings = intent.getStringArrayListExtra("toppings");

            food = new Food(
                    foodId,
                    name,
                    price,
                    description,
                    images,
                    sizes,
                    toppings
            );
        }
        init(food);
    }

    Observer<List<Topping>> toppingObserver = new Observer<List<Topping>>() {
        @Override
        public void onChanged(@Nullable List<Topping> value) {
            foodAdminEditViewModel.setLoadingTopping(true);
            if(value != null)
            {
                foodAdminToppingAdapter.changeDataSet(value);
                foodAdminEditViewModel.setLoadingTopping(false);
                ToppingRepository.getInstance().getToppingListMutableLiveData().removeObserver(toppingObserver);
            }
        }
    };

    Observer<List<Size>> sizeObserver = new Observer<List<Size>>() {
        @Override
        public void onChanged(@Nullable List<Size> value) {
            foodAdminEditViewModel.setLoadingSize(true);
            if(value != null)
            {
                foodAdminSizeAdapter.changeDataSet(value);
                foodAdminEditViewModel.setLoadingSize(false);
                SizeRepository.getInstance().getSizeListMutableLiveData().removeObserver(sizeObserver);
            }
        }
    };

    void init(Food food){
        if(food != null)
        {
            foodAdminEditViewModel.updateParameter(food);
            foodAdminToppingAdapter.changeAvailableToppings(food.getToppings());
            foodAdminSizeAdapter.changeAvailableSizes(food.getSizes());
            imageEditAdapter = new ImageEditAdapter(food.getImages());
            toolbar.setTitle("Thay đổi cửa hàng");
        }
        else
        {
            imageEditAdapter = new ImageEditAdapter(new ArrayList<>());
        }
        ToppingRepository.getInstance().getToppingListMutableLiveData().observe(this, toppingObserver);
        SizeRepository.getInstance().getSizeListMutableLiveData().observe(this, sizeObserver);

        foodAdminToppingAdapter.setOnFoodAdminToppingCheckedChangedListener(toppingCheckedListener);
        activityFoodAdminEditBinding.toppingRecycler.setLayoutManager(new LinearLayoutManager(this));
        activityFoodAdminEditBinding.toppingRecycler.setAdapter(foodAdminToppingAdapter);

        foodAdminSizeAdapter.setOnFoodAdminSizeCheckedChangedListener(sizeCheckedListener);
        activityFoodAdminEditBinding.sizeRecycler.setLayoutManager(new LinearLayoutManager(this));
        activityFoodAdminEditBinding.sizeRecycler.setAdapter(foodAdminSizeAdapter);

        activityFoodAdminEditBinding.imageRecycler.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
        activityFoodAdminEditBinding.imageRecycler.setAdapter(imageEditAdapter);

        activityFoodAdminEditBinding.setViewModel(foodAdminEditViewModel);

        activityFoodAdminEditBinding.loading.setOnTouchListener((v, event) -> {
            //Prevent any action when loading is visible
            return true;
        });

        KeyboardHelper.setKeyboardVisibilityListener(this, foodAdminEditViewModel::setKeyBoardShow);

        activityFoodAdminEditBinding.addImageButton.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);

            chooseImageResultLauncher.launch(intent);
        });

        activityFoodAdminEditBinding.button.setOnClickListener(v -> {
            if(updateFoodTask!=null)
            {
                updateFoodTask.cancel(true);
            }
            updateFoodTask = new UpdateFoodTask();
            updateFoodTask.execute();
        });
    }
    private final class UpdateFoodTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            foodAdminEditViewModel.setUpdating(true);
        }
        @Override
        protected Void doInBackground(Void... params) {
            boolean isValid = true;
            if(!ValidateHelper.validateText(foodAdminEditViewModel.getName()))
            {
                runOnUiThread(() -> activityFoodAdminEditBinding.nameEditTextFrame.setError("Vui lòng nhập tên"));
                isValid = false;
            }
            else
            {
                runOnUiThread(() -> activityFoodAdminEditBinding.nameEditTextFrame.setError(null));
            }
            if(!ValidateHelper.validateDouble(foodAdminEditViewModel.getPrice()))
            {
                runOnUiThread(() -> activityFoodAdminEditBinding.priceEditTextFrame.setError("Vui lòng nhập số"));
                isValid = false;
            }
            else
            {
                runOnUiThread(() -> activityFoodAdminEditBinding.priceEditTextFrame.setError(null));
            }
            if(!isValid)
            {
                foodAdminEditViewModel.setUpdating(false);
                return null;
            }

            if(sizeIds.size() == 0)
            {
                foodAdminEditViewModel.setUpdating(false);
                runOnUiThread(() -> Toast.makeText(
                        FoodAdminEditActivity.this,
                        "Xin hãy chọn ít nhất 1 size.",
                        Toast.LENGTH_SHORT
                ).show());
                return null;
            }

            //Check image
            if(imageEditAdapter.getImages().size() == 0)
            {
                foodAdminEditViewModel.setUpdating(false);
                runOnUiThread(() -> Toast.makeText(
                        FoodAdminEditActivity.this,
                        "Chưa chọn hình ảnh.",
                        Toast.LENGTH_SHORT
                ).show());
                return null;
            }
            if(foodId != null)
            {
                Food food = new Food(
                        foodId,
                        foodAdminEditViewModel.getName(),
                        Double.parseDouble(foodAdminEditViewModel.getPrice()),
                        foodAdminEditViewModel.getDescription(),
                        imageEditAdapter.getImages(),
                        sizeIds,
                        toppingIds
                );
                FoodRepository.getInstance().updateFood(food, success -> {
                    if(success)
                    {
                        Log.e(TAG, "update food successfully.");
                        foodAdminEditViewModel.setUpdating(false);
                        runOnUiThread(() -> Toast.makeText(
                                FoodAdminEditActivity.this,
                                "Đã chỉnh đồ uống thành công.",
                                Toast.LENGTH_SHORT
                        ).show());
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                    else
                    {
                        Log.e(TAG, "update food failed.");
                        foodAdminEditViewModel.setUpdating(false);
                        runOnUiThread(() -> Toast.makeText(
                                FoodAdminEditActivity.this,
                                "Đã có lỗi xảy ra. Xin hãy thử lại sau.",
                                Toast.LENGTH_SHORT
                        ).show());
                    }
                });
            }
            else
            {
                Food food = new Food(
                        null,
                        foodAdminEditViewModel.getName(),
                        Double.parseDouble(foodAdminEditViewModel.getPrice()),
                        foodAdminEditViewModel.getDescription(),
                        imageEditAdapter.getImages(),
                        sizeIds,
                        toppingIds
                );
                FoodRepository.getInstance().insertFood(food, success -> {
                    if(success)
                    {
                        Log.e(TAG, "insert food successfully.");
                        foodAdminEditViewModel.setUpdating(false);
                        runOnUiThread(() -> Toast.makeText(
                                FoodAdminEditActivity.this,
                                "Đã thêm đồ uống thành công.",
                                Toast.LENGTH_SHORT
                        ).show());
                        finish();
                    }
                    else
                    {
                        Log.e(TAG, "insert food failed.");
                        foodAdminEditViewModel.setUpdating(false);
                        runOnUiThread(() -> Toast.makeText(
                                FoodAdminEditActivity.this,
                                "Đã có lỗi xảy ra. Xin hãy thử lại sau.",
                                Toast.LENGTH_SHORT
                        ).show());
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {

        }
    }
}