package com.example.coffee_shop_app.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coffee_shop_app.R;
import com.example.coffee_shop_app.databinding.ToppingItemBinding;
import com.example.coffee_shop_app.models.Topping;
import com.example.coffee_shop_app.utils.ItemClickedListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ToppingItemAdapter extends RecyclerView.Adapter<ToppingItemAdapter.ViewHolder> {
    private List<Topping> toppings;
    private LayoutInflater layoutInflater;
    private List<Topping> selectedToppings=new ArrayList<>();

    public List<Topping> getSelectedToppings() {
        return selectedToppings;
    }
    private ItemClickedListener itemClickedListener;

    public ToppingItemAdapter(List<Topping> toppings, ItemClickedListener itemClickedListener) {
        this.toppings = toppings;
        this.itemClickedListener = itemClickedListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(layoutInflater==null){
            layoutInflater=LayoutInflater.from(parent.getContext());
        }
        ToppingItemBinding toppingItemBinding= DataBindingUtil.inflate(layoutInflater, R.layout.topping_item, parent, false);
        return new ViewHolder(toppingItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindToppingItem(toppings.get(position));
    }

    @Override
    public int getItemCount() {
        return toppings.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ToppingItemBinding toppingItemBinding;

        public ViewHolder(ToppingItemBinding toppingItemBinding) {
            super(toppingItemBinding.getRoot());
            this.toppingItemBinding = toppingItemBinding;
        }

        public void bindToppingItem(Topping topping){
            DecimalFormat formatter = new DecimalFormat("#,##0.##");
            toppingItemBinding.setTopping(topping);
            toppingItemBinding.txtPrice.setText(formatter.format(topping.getPrice()) + toppingItemBinding.getRoot().getContext().getString(R.string.vndUnit));
            toppingItemBinding.cbTopping.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        if(selectedToppings.size()>=2){
                            buttonView.setChecked(false);
                            Toast.makeText(toppingItemBinding.getRoot().getContext(), "Bạn không được chọn quá 2 loại topping!", Toast.LENGTH_SHORT).show();
                        }else{
                            selectedToppings.add(topping);
                        }
                    } else {
                        selectedToppings.remove(topping);
                    }
                    itemClickedListener.onItemClick(-1);
                }
            });
            toppingItemBinding.executePendingBindings();
            toppingItemBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isChecked=toppingItemBinding.cbTopping.isChecked();
                    if(!isChecked && selectedToppings.size()>=2){
                            Toast.makeText(toppingItemBinding.getRoot().getContext(), "Bạn không được chọn quá 2 loại topping!", Toast.LENGTH_SHORT).show();
                    } else{
                        toppingItemBinding.cbTopping.setChecked(!isChecked);
                    }
                }
            });
        }
    }
}

