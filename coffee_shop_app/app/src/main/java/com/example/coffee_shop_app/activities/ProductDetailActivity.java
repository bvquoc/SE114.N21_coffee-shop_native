package com.example.coffee_shop_app.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import com.example.coffee_shop_app.Data;
import com.example.coffee_shop_app.R;
import com.example.coffee_shop_app.adapters.ImageViewPagerAdapter;
import com.example.coffee_shop_app.adapters.SizeItemAdapter;
import com.example.coffee_shop_app.adapters.ToppingItemAdapter;
import com.example.coffee_shop_app.databinding.ActivityProductDetailBinding;
import com.example.coffee_shop_app.models.CartFood;
import com.example.coffee_shop_app.models.Product;
import com.example.coffee_shop_app.models.Size;
import com.example.coffee_shop_app.models.Topping;
import com.example.coffee_shop_app.utils.SqliteHelper;
import com.example.coffee_shop_app.utils.ItemClickedListener;
import com.example.coffee_shop_app.utils.keyboard.KeyboardHelper;
import com.example.coffee_shop_app.utils.keyboard.OnKeyboardVisibilityListener;
import com.example.coffee_shop_app.viewmodels.ProductDetailViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class ProductDetailActivity extends AppCompatActivity {
    private ActivityProductDetailBinding activityProductDetailBinding;
    private SizeItemAdapter sizeAdapter;
    private ToppingItemAdapter toppingItemAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityProductDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_product_detail);

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        init();
    }
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            activityProductDetailBinding.edtNote.clearFocus();
            return false;
        }
        return super.dispatchKeyEvent(event);
    }
    private void init() {
        activityProductDetailBinding.setIsLoading(true);

        List<Size> sizes = new ArrayList<>();
        sizes.add(new Size("1", "Small", 0, "https://product.hstatic.net/1000075078/product/chocolatenong_949029_c1932e1298a841e18537713220be2333_large.jpg"));
        sizes.add(new Size("2", "Large", 10000, "https://product.hstatic.net/1000075078/product/chocolatenong_949029_c1932e1298a841e18537713220be2333_large.jpg"));

        List<Topping> toppings = new ArrayList<>();
        toppings.add(new Topping("1", "Cheese", 5000, "https://product.hstatic.net/1000075078/product/chocolatenong_949029_c1932e1298a841e18537713220be2333_large.jpg"));
        toppings.add(new Topping("2", "Espresso (1 shot)", 10000, "https://product.hstatic.net/1000075078/product/chocolatenong_949029_c1932e1298a841e18537713220be2333_large.jpg"));

        Product product = Data.instance.products.get(0);
        //TODO: fix this
//        product.setSizes(sizes);
//        product.setToppings(toppings);
        ProductDetailViewModel viewModel=new ProductDetailViewModel(new CartFood(product, null, 0));
        activityProductDetailBinding.setViewModel(viewModel);

        activityProductDetailBinding.productCard.txtName.setVisibility(View.VISIBLE);
        activityProductDetailBinding.productCard.txtPrice.setVisibility(View.VISIBLE);
        activityProductDetailBinding.productCard.description.setVisibility(View.VISIBLE);

        KeyboardHelper.setKeyboardVisibilityListener(this, new OnKeyboardVisibilityListener() {
            @Override
            public void onVisibilityChanged(boolean visible) {
                if(!visible){
                    activityProductDetailBinding.edtNote.clearFocus();
                }
            }
        });
        activityProductDetailBinding.edtNote.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    activityProductDetailBinding.bottomView.setVisibility(View.GONE);
                } else {
                    activityProductDetailBinding.bottomView.setVisibility(View.VISIBLE);
                }
            }
        });

        activityProductDetailBinding.btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isAdded=false;
                SqliteHelper repo=new SqliteHelper(ProductDetailActivity.this);
                ArrayList<HashMap<String, Object>> items= repo.getCartFood("1");
                CartFood cartFoodToAdd=viewModel.getCartFood();
                for (HashMap<String, Object> item :
                        items) {
                    if(item.get(SqliteHelper.COLUMN_FOOD_ID).toString().equals(cartFoodToAdd.getProduct().getId())
                    && item.get(SqliteHelper.COLUMN_SIZE).toString().equals(cartFoodToAdd.getSize())
                    && item.get(SqliteHelper.COLUMN_TOPPING).toString().equals(cartFoodToAdd.getTopping())){
                        isAdded=true;
                        CartFood updatedCartFood=new CartFood(cartFoodToAdd);
                        updatedCartFood.setQuantity(cartFoodToAdd.getQuantity()+(int)item.get(SqliteHelper.COLUMN_QUANTITY));
                        updatedCartFood.setId(Integer.valueOf((String)item.get("id")));
                        repo.updateCartFood(updatedCartFood);
                    }
                }
                if(!isAdded) {
                    repo.createCartFood(viewModel.getCartFood());
                }
            }
        });
        String[] imageList = {"https://product.hstatic.net/1000075078/product/chocolatenong_949029_c1932e1298a841e18537713220be2333_large.jpg",
                "https://aeonmall-long-bien.com.vn/wp-content/uploads/2021/01/aeon-mall-tra-sen-750x468.jpg"};
        setImageViewPager(imageList);

        //TODO: fix this
        //setSizeRecycler(product.getSizes());
        //TODO: fix this
        //setToppingRecycler(product.getToppings());

        activityProductDetailBinding.setIsLoading(false);
    }

    private void setImageViewPager(String[] images) {
        activityProductDetailBinding.productCard.viewpagerImage.setOffscreenPageLimit(1);
        activityProductDetailBinding.productCard.viewpagerImage.setAdapter(new ImageViewPagerAdapter(images));
        activityProductDetailBinding.productCard.setIndex("1");
        activityProductDetailBinding.productCard.setImageCount(Integer.toString(images.length));
        activityProductDetailBinding.productCard.viewpagerImage.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                activityProductDetailBinding.productCard.setIndex(Integer.toString(position + 1));
            }
        });
    }

    private void setSizeRecycler(List<Size> sizes) {
        activityProductDetailBinding.recyclerSize.setNestedScrollingEnabled(false);
        activityProductDetailBinding.recyclerSize.setLayoutManager(new LinearLayoutManager(this));
        activityProductDetailBinding.recyclerSize.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));

        sizeAdapter = new SizeItemAdapter(sizes, new ItemClickedListener() {
            @Override
            public void onItemClick(int position) {
                activityProductDetailBinding.getViewModel().getCartFood().setSize(sizes.get(position).getId());
            }
        });
        activityProductDetailBinding.recyclerSize.setAdapter(sizeAdapter);

    }

    private void setToppingRecycler(List<Topping> toppings) {
        activityProductDetailBinding.recyclerTopping.setNestedScrollingEnabled(false);
        activityProductDetailBinding.recyclerTopping.setLayoutManager(new LinearLayoutManager(this));
        activityProductDetailBinding.recyclerTopping.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));

        toppingItemAdapter=new ToppingItemAdapter(toppings, new ItemClickedListener() {
            @Override
            public void onItemClick(int position) {
                String joinedTopping= toppingItemAdapter.getSelectedToppings().stream()
                        .map(Topping::getId)
                        .collect(Collectors.joining(", "));
                activityProductDetailBinding.getViewModel().getCartFood().setTopping(joinedTopping);
            }
        });
        activityProductDetailBinding.recyclerTopping.setAdapter(toppingItemAdapter);
    }
}