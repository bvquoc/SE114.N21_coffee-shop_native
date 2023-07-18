package com.example.coffee_shop_app.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.coffee_shop_app.Data;
import com.example.coffee_shop_app.R;
import com.example.coffee_shop_app.activities.ProductDetailActivity;
import com.example.coffee_shop_app.activities.address.AddressListingActivity;
import com.example.coffee_shop_app.activities.cart.CartDeliveryActivity;
import com.example.coffee_shop_app.activities.cart.CartPickupActivity;
import com.example.coffee_shop_app.activities.store.StoreActivity;
import com.example.coffee_shop_app.adapters.ProductAdapter;
import com.example.coffee_shop_app.databinding.FragmentHomeBinding;
import com.example.coffee_shop_app.databinding.FragmentMenuBinding;
import com.example.coffee_shop_app.databinding.OrderTypeBottomSheetBinding;
import com.example.coffee_shop_app.models.Product;
import com.example.coffee_shop_app.repository.ProductRepository;
import com.example.coffee_shop_app.utils.interfaces.OnProductClickListener;
import com.example.coffee_shop_app.viewmodels.CartButtonViewModel;
import com.example.coffee_shop_app.viewmodels.HomeViewModel;
import com.example.coffee_shop_app.viewmodels.MenuViewModel;
import com.example.coffee_shop_app.viewmodels.OrderType;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class HomeFragment extends Fragment {
    private List<String> recentProductIds = new ArrayList<>();

    private boolean isExecutingCartButtonAnimation1 = false;
    private boolean isExecutingCartButtonAnimation2 = false;
    private BottomSheetDialog bottomSheetDialog;
    private FragmentHomeBinding fragmentHomeBinding;
    private ProductAdapter productAdapter = new ProductAdapter(new ArrayList<>());


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        HomeViewModel homeViewModel = new HomeViewModel();

        SharedPreferences prefs = requireContext().getSharedPreferences(
                "recentProducts",
                MODE_PRIVATE);


        Gson gson = new Gson();

        String json = prefs.getString(
                "recentProducts", null);

        if(json == null)
        {
            recentProductIds = new ArrayList<>();
        }
        else
        {
            Type type = new TypeToken<ArrayList<String>>() {}.getType();
            recentProductIds = gson.fromJson(json, type);
        }

        ProductRepository.getInstance().getProductListMutableLiveData().observe(getViewLifecycleOwner(), products -> {
            homeViewModel.setLoading(true);

            List<Product> recentProduct = new ArrayList<>();
            for(int i = recentProductIds.size() - 1; i >=0; i--)
            {
                for(int j = 0; j < products.size(); j++)
                {
                    Product product = products.get(j);
                    if(product.getId().equals(recentProductIds.get(i)))
                    {
                        recentProduct.add(product);
                    }
                }

            }

            homeViewModel.getRecentProducts().postValue(recentProduct);
        });



        homeViewModel.getRecentProducts().observe(getViewLifecycleOwner(), products -> {
            productAdapter.changeDataSet(products);
            homeViewModel.setLoading(false);
        });

        fragmentHomeBinding = FragmentHomeBinding.inflate(inflater, container, false);

        productAdapter.setOnProductClickListener(productId -> {
            Intent intent = new Intent(getContext(), ProductDetailActivity.class);
            intent.putExtra("productId", productId);
            startActivity(intent);
        });
        fragmentHomeBinding.productItemRecentRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        fragmentHomeBinding.productItemRecentRecyclerView.setAdapter(productAdapter);

        fragmentHomeBinding.setHomeViewModel(homeViewModel);

        fragmentHomeBinding.cartButton.setOnClickListener(view -> {
            bottomSheetDialog = new BottomSheetDialog(requireContext(), R.style.BottomSheetTheme);
            OrderTypeBottomSheetBinding orderTypeBottomSheetBinding = OrderTypeBottomSheetBinding.inflate(LayoutInflater.from(getContext()), container, false);
            orderTypeBottomSheetBinding.setViewModel(CartButtonViewModel.getInstance());

            orderTypeBottomSheetBinding.closeButton.setOnClickListener(view1 -> bottomSheetDialog.dismiss());

            orderTypeBottomSheetBinding.pickUpEditButton.setOnClickListener(view12 -> {
                bottomSheetDialog.dismiss();
                Intent intent = new Intent(getContext(), StoreActivity.class);
                startActivity(intent);
            });

            orderTypeBottomSheetBinding.deliveryEditButton.setOnClickListener(view13 -> {
                bottomSheetDialog.dismiss();
                Intent intent = new Intent(getContext(), AddressListingActivity.class);
                startActivity(intent);
            });

            orderTypeBottomSheetBinding.deliveryLayout.setOnClickListener(view14 -> {
                CartButtonViewModel.getInstance().getSelectedOrderType().postValue(OrderType.Delivery);
                bottomSheetDialog.dismiss();
            });
            orderTypeBottomSheetBinding.pickUpLayout.setOnClickListener(view15 -> {
                CartButtonViewModel.getInstance().getSelectedOrderType().postValue(OrderType.StorePickUp);
                bottomSheetDialog.dismiss();
            });

            bottomSheetDialog.setContentView(orderTypeBottomSheetBinding.getRoot());
            // Set the behavior to STATE_EXPANDED
            View bottomSheetInternal = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheetInternal != null) {
                BottomSheetBehavior.from(bottomSheetInternal).setState(BottomSheetBehavior.STATE_EXPANDED);
                bottomSheetDialog.show();
            }
        });
        fragmentHomeBinding.setCartButtonViewModel(CartButtonViewModel.getInstance());
        fragmentHomeBinding.scrollView.setOnScrollChangeListener((View.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {

            if (Math.abs(scrollY - oldScrollY) > 5) {
                if (scrollY > oldScrollY) {
                    if (!isExecutingCartButtonAnimation1 && !isExecutingCartButtonAnimation2) {
                        fragmentHomeBinding.addressInfoMotionLayout.transitionToEnd();
                        fragmentHomeBinding.cartButtonPriceAndAmountMotionLayout.transitionToEnd();
                    }
                } else {
                    if (!isExecutingCartButtonAnimation1 && !isExecutingCartButtonAnimation2) {
                        fragmentHomeBinding.addressInfoMotionLayout.transitionToStart();
                        fragmentHomeBinding.cartButtonPriceAndAmountMotionLayout.transitionToEnd();
                    }
                }
            }
        });
        fragmentHomeBinding.scrollView.setOnScrollChangeListener((View.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {

            if (Math.abs(scrollY - oldScrollY) > 5) {
                if (scrollY > oldScrollY) {
                    if (!isExecutingCartButtonAnimation1 && !isExecutingCartButtonAnimation2) {
                        fragmentHomeBinding.addressInfoMotionLayout.transitionToEnd();
                        fragmentHomeBinding.cartButtonPriceAndAmountMotionLayout.transitionToEnd();
                    }
                } else {
                    if (!isExecutingCartButtonAnimation1 && !isExecutingCartButtonAnimation2) {
                        fragmentHomeBinding.addressInfoMotionLayout.transitionToStart();
                        fragmentHomeBinding.cartButtonPriceAndAmountMotionLayout.transitionToEnd();
                    }
                }
            }
        });
        fragmentHomeBinding.addressInfoMotionLayout.setTransitionListener(new MotionLayout.TransitionListener() {
            @Override
            public void onTransitionStarted(MotionLayout motionLayout, int startId, int endId) {
                isExecutingCartButtonAnimation1 = true;
            }

            @Override
            public void onTransitionChange(MotionLayout motionLayout, int startId, int endId, float progress) {

            }

            @Override
            public void onTransitionCompleted(MotionLayout motionLayout, int currentId) {
                isExecutingCartButtonAnimation1 = false;
            }

            @Override
            public void onTransitionTrigger(MotionLayout motionLayout, int triggerId, boolean positive, float progress) {

            }
        });

        fragmentHomeBinding.cartButtonPriceAndAmountMotionLayout.setTransitionListener(new MotionLayout.TransitionListener() {
            @Override
            public void onTransitionStarted(MotionLayout motionLayout, int startId, int endId) {
                isExecutingCartButtonAnimation2 = true;
            }

            @Override
            public void onTransitionChange(MotionLayout motionLayout, int startId, int endId, float progress) {

            }

            @Override
            public void onTransitionCompleted(MotionLayout motionLayout, int currentId) {
                isExecutingCartButtonAnimation2 = false;
            }

            @Override
            public void onTransitionTrigger(MotionLayout motionLayout, int triggerId, boolean positive, float progress) {

            }
        });

        fragmentHomeBinding.cartButtonPriceAndAmountLinear.setOnClickListener(v -> {
            if (CartButtonViewModel.getInstance().getSelectedOrderType().getValue() == OrderType.Delivery) {
                Intent intent = new Intent(getContext(), CartDeliveryActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(getContext(), CartPickupActivity.class);
                startActivity(intent);
            }
        });





        return fragmentHomeBinding.getRoot();
    }
}