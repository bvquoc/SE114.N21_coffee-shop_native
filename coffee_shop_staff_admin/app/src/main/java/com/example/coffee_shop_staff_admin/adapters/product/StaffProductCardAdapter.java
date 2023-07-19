package com.example.coffee_shop_staff_admin.adapters.product;

import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coffee_shop_staff_admin.R;
import com.example.coffee_shop_staff_admin.activities.StaffProductDetailActivity;
import com.example.coffee_shop_staff_admin.databinding.ProductCardBinding;
import com.example.coffee_shop_staff_admin.models.Food;
import com.example.coffee_shop_staff_admin.models.FoodChecker;
import com.example.coffee_shop_staff_admin.models.StoreProduct;
import com.example.coffee_shop_staff_admin.models.Topping;
import com.example.coffee_shop_staff_admin.viewmodels.product.ProductOfStoreViewModel;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;

public class StaffProductCardAdapter extends RecyclerView.Adapter<StaffProductCardAdapter.ViewHolder> {
    private LayoutInflater layoutInflater;
    private List<StoreProduct> productList;

    public StaffProductCardAdapter(List<StoreProduct> productList) {
        this.productList = productList;
    }

    @NonNull
    @Override
    public StaffProductCardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.getContext());
        }
        ProductCardBinding productCardBinding = DataBindingUtil
                .inflate(layoutInflater, R.layout.product_card, parent, false);

        return new StaffProductCardAdapter.ViewHolder(productCardBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.onBindOrderCard(productList.get(position));
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ProductCardBinding productCardBinding;
        private ViewGroup cardContainer;
        private CardView btnProductCard;
        private TextView productCardTitle;
        private ImageView productCardIcon;
        private Boolean isOpen = false;
        private Context context;
        ProductOfStoreViewModel viewModel = ProductOfStoreViewModel.getInstance();

        public ViewHolder(ProductCardBinding productCardBinding) {
            super(productCardBinding.getRoot());

            this.productCardBinding = productCardBinding;
            cardContainer = productCardBinding.productCardContainer;
            btnProductCard = productCardBinding.btnProductCard;
            productCardTitle = productCardBinding.productCardTitle;
            productCardIcon = productCardBinding.productCardIcon;
            context = productCardBinding.getRoot().getContext();
        }

        private void onBindOrderCard(StoreProduct product) {
            if(product instanceof FoodChecker){
                onBindDrink(product);
            }
            else {
                onBindTopping(product);
            }
        }

        private void onBindDrink(StoreProduct product){
            DecimalFormat formatter = new DecimalFormat("#,##0.##");

            FoodChecker drink = (FoodChecker) product;

            Food item = (Food) product.getProduct();
            ImageView image = productCardBinding.productCardImage;
            Picasso.get()
                    .load(item.getImages().get(0))
                    .placeholder(R.drawable.img_placeholder)
                    .error(R.drawable.img_placeholder)
                    .fit()
                    .centerCrop()
                    .into(image);

            productCardTitle.setText(item.getName());
            setStockUI(product.getStocking());

            if (drink.getBlockSize() != null && !drink.getBlockSize().isEmpty() && drink.getStocking()) {
                productCardTitle.setTextColor(ContextCompat.getColor(context, R.color.blue));
                productCardIcon.setColorFilter(ContextCompat.getColor(context, R.color.blue));
            }
            //Animation

            productCardBinding.productCardValue.setText(formatter.format(item.getPrice()) + context.getString(R.string.vndUnit));
            productCardIcon.setOnClickListener(v -> {
                peformSwipe();
            });
            btnProductCard.setOnClickListener(v -> {
                drink.setStocking(!drink.getStocking());

                viewModel.onUpdateProduct(drink);

                setStockUI(drink.getStocking());
                peformSwipe();
            });
            cardContainer.setOnClickListener(v -> {
                Intent intent = new Intent(context, StaffProductDetailActivity.class);
                intent.putExtra("product", drink);
                context.startActivity(intent);
            });
        }
        private void onBindTopping(StoreProduct product){
            DecimalFormat formatter = new DecimalFormat("#,##0.##");

            Topping item = (Topping) product.getProduct();

            ImageView image = productCardBinding.productCardImage;

            Picasso.get()
                    .load(item.getImage())
                    .placeholder(R.drawable.img_placeholder)
                    .error(R.drawable.img_placeholder)
                    .fit()
                    .centerCrop()
                    .into(image);

            productCardTitle.setText(item.getName());
            setStockUI(product.getStocking());
            //Animation

            productCardBinding.productCardValue.setText(formatter.format(item.getPrice()) + context.getString(R.string.vndUnit));
            productCardIcon.setOnClickListener(v -> {
                peformSwipe();
            });

            btnProductCard.setOnClickListener(v -> {
                product.setStocking(!product.getStocking());
                viewModel.onUpdateProduct(product);
                setStockUI(product.getStocking());
                peformSwipe();
            });
        }
        private void peformSwipe() {
            isOpen = !isOpen;
            float cardWidth = cardContainer.getWidth();
            float buttonWidth = btnProductCard.getWidth();
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            float cardStart = ((float) displayMetrics.widthPixels / 2) - cardWidth / 2;

            if (isOpen) {
                cardContainer.animate().x(-buttonWidth).setDuration(500).start();
                btnProductCard.animate().x(cardWidth - buttonWidth).setDuration(500).start();
            } else {
                cardContainer.animate().x(0).setDuration(500).start();
                btnProductCard.animate().x(cardWidth).setDuration(500).start();
            }
        }
        private void setStockUI(Boolean isStocking) {
            if (!isStocking) {
                productCardBinding.btnProductCardIcon.setImageResource(R.drawable.ic_add_shopping_cart);
                productCardTitle.setTextColor(ContextCompat.getColor(context, R.color.red));
                productCardIcon.setColorFilter(ContextCompat.getColor(context, R.color.red));
                btnProductCard.setCardBackgroundColor(ContextCompat.getColor(context, R.color.green));
            } else {
                productCardBinding.btnProductCardIcon.setImageResource(R.drawable.ic_remove_shopping_cart);
                productCardTitle.setTextColor(ContextCompat.getColor(context, R.color.green));
                productCardIcon.setColorFilter(ContextCompat.getColor(context, R.color.green));
                btnProductCard.setCardBackgroundColor(ContextCompat.getColor(context, R.color.red));
            }
        }
    }
}
