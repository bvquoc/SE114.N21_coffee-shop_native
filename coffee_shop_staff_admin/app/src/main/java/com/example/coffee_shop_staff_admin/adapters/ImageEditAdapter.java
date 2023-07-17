package com.example.coffee_shop_staff_admin.adapters;

import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.coffee_shop_staff_admin.R;
import com.google.android.material.button.MaterialButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ImageEditAdapter extends RecyclerView.Adapter<ImageEditAdapter.ImageEditViewHolder> {
    private final String TAG = "ImageEditAdapter";
    List<String> images = new ArrayList<>();
    public ImageEditAdapter(List<String> images)
    {
        this.images = images;
    }
    public void insert(String image)
    {
        images.add(image);
        notifyItemInserted(images.size() - 1);
    }
    public void delete(int index)
    {
        images.remove(index);
        notifyItemRemoved(index);
    }
    public List<String> getImages()
    {
        return  images;
    }
    @NonNull
    @Override
    public ImageEditViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_edit_item, parent, false);
        return new ImageEditViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageEditViewHolder holder, int position) {
        String image = images.get(position);

        Uri imageUri = Uri.parse(image);
        String scheme = imageUri.getScheme();
        if (scheme != null) {
            if (scheme.equals("https") || scheme.equals("gs")) {
                //The image is from FireBase
                try {
                    Glide.with(holder.itemView.getContext())
                            .load(imageUri)
                            .placeholder(R.drawable.img_placeholder)
                            .error(R.drawable.img_placeholder)
                            .into(holder.imageView);
                }
                catch (Exception e)
                {
                    Log.e(TAG, e.getMessage());
                }
            } else {
                //The image is from phone
                Bitmap selectedImageBitmap;
                try {
                    selectedImageBitmap = MediaStore.Images.Media.getBitmap(
                            holder.itemView.getContext().getContentResolver(), imageUri);
                    Glide.with(holder.itemView.getContext())
                            .load(selectedImageBitmap)
                            .placeholder(R.drawable.img_placeholder)
                            .error(R.drawable.img_placeholder)
                            .into(holder.imageView);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            Log.e(TAG, "The URI does not have a scheme or is invalid");
        }


        holder.deleteButton.setOnClickListener(v -> delete(holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public static class ImageEditViewHolder extends RecyclerView.ViewHolder
    {
        public final ImageView imageView;
        public final MaterialButton deleteButton;

        public ImageEditViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }
    }
}
