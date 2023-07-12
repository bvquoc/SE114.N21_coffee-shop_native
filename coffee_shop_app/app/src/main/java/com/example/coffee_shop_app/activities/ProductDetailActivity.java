package com.example.coffee_shop_app.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
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
import com.example.coffee_shop_app.models.Store;
import com.example.coffee_shop_app.models.Topping;
import com.example.coffee_shop_app.repository.ProductRepository;
import com.example.coffee_shop_app.repository.SizeRepository;
import com.example.coffee_shop_app.repository.ToppingRepository;
import com.example.coffee_shop_app.utils.SqliteHelper;
import com.example.coffee_shop_app.utils.ItemClickedListener;
import com.example.coffee_shop_app.utils.keyboard.KeyboardHelper;
import com.example.coffee_shop_app.utils.keyboard.OnKeyboardVisibilityListener;
import com.example.coffee_shop_app.viewmodels.CartButtonViewModel;
import com.example.coffee_shop_app.viewmodels.CartViewModel;
import com.example.coffee_shop_app.viewmodels.ProductDetailViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class ProductDetailActivity extends AppCompatActivity {
    private ActivityProductDetailBinding activityProductDetailBinding;
    private SizeItemAdapter sizeAdapter;
    private ToppingItemAdapter toppingItemAdapter;
    private Product product;
    private List<Size> listSizes=new ArrayList<>();
    private List<String> listBannedSize;
    private List<Size> listProductSize;
    private List<Topping> listToppings=new ArrayList<>();
    private List<String> listBannedTopping;
    private List<Topping> listProductTopping;
    private Store selectedStore;
    private ProductDetailViewModel viewModel;
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

        String prdId = ProductDetailActivity.this.getIntent().getStringExtra("productId");

        ProductRepository.getInstance().getProductListMutableLiveData().observe(this, new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> products) {
                product = null;
                for (Product prd: products
                ) {
                    if(prd.getId().equals(prdId))
                    {
                        product = prd;
                        setProduct();
                        break;
                    }
                }
            }
        });
        init();
    }
    private void setProduct(){
        CartButtonViewModel.getInstance().getSelectedStore().observe(this, new Observer<Store>() {
            @Override
            public void onChanged(Store store) {
                if(store.getStateFood().containsKey(product.getId())
                        && store.getStateFood().get(product.getId())!=null){
                    listBannedSize=store.getStateFood().get(product.getId());
                    listBannedTopping=store.getStateTopping();
                    setSize();
                }
            }
        });
        viewModel=new ProductDetailViewModel(new CartFood(product, null, 0));
        activityProductDetailBinding.setViewModel(viewModel);
        setImageViewPager(product.getImages().toArray(new String[0]));

        SizeRepository.getInstance().getSizeListMutableLiveData().observe(this, new Observer<List<Size>>() {
            @Override
            public void onChanged(List<Size> sizes) {
                listSizes=sizes;
                setSize();
            }
        });
        ToppingRepository.getInstance().getToppingListMutableLiveData().observe(this, new Observer<List<Topping>>() {
            @Override
            public void onChanged(List<Topping> toppings) {
                listToppings=toppings;
                setTopping();
            }
        });
    }
    private void setSize(){
        if(listBannedSize==null){
            listBannedSize=new ArrayList<>();
        }
        listProductSize=new ArrayList<>();
        for (Size s :
                listSizes) {
            if(product.getSizes().contains(s.getId())
            && !listBannedSize.contains(s.getId())){
                listProductSize.add(s);
            }
        }
        setSizeRecycler(listProductSize);
    }
    private void setTopping(){
        if(listBannedTopping==null){
            listBannedTopping=new ArrayList<>();
        }
        listProductTopping=new ArrayList<>();
        if(product.getToppings()!=null && !product.getToppings().isEmpty()){
            for (Topping t :
                    listToppings) {
                if(product.getToppings().contains(t.getId())&&!listBannedTopping.contains(t.getId())){
                    listProductTopping.add(t);
                }
            }
            setToppingRecycler(listProductTopping);
        }
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
                ArrayList<CartFood> items= repo.getCartFood(Data.instance.userId);
                CartFood cartFoodToAdd=viewModel.getCartFood();
                for (CartFood item :
                        items) {
                    if(item.getProduct().getId().equals(cartFoodToAdd.getProduct().getId())
                    && item.getSize().equals(cartFoodToAdd.getSize())
                    && item.getTopping().equals(cartFoodToAdd.getTopping())){
                        isAdded=true;
                        CartFood updatedCartFood=new CartFood(cartFoodToAdd);
                        updatedCartFood.setQuantity(cartFoodToAdd.getQuantity()+(int)item.getQuantity());
                        updatedCartFood.setId(item.getId());
                        repo.updateCartFood(updatedCartFood);
                    }
                }
                if(!isAdded) {
                    repo.createCartFood(viewModel.getCartFood());
                }
                CartViewModel.getInstance().getCartFoods().setValue(repo.getCartFood(Data.instance.userId));
                finish();
            }
        });
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