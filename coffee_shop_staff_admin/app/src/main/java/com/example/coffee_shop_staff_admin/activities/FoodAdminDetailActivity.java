package com.example.coffee_shop_staff_admin.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

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
import com.example.coffee_shop_staff_admin.utils.interfaces.UpdateDataListener;
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
    private AsyncTask<Void, Void, Void> updateToppingSizeOfFoodTask;

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
        init(foodId);
    }
    private void init(String foodId)
    {
        List<Food> foods = FoodRepository.getInstance().getFoodListMutableLiveData().getValue();
        if(foods == null)
        {
            activityFoodAdminDetailBinding.setViewModel(foodAdminDetailViewModel);
            return;
        }
        Food selectedFood = null;
        for (Food food : foods) {
            if(food.getId().equals(foodId))
            {
                selectedFood = food;
                break;
            }
        }
        if(selectedFood == null)
        {
            activityFoodAdminDetailBinding.setViewModel(foodAdminDetailViewModel);
            return;
        }

        List<Size> sizes = SizeRepository.getInstance().getSizeListMutableLiveData().getValue();
        if(sizes == null)
        {
            activityFoodAdminDetailBinding.setViewModel(foodAdminDetailViewModel);
            return;
        }
        FoodAdminSizeAdapter foodAdminSizeAdapter = new FoodAdminSizeAdapter(sizes,selectedFood.getSizes());
        foodAdminSizeAdapter.setOnFoodAdminSizeCheckedChangedListener(sizeCheckedListener);

        List<Topping> toppings = ToppingRepository.getInstance().getToppingListMutableLiveData().getValue();
        if(toppings == null)
        {
            activityFoodAdminDetailBinding.setViewModel(foodAdminDetailViewModel);
            return;
        }
        FoodAdminToppingAdapter foodAdminToppingAdapter = new FoodAdminToppingAdapter(toppings, selectedFood.getToppings());
        foodAdminToppingAdapter.setOnFoodAdminToppingCheckedChangedListener(toppingCheckedListener);

        foodAdminDetailViewModel.setIndex(1);
        foodAdminDetailViewModel.setImageCount(selectedFood.getImages().size());
        activityFoodAdminDetailBinding.viewpagerImage.setAdapter(
                new ImageViewPagerAdapter(selectedFood.getImages())
        );
        foodAdminDetailViewModel.setName(selectedFood.getName());

        DecimalFormat formatter = new DecimalFormat("###0.##");
        String formattedPrice = formatter.format(selectedFood.getPrice());
        foodAdminDetailViewModel.setPrice(formattedPrice);

        foodAdminDetailViewModel.setDescription(selectedFood.getDescription());

        foodAdminDetailViewModel.setLoading(false);

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
                    foodAdminDetailViewModel.setLoading(false);
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