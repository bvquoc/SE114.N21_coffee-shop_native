package com.example.coffee_shop_staff_admin.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.coffee_shop_staff_admin.R;
import com.example.coffee_shop_staff_admin.adapters.product.StaffSizeAdapter;
import com.example.coffee_shop_staff_admin.models.Food;
import com.example.coffee_shop_staff_admin.models.FoodChecker;
import com.example.coffee_shop_staff_admin.models.Size;
import com.example.coffee_shop_staff_admin.models.StoreProduct;
import com.example.coffee_shop_staff_admin.repositories.SizeRepository;
import com.example.coffee_shop_staff_admin.viewmodels.product.StaffProductDetailViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StaffProductDetailActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private StaffSizeAdapter adapter;
    private FoodChecker currentProduct;
    private Button saveButton;

StaffProductDetailViewModel viewModel;
    private List<Size> storeSize;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_product_detail);

        recyclerView = findViewById(R.id.recyclerSizeProductStaff);
        saveButton = findViewById(R.id.btn_save_product_staff);

        storeSize = SizeRepository.getInstance().getSizeListMutableLiveData().getValue();

        currentProduct = (FoodChecker) getIntent().getSerializableExtra("product");

        viewModel = new StaffProductDetailViewModel(currentProduct);

        List<String> sizeRaw = ((Food) currentProduct.getProduct()).getSizes();

        List<Size> sizes = new ArrayList<>();
        for(String item : sizeRaw){
            sizes.add(findSize(item));
        }
        //Set adapter
        adapter = new StaffSizeAdapter(currentProduct);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(adapter);

        setImage();
        setAppBar();

        saveButton.setOnClickListener(v -> {
            Map<String, Boolean> stateFood = adapter.getCurrentState();
            List<String> blockSize = new ArrayList<>();
            for(Map.Entry<String, Boolean> item : stateFood.entrySet()){
                if(!item.getValue()) {
                    blockSize.add(item.getKey());
                }
            }
            currentProduct.setBlockSize(blockSize);

            viewModel.onUpdateProduct(currentProduct);
        });
    }

    private void setImage(){
        List<SlideModel> slideModelList = new ArrayList<>();
        Food product = (Food) currentProduct.getProduct();
        List<String> images = product.getImages();
        for(String item : images){
            slideModelList.add(new SlideModel(item, null, ScaleTypes.CENTER_CROP));
        }
        ImageSlider imageSlider = findViewById(R.id.image_slider);
        imageSlider.setImageList(slideModelList);
    }

    private Size findSize(String s){
        for(Size item : storeSize){
            if(item.getId().equals(s)){
                return item;
            }
        }
        return null;
    }

    private void setAppBar(){
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        toolbar.setTitle(((Food) currentProduct.getProduct()).getName());
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }
}