package com.example.coffee_shop_app.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.coffee_shop_app.R;
import com.example.coffee_shop_app.activities.ProductDetailActivity;
import com.example.coffee_shop_app.activities.address.AddressListingActivity;
import com.example.coffee_shop_app.activities.cart.CartDeliveryActivity;
import com.example.coffee_shop_app.activities.cart.CartPickupActivity;
import com.example.coffee_shop_app.activities.store.StoreActivity;
import com.example.coffee_shop_app.adapters.ProductAdapter;
import com.example.coffee_shop_app.databinding.FragmentMenuBinding;
import com.example.coffee_shop_app.databinding.OrderTypeBottomSheetBinding;
import com.example.coffee_shop_app.repository.ProductRepository;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuFragment extends Fragment {
    private FragmentMenuBinding fragmentMenuBinding;
    private final ProductAdapter favoriteProductAdapter = new ProductAdapter(new ArrayList<>());
    private final ProductAdapter otherProductAdapter = new ProductAdapter(new ArrayList<>());
    private boolean isExecutingCartButtonAnimation1 = false;
    private boolean isExecutingCartButtonAnimation2 = false;
    private BottomSheetDialog bottomSheetDialog;
    private final OnProductClickListener listener = productId -> {
        SharedPreferences prefs =
                requireContext().getSharedPreferences(
                        "recentProducts",
                        MODE_PRIVATE);

        Gson gson = new Gson();

        String json = prefs.getString(
                "recentProducts", null);

        List<String> recentProducts;

        if(json == null)
        {
            recentProducts = new ArrayList<>();
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
    };

    public MenuFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void setToolBarTitle(String title)
    {
        Toolbar toolbar = requireActivity().findViewById(R.id.my_toolbar);
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

        CartButtonViewModel.getInstance().getSelectedOrderType().observe(getViewLifecycleOwner(), orderType -> {
            if (orderType == OrderType.Delivery) {
                setToolBarTitle("Giao hàng");
            } else {
                setToolBarTitle("Mang đi");
            }
        });
        fragmentMenuBinding.setCartButtonViewModel(CartButtonViewModel.getInstance());

        MenuViewModel menuViewModel = new MenuViewModel();
        menuViewModel.getFavoriteProducts().observe(getViewLifecycleOwner(), favoriteProductAdapter::changeDataSet);
        menuViewModel.getOtherProducts().observe(getViewLifecycleOwner(), otherProductAdapter::changeDataSet);
        fragmentMenuBinding.setMenuViewModel(menuViewModel);
        fragmentMenuBinding.deliveryNestedScrollView.setOnScrollChangeListener((View.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {

            if (Math.abs(scrollY - oldScrollY) > 5) {
                if (scrollY > oldScrollY) {
                    if (!isExecutingCartButtonAnimation1 && !isExecutingCartButtonAnimation2) {
                        fragmentMenuBinding.addressInfoMotionLayout.transitionToEnd();
                        fragmentMenuBinding.cartButtonPriceAndAmountMotionLayout.transitionToEnd();
                    }
                } else {
                    if (!isExecutingCartButtonAnimation1 && !isExecutingCartButtonAnimation2) {
                        fragmentMenuBinding.addressInfoMotionLayout.transitionToStart();
                        fragmentMenuBinding.cartButtonPriceAndAmountMotionLayout.transitionToEnd();
                    }
                }
            }
        });
        fragmentMenuBinding.pickUpNestedScrollView.setOnScrollChangeListener((View.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {

            if (Math.abs(scrollY - oldScrollY) > 5) {
                if (scrollY > oldScrollY) {
                    if (!isExecutingCartButtonAnimation1 && !isExecutingCartButtonAnimation2) {
                        fragmentMenuBinding.addressInfoMotionLayout.transitionToEnd();
                        fragmentMenuBinding.cartButtonPriceAndAmountMotionLayout.transitionToEnd();
                    }
                } else {
                    if (!isExecutingCartButtonAnimation1 && !isExecutingCartButtonAnimation2) {
                        fragmentMenuBinding.addressInfoMotionLayout.transitionToStart();
                        fragmentMenuBinding.cartButtonPriceAndAmountMotionLayout.transitionToEnd();
                    }
                }
            }
        });
        fragmentMenuBinding.addressInfoMotionLayout.setTransitionListener(new MotionLayout.TransitionListener() {
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

        fragmentMenuBinding.cartButtonPriceAndAmountMotionLayout.setTransitionListener(new MotionLayout.TransitionListener() {
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

        fragmentMenuBinding.cartButtonPriceAndAmountLinear.setOnClickListener(v -> {
            if (CartButtonViewModel.getInstance().getSelectedOrderType().getValue() == OrderType.Delivery) {
                Intent intent = new Intent(getContext(), CartDeliveryActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(getContext(), CartPickupActivity.class);
                startActivity(intent);
            }
        });
        fragmentMenuBinding.pickUpStorePickupPicker.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), StoreActivity.class);
            startActivity(intent);
        });
        fragmentMenuBinding.deliveryStorePicker.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), StoreActivity.class);
            startActivity(intent);
        });
        fragmentMenuBinding.deliveryAddressPickerFrame.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AddressListingActivity.class);
            startActivity(intent);
        });

        fragmentMenuBinding.refreshLayout.setOnRefreshListener(() -> {
            Map<String, List<String>> stateFood = new HashMap<>();
            if (CartButtonViewModel.getInstance().getSelectedStore().getValue() != null) {
                stateFood = CartButtonViewModel.getInstance().getSelectedStore().getValue().getStateFood();
            }
            ProductRepository.getInstance().registerSnapshotListener(stateFood);
            fragmentMenuBinding.refreshLayout.setRefreshing(false);
        });

        fragmentMenuBinding.cartButton.setOnClickListener(view -> {
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
        return fragmentMenuBinding.getRoot();
    }
}