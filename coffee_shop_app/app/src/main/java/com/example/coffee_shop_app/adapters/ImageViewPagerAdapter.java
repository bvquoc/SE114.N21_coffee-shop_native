package com.example.coffee_shop_app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coffee_shop_app.R;
import com.example.coffee_shop_app.databinding.ImageItemBinding;

public class ImageViewPagerAdapter extends RecyclerView.Adapter<ImageViewPagerAdapter.ImageViewHolder> {
    private String[] images;
    private LayoutInflater layoutInflater;

    public ImageViewPagerAdapter(String[] images) {
        this.images = images;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(layoutInflater==null){
            layoutInflater=LayoutInflater.from(parent.getContext());
        }
        ImageItemBinding imageItemBinding= DataBindingUtil.inflate(
                layoutInflater, R.layout.image_item, parent, false
        );
        return new ImageViewHolder(imageItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        holder.bindImageItem(images[position]);
    }

    @Override
    public int getItemCount() {
        return images.length;
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder{
        private ImageItemBinding imageItemBinding;
        public ImageViewHolder(ImageItemBinding imageItemBinding) {
            super(imageItemBinding.getRoot());
            this.imageItemBinding=imageItemBinding;
        }

        public void bindImageItem(String imageURL){
            imageItemBinding.setImageURL(imageURL);
        }
    }

}
