package com.example.coffee_shop_app.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.coffee_shop_app.R;
import com.example.coffee_shop_app.activities.ProductDetailActivity;
import com.example.coffee_shop_app.activities.address.AddressListingActivity;
import com.example.coffee_shop_app.activities.promo.PromoActivity;
import com.example.coffee_shop_app.activities.store.StoreActivity;
import com.example.coffee_shop_app.adapters.ProductAdapter;
import com.example.coffee_shop_app.databinding.FragmentMenuBinding;
import com.example.coffee_shop_app.databinding.FragmentStoresBinding;
import com.example.coffee_shop_app.databinding.OrderTypeBottomSheetBinding;
import com.example.coffee_shop_app.models.Product;
import com.example.coffee_shop_app.utils.interfaces.OnProductClickListener;
import com.example.coffee_shop_app.viewmodels.CartButtonViewModel;
import com.example.coffee_shop_app.viewmodels.MenuViewModel;
import com.example.coffee_shop_app.viewmodels.OrderType;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MenuFragment extends Fragment {
    private FragmentMenuBinding fragmentMenuBinding;
    private ProductAdapter favoriteProductAdapter = new ProductAdapter(new ArrayList<>());
    private ProductAdapter otherProductAdapter = new ProductAdapter(new ArrayList<>());
    private boolean isExcutingCartButtonAnimation1 = false;
    private boolean isExcutingCartButtonAnimation2 = false;
    private BottomSheetDialog bottomSheetDialog;
    private OnProductClickListener listener = new OnProductClickListener() {
        @Override
        public void onProductClick(String productId) {
            SharedPreferences prefs =
                    getContext().getSharedPreferences(
                            "recentProducts",
                            MODE_PRIVATE);

            Gson gson = new Gson();

            String json = prefs.getString(
                    "recentProducts", null);

            List<String> recentProducts;

            if(json == null)
            {
                recentProducts = new ArrayList<String>();
            }
            else
            {
                Type type = new TypeToken<ArrayList<String>>() {}.getType();
                recentProducts = gson.fromJson(json, type);
            }

            if(!recentProducts.contains(productId)){
                if (recentProducts.size() > 8){
                    recentProducts.remove(0);
                }
            }
            else{
                recentProducts.remove(productId);
            }
            recentProducts.add(productId);

            String jsonDone = gson.toJson(recentProducts);

            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("recentProducts", jsonDone);
            editor.apply();

            Intent intent = new Intent(getContext(), ProductDetailActivity.class);
            intent.putExtra("productId", productId);
            startActivity(intent);
        }
    };
    public MenuFragment() {

    }

    public static MenuFragment newInstance() {
        MenuFragment fragment = new MenuFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void setToolBarTitle(String title)
    {
        Toolbar toolbar = ((AppCompatActivity)requireActivity()).findViewById(R.id.my_toolbar);
        toolbar.setTitle(title);
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        favoriteProductAdapter.setOnProductClickListener(listener);
        otherProductAdapter.setOnProductClickListener(listener);

        fragmentMenuBinding = FragmentMenuBinding.inflate(inflater, container, false);

        fragmentMenuBinding.pickUpProductItemFavoritesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        fragmentMenuBinding.pickUpProductItemFavoritesRecyclerView.setAdapter(favoriteProductAdapter);

        fragmentMenuBinding.pickUpProductItemRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        fragmentMenuBinding.pickUpProductItemRecyclerView.setAdapter(otherProductAdapter);

        fragmentMenuBinding.deliveryProductItemFavoritesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        fragmentMenuBinding.deliveryProductItemFavoritesRecyclerView.setAdapter(favoriteProductAdapter);

        fragmentMenuBinding.deliveryProductItemRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        fragmentMenuBinding.deliveryProductItemRecyclerView.setAdapter(otherProductAdapter);

        CartButtonViewModel.getInstance().getSelectedOrderType().observe(getViewLifecycleOwner(), new Observer<OrderType>() {
            @Override
            public void onChanged(OrderType orderType) {
                if(orderType == OrderType.Delivery)
                {
                    setToolBarTitle("Giao hàng");
                }
                else
                {
                    setToolBarTitle("Mang đi");
                }
            }
        });
        fragmentMenuBinding.setCartButtonViewModel(CartButtonViewModel.getInstance());

        MenuViewModel menuViewModel = new MenuViewModel();
        menuViewModel.getFavoriteProducts().observe(getViewLifecycleOwner(), new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> products) {
                favoriteProductAdapter.changeDataSet(products);
            }
        });
        menuViewModel.getOtherProducts().observe(getViewLifecycleOwner(), new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> products) {
                otherProductAdapter.changeDataSet(products);
            }
        });
        fragmentMenuBinding.setMenuViewModel(menuViewModel);
        fragmentMenuBinding.deliveryNestedScrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                if(Math.abs(scrollY - oldScrollY)> 5)
                {
                    if (scrollY > oldScrollY) {
                        if(!isExcutingCartButtonAnimation1 && !isExcutingCartButtonAnimation2)
                        {
                            fragmentMenuBinding.addressInfoMotionLayout.transitionToEnd();
                            fragmentMenuBinding.cartButtonPriceAndAmountMotionLayout.transitionToEnd();
                        }
                    } else {
                        if(!isExcutingCartButtonAnimation1 && !isExcutingCartButtonAnimation2)
                        {
                            fragmentMenuBinding.addressInfoMotionLayout.transitionToStart();
                            fragmentMenuBinding.cartButtonPriceAndAmountMotionLayout.transitionToEnd();
                        }
                    }
                }
            }
        });
        fragmentMenuBinding.pickUpNestedScrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                if(Math.abs(scrollY - oldScrollY)> 5)
                {
                    if (scrollY > oldScrollY) {
                        if(!isExcutingCartButtonAnimation1 && !isExcutingCartButtonAnimation2)
                        {
                            fragmentMenuBinding.addressInfoMotionLayout.transitionToEnd();
                            fragmentMenuBinding.cartButtonPriceAndAmountMotionLayout.transitionToEnd();
                        }
                    } else {
                        if(!isExcutingCartButtonAnimation1 && !isExcutingCartButtonAnimation2)
                        {
                            fragmentMenuBinding.addressInfoMotionLayout.transitionToStart();
                            fragmentMenuBinding.cartButtonPriceAndAmountMotionLayout.transitionToEnd();
                        }
                    }
                }
            }
        });
        fragmentMenuBinding.addressInfoMotionLayout.setTransitionListener(new MotionLayout.TransitionListener() {
            @Override
            public void onTransitionStarted(MotionLayout motionLayout, int startId, int endId) {
                isExcutingCartButtonAnimation1 = true;
            }

            @Override
            public void onTransitionChange(MotionLayout motionLayout, int startId, int endId, float progress) {

            }

            @Override
            public void onTransitionCompleted(MotionLayout motionLayout, int currentId) {
                isExcutingCartButtonAnimation1 = false;
            }

            @Override
            public void onTransitionTrigger(MotionLayout motionLayout, int triggerId, boolean positive, float progress) {

            }
        });

        fragmentMenuBinding.cartButtonPriceAndAmountMotionLayout.setTransitionListener(new MotionLayout.TransitionListener() {
            @Override
            public void onTransitionStarted(MotionLayout motionLayout, int startId, int endId) {
                isExcutingCartButtonAnimation2 = true;
            }

            @Override
            public void onTransitionChange(MotionLayout motionLayout, int startId, int endId, float progress) {

            }

            @Override
            public void onTransitionCompleted(MotionLayout motionLayout, int currentId) {
                isExcutingCartButtonAnimation2 = false;
            }

            @Override
            public void onTransitionTrigger(MotionLayout motionLayout, int triggerId, boolean positive, float progress) {

            }
        });

        fragmentMenuBinding.pickUpStorePickupPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), StoreActivity.class);
                startActivity(intent);
            }
        });
        fragmentMenuBinding.deliveryStorePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), StoreActivity.class);
                startActivity(intent);
            }
        });
        fragmentMenuBinding.deliveryAddressPickerFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddressListingActivity.class);
                startActivity(intent);
            }
        });

        fragmentMenuBinding.cartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("Hello", "dsadsad");
                bottomSheetDialog = new BottomSheetDialog(getContext(), R.style.BottomSheetTheme);
                OrderTypeBottomSheetBinding orderTypeBottomSheetBinding = OrderTypeBottomSheetBinding.inflate(LayoutInflater.from(getContext()), container, false);
                orderTypeBottomSheetBinding.setViewModel(CartButtonViewModel.getInstance());

                orderTypeBottomSheetBinding.closeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bottomSheetDialog.dismiss();
                    }
                });

                orderTypeBottomSheetBinding.pickUpEditButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //TODO: press pickup
                    }
                });

                orderTypeBottomSheetBinding.deliveryEditButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //TODO: press pickup
                    }
                });

                orderTypeBottomSheetBinding.deliveryLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CartButtonViewModel.getInstance().setDelivering(true);
                    }
                });
                orderTypeBottomSheetBinding.pickUpEditButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CartButtonViewModel.getInstance().setDelivering(false);
                    }
                });

                bottomSheetDialog.setContentView(orderTypeBottomSheetBinding.getRoot());
                // Set the behavior to STATE_EXPANDED
                View bottomSheetInternal = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
                BottomSheetBehavior.from(bottomSheetInternal).setState(BottomSheetBehavior.STATE_EXPANDED);
                bottomSheetDialog.show();
            }
        });
        return fragmentMenuBinding.getRoot();
    }
}