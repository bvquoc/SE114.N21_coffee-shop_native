package com.example.coffee_shop_staff_admin.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.coffee_shop_staff_admin.R;
import com.example.coffee_shop_staff_admin.adapters.FoodAdminSizeAdapter;
import com.example.coffee_shop_staff_admin.adapters.FoodAdminToppingAdapter;
import com.example.coffee_shop_staff_admin.adapters.ImageViewPagerAdapter;
import com.example.coffee_shop_staff_admin.databinding.ActivityFoodAdminDetailBinding;
import com.example.coffee_shop_staff_admin.models.Food;
import com.example.coffee_shop_staff_admin.models.Size;
import com.example.coffee_shop_staff_admin.models.Topping;
import com.example.coffee_shop_staff_admin.repositories.FoodRepository;
import com.example.coffee_shop_staff_admin.repositories.SizeRepository;
import com.example.coffee_shop_staff_admin.repositories.ToppingRepository;
import com.example.coffee_shop_staff_admin.utils.interfaces.OnFoodAdminSizeCheckedChangedListener;
import com.example.coffee_shop_staff_admin.utils.interfaces.OnFoodAdminToppingCheckedChangedListener;
import com.example.coffee_shop_staff_admin.viewmodels.FoodAdminDetailViewModel;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class FoodAdminDetailActivity extends AppCompatActivity {
    private final String TAG = "FoodAdminDetailActivity";
    private ActivityFoodAdminDetailBinding activityFoodAdminDetailBinding;
    private final FoodAdminDetailViewModel foodAdminDetailViewModel = new FoodAdminDetailViewModel();
    private String foodId;
    private final List<String> toppingIds = new ArrayList<>();
    private final List<String> sizeIds = new ArrayList<>();
    private  Food selectedFood;
    private AsyncTask<Void, Void, Void> updateToppingSizeOfFoodTask;
    private AsyncTask<Void, Void, Void> deleteFoodTask;
    private final FoodAdminSizeAdapter foodAdminSizeAdapter = new FoodAdminSizeAdapter();
    private final FoodAdminToppingAdapter foodAdminToppingAdapter = new FoodAdminToppingAdapter();

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
    private final ActivityResultLauncher<Intent> activityEditFoodResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    finish();
                } else {
                    //User do nothing
                }
            }
    );
    Observer<List<Topping>> toppingObserver = new Observer<List<Topping>>() {
        @Override
        public void onChanged(@Nullable List<Topping> value) {
            foodAdminDetailViewModel.setLoadingTopping(true);
            if(value != null)
            {
                foodAdminToppingAdapter.changeDataSet(value);
                foodAdminDetailViewModel.setLoadingTopping(false);
                ToppingRepository.getInstance().getToppingListMutableLiveData().removeObserver(toppingObserver);
            }
        }
    };

    Observer<List<Size>> sizeObserver = new Observer<List<Size>>() {
        @Override
        public void onChanged(@Nullable List<Size> value) {
            foodAdminDetailViewModel.setLoadingSize(true);
            if(value != null)
            {
                foodAdminSizeAdapter.changeDataSet(value);
                foodAdminDetailViewModel.setLoadingSize(false);
                SizeRepository.getInstance().getSizeListMutableLiveData().removeObserver(sizeObserver);
            }
        }
    };

    Observer<List<Food>> foodObserver = new Observer<List<Food>>() {
        @Override
        public void onChanged(@Nullable List<Food> value) {
            foodAdminDetailViewModel.setLoadingFood(true);
            if(value != null)
            {
                selectedFood = null;
                for (Food food : value) {
                    if(food.getId().equals(foodId))
                    {
                        selectedFood = food;
                        break;
                    }
                }
                if(selectedFood != null)
                {
                    foodAdminSizeAdapter.changeAvailableSizes(selectedFood.getSizes());
                    foodAdminToppingAdapter.changeAvailableToppings(selectedFood.getToppings());

                    foodAdminDetailViewModel.setIndex(1);
                    foodAdminDetailViewModel.setImageCount(selectedFood.getImages().size());
                    activityFoodAdminDetailBinding.viewpagerImage.setAdapter(
                            new ImageViewPagerAdapter(selectedFood.getImages())
                    );
                    foodAdminDetailViewModel.setName(selectedFood.getName());

                    DecimalFormat formatter = new DecimalFormat("#,##0.##");
                    String formattedPrice = formatter.format(selectedFood.getPrice());
                    foodAdminDetailViewModel.setPrice(formattedPrice+"đ");

                    foodAdminDetailViewModel.setDescription(selectedFood.getDescription());

                    foodAdminDetailViewModel.setLoadingFood(false);
                }
                FoodRepository.getInstance().getFoodListMutableLiveData().removeObserver(foodObserver);
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityFoodAdminDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_food_admin_detail);

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        toolbar.setTitle("Chi tiết đồ uống");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        Intent intent = getIntent();
        foodId = intent.getStringExtra("foodId");
        init();
    }
    private void init()
    {
        FoodRepository.getInstance().getFoodListMutableLiveData().observe(this, foodObserver);
        SizeRepository.getInstance().getSizeListMutableLiveData().observe(this, sizeObserver);
        ToppingRepository.getInstance().getToppingListMutableLiveData().observe(this, toppingObserver);

        foodAdminSizeAdapter.setOnFoodAdminSizeCheckedChangedListener(sizeCheckedListener);
        foodAdminToppingAdapter.setOnFoodAdminToppingCheckedChangedListener(toppingCheckedListener);

        activityFoodAdminDetailBinding.setViewModel(foodAdminDetailViewModel);

        activityFoodAdminDetailBinding.viewpagerImage.setOffscreenPageLimit(1);
        activityFoodAdminDetailBinding.viewpagerImage.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                foodAdminDetailViewModel.setIndex(position + 1);
            }
        });

        activityFoodAdminDetailBinding.recyclerSize.setLayoutManager(new LinearLayoutManager(this));
        activityFoodAdminDetailBinding.recyclerSize.setAdapter(foodAdminSizeAdapter);

        activityFoodAdminDetailBinding.recyclerTopping.setLayoutManager(new LinearLayoutManager(this));
        activityFoodAdminDetailBinding.recyclerTopping.setAdapter(foodAdminToppingAdapter);

        activityFoodAdminDetailBinding.saveButton.setOnClickListener(v -> {
            if(updateToppingSizeOfFoodTask!=null)
            {
                updateToppingSizeOfFoodTask.cancel(true);
            }
            updateToppingSizeOfFoodTask = new UpdateToppingSizeOfFoodTask();
            updateToppingSizeOfFoodTask.execute();
        });

        activityFoodAdminDetailBinding.editButton.setOnClickListener(v -> {
            if(selectedFood!=null)
            {
                Intent intent = new Intent(FoodAdminDetailActivity.this, FoodAdminEditActivity.class);
                intent.putExtra("foodId", selectedFood.getId());
                intent.putExtra("name", selectedFood.getName());
                intent.putExtra("description", selectedFood.getDescription());
                intent.putExtra("price", selectedFood.getPrice());
                intent.putStringArrayListExtra("images",  new ArrayList<>(selectedFood.getImages()));
                intent.putStringArrayListExtra("sizes",  new ArrayList<>(selectedFood.getSizes()));
                intent.putStringArrayListExtra("toppings",  new ArrayList<>(selectedFood.getToppings()));
                activityEditFoodResultLauncher.launch(intent);
            }
        });

        activityFoodAdminDetailBinding.deleteButton.setOnClickListener(v -> {
            if(deleteFoodTask!=null)
            {
                deleteFoodTask.cancel(true);
            }
            deleteFoodTask = new DeleteFoodTask();
            deleteFoodTask.execute();
        });
    }
    private final class DeleteFoodTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            foodAdminDetailViewModel.setUpdating(true);
        }
        @Override
        protected Void doInBackground(Void... params) {
            FoodRepository.getInstance().deleteFood(foodId, success -> {
                if(success)
                {
                    Log.e(TAG, "delete food successfully.");
                    foodAdminDetailViewModel.setUpdating(false);
                    runOnUiThread(() -> Toast.makeText(
                            FoodAdminDetailActivity.this,
                            "Đã xóa đồ uống thành công.",
                            Toast.LENGTH_SHORT
                    ).show());
                    finish();
                }
                else
                {
                    Log.e(TAG, "delete food failed.");
                    foodAdminDetailViewModel.setUpdating(false);
                    runOnUiThread(() -> Toast.makeText(
                            FoodAdminDetailActivity.this,
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
    private final class UpdateToppingSizeOfFoodTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            foodAdminDetailViewModel.setUpdating(true);
        }
        @Override
        protected Void doInBackground(Void... params) {
            if(sizeIds.size() == 0)
            {
                foodAdminDetailViewModel.setUpdating(false);
                runOnUiThread(() -> Toast.makeText(
                        FoodAdminDetailActivity.this,
                        "Xin hãy chọn ít nhất 1 size.",
                        Toast.LENGTH_SHORT
                ).show());
                return null;
            }
            FoodRepository.getInstance().updateToppingSizeOfFood(foodId, toppingIds, sizeIds, success -> {
                if(success)
                {
                    Log.e(TAG, "update size, topping of food successfully.");
                    foodAdminDetailViewModel.setUpdating(false);
                    runOnUiThread(() -> Toast.makeText(
                            FoodAdminDetailActivity.this,
                            "Đã chỉnh size và topping thành công.",
                            Toast.LENGTH_SHORT
                    ).show());
                    finish();
                }
                else
                {
                    Log.e(TAG, "update size, topping of food failed.");
                    foodAdminDetailViewModel.setUpdating(false);
                    runOnUiThread(() -> Toast.makeText(
                            FoodAdminDetailActivity.this,
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