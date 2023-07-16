package com.example.coffee_shop_app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.coffee_shop_app.R;
import com.example.coffee_shop_app.databinding.SizeItemBinding;
import com.example.coffee_shop_app.models.Size;
import com.example.coffee_shop_app.utils.ItemClickedListener;

import java.text.DecimalFormat;
import java.util.List;

public class SizeItemAdapter extends RecyclerView.Adapter<SizeItemAdapter.ViewHolder> {
    private List<Size> sizes;
    private ItemClickedListener itemClickedListener;
    private LayoutInflater layoutInflater;
    int selectedPosition = -1;

    public SizeItemAdapter(List<Size> sizes, ItemClickedListener itemClickedListener) {
        this.sizes = sizes;
        this.itemClickedListener = itemClickedListener;
        if(sizes.size()>0)
            selectedPosition=0;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(layoutInflater==null){
            layoutInflater=LayoutInflater.from(parent.getContext());
        }
        SizeItemBinding sizeItemBinding= DataBindingUtil.inflate(layoutInflater, R.layout.size_item, parent, false);
        return new ViewHolder(sizeItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindSizeItem(sizes.get(position));
    }

    @Override
    public int getItemCount() {
        return sizes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private SizeItemBinding sizeItemBinding;

        public ViewHolder(SizeItemBinding sizeItemBinding) {
            super(sizeItemBinding.getRoot());
            this.sizeItemBinding = sizeItemBinding;
        }

        public void bindSizeItem(Size size){
            DecimalFormat formatter = new DecimalFormat("#,##0.##");
            sizeItemBinding.setSize(size);
            sizeItemBinding.executePendingBindings();
            sizeItemBinding.txtPrice.setText(formatter.format(size.getPrice()) + sizeItemBinding.getRoot().getContext().getString(R.string.vndUnit));

            sizeItemBinding.rdBtnSize.setChecked(getAdapterPosition()
                    == selectedPosition);
            itemClickedListener.onItemClick(selectedPosition);

            // set listener on radio button
            sizeItemBinding.rdBtnSize.setOnCheckedChangeListener(
                    new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(
                                CompoundButton compoundButton,
                                boolean b)
                        {
                            // check condition
                            if (b) {
                                // When checked
                                // update selected position
                                selectedPosition
                                        = getAdapterPosition();
                                itemClickedListener.onItemClick(selectedPosition);
                                notifyDataSetChanged();
                            }
                        }
                    });

            sizeItemBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sizeItemBinding.rdBtnSize.setChecked(true);
                }
            });
        }
    }
}
