package com.example.coffee_shop_staff_admin.adapters.product;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coffee_shop_staff_admin.R;
import com.example.coffee_shop_staff_admin.databinding.ProductCardBinding;
import com.example.coffee_shop_staff_admin.databinding.SizeStaffBinding;
import com.example.coffee_shop_staff_admin.models.Food;
import com.example.coffee_shop_staff_admin.models.FoodChecker;
import com.example.coffee_shop_staff_admin.models.Size;
import com.example.coffee_shop_staff_admin.models.Store;
import com.example.coffee_shop_staff_admin.repositories.SizeRepository;
import com.example.coffee_shop_staff_admin.repositories.StoreRepository;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StaffSizeAdapter extends RecyclerView.Adapter<StaffSizeAdapter.ViewHolder> {
    private LayoutInflater layoutInflater;
    private FoodChecker product;
    private List<Size> sizes;
    private Food food;

    public Map<String, Boolean> getCurrentState() {
        return currentState;
    }

    Map<String, Boolean> currentState;

    public StaffSizeAdapter(FoodChecker product) {
        this.product = product;
        food = (Food) product.getProduct();
        sizes = SizeRepository.getInstance().getSizeListMutableLiveData().getValue();
        currentState = new HashMap<>();
        for (String item : food.getSizes()) {
            if(product.getStocking()) {
                currentState.put(item, true);
            }
            else {
                currentState.put(item, false);
            }
        }

        if (product.getBlockSize() != null) {
            for (Object item : product.getBlockSize()) {
                currentState.put((String) item, false);
            }
        }
    }

    @NonNull
    @Override
    public StaffSizeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.getContext());
        }
        SizeStaffBinding sizeCardBinding = DataBindingUtil
                .inflate(layoutInflater, R.layout.size_staff, parent, false);

        return new StaffSizeAdapter.ViewHolder(sizeCardBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Size res = sizes.get(0);
        for (Size size : sizes) {
            if (size.getId().equals(food.getSizes().get(position))) {
                res = size;
            }
        }
        holder.onBindSizeCard(res, product);
    }

    @Override
    public int getItemCount() {
        return food.getSizes().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private SizeStaffBinding sizeCardBinding;

        private Context context;

        Store currentStore;

        public ViewHolder(SizeStaffBinding sizeCardBinding) {
            super(sizeCardBinding.getRoot());

            this.sizeCardBinding = sizeCardBinding;
            context = sizeCardBinding.getRoot().getContext();
            currentStore = StoreRepository.getInstance().getCurrentStore().getValue();
        }

        private void onBindSizeCard(Size size, FoodChecker product) {
            DecimalFormat formatter = new DecimalFormat("#,##0.##");
            if (!currentState.get(size.getId())) {
                sizeCardBinding.staffSizeCheckBox.setChecked(false);
            } else {
                sizeCardBinding.staffSizeCheckBox.setChecked(true);
            }
            if (!product.getStocking()) {
                sizeCardBinding.staffSizeCheckBox.setChecked(false);
            }
            //Size Card Logic & UI set
            sizeCardBinding.sizeName.setText(size.getName());
            sizeCardBinding.sizePrice.setText(formatter.format(size.getPrice()) + context.getString(R.string.vndUnit));

            sizeCardBinding.staffSizeCheckBox.setOnClickListener(v -> {
                currentState.put(size.getId(), !currentState.get(size.getId()));
            });
        }

    }
}
