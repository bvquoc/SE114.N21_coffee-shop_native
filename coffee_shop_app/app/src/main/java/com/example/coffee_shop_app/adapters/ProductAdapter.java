package com.example.coffee_shop_app.adapters;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.coffee_shop_app.models.Product;
import com.example.coffee_shop_app.R;
import com.example.coffee_shop_app.utils.interfaces.OnProductClickListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter implements Filterable {
    private static final int VIEW_TYPE_EMPTY_STATE = 0;
    private static final int VIEW_TYPE_ITEM_HORIZONTAL = 1;
    private final boolean isNeedSearch;
    private List<Product> products;
    private List<Product> productFilter;
    private OnProductClickListener onProductClickListener;

    public void setOnProductClickListener(OnProductClickListener onProductClickListener) {
        this.onProductClickListener = onProductClickListener;
    }
    public void changeDataSet(List<Product> productList)
    {
        products = productList;
        productFilter = productList;
        notifyDataSetChanged();
    }
    public ProductAdapter(List<Product> products) {
        this.products = products;
        this.productFilter = products;
        isNeedSearch = false;
    }
    public ProductAdapter(List<Product> products, boolean isNeedSearch) {
        this.products = products;
        this.productFilter = products;
        this.isNeedSearch = isNeedSearch;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_EMPTY_STATE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.can_not_find_state, parent, false);
            return new EmptyProductStateViewHolder(view);
        } else {
            View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item, parent, false);
            return new ProductItemHorizontalViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ProductItemHorizontalViewHolder) {
            ProductItemHorizontalViewHolder productItemViewHolder = (ProductItemHorizontalViewHolder) holder;
            Product product = productFilter.get(position);

            productItemViewHolder.nameTextView.setText(product.getName());
            productItemViewHolder.nameTextView.setAlpha(product.isAvailable()?1.0f:0.15f);

            DecimalFormat formatter = new DecimalFormat("#,##0.##");
            String formattedPrice = formatter.format(product.getPrice());

            productItemViewHolder.priceTextView.setText(formattedPrice + "đ");
            productItemViewHolder.priceTextView.setAlpha(product.isAvailable()?1.0f:0.15f);

            Glide.with(holder.itemView.getContext())
                    .load(Uri.parse(product.getImages().get(0)))
                    .placeholder(R.drawable.img_placeholder)
                    .error(R.drawable.img_placeholder)
                    .fitCenter()
                    .into(productItemViewHolder.productImageView);
            productItemViewHolder.productImageView.setAlpha(product.isAvailable()?1.0f:0.4f);

            productItemViewHolder.statusNotAvailableTextView.setVisibility(product.isAvailable()?View.GONE:View.VISIBLE);

            long diffInMillis =  (new Date()).getTime() - product.getDateRegister().getTime();
            long diffInDays = Math.round((double) diffInMillis / (24 * 60 * 60 * 1000));
            productItemViewHolder.statusNewTextView.setAlpha(diffInDays < 7?(product.isAvailable()?1.0f:0.4f):0);

            if(product.isAvailable())
            {
                productItemViewHolder.itemView.setOnClickListener(v -> {
                    SharedPreferences prefs =
                            productItemViewHolder.itemView.getContext().getSharedPreferences(
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

                    if(!recentProducts.contains(product.getId())){
                        if (recentProducts.size() > 8){
                            recentProducts.remove(0);
                        }
                    }
                    else{
                        recentProducts.remove(product.getId());
                    }
                    recentProducts.add(product.getId());

                    String jsonDone = gson.toJson(recentProducts);

                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("recentProducts", jsonDone);
                    editor.apply();
                    if(onProductClickListener!=null)
                    {
                        onProductClickListener.onProductClick(product.getId());
                    }
                });
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(isNeedSearch) {
            if(productFilter.size() == 0) {
                return VIEW_TYPE_EMPTY_STATE;
            }
            else 
            {
                return VIEW_TYPE_ITEM_HORIZONTAL;
            }
        }
        else {
            return VIEW_TYPE_ITEM_HORIZONTAL;
        }
    }

    @Override
    public int getItemCount() {
        if(productFilter.size() == 0 && isNeedSearch)
        {
            return 1;
        }
        else
        {
            return productFilter.size();
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String query = charSequence.toString();

                if (query.isEmpty()) {
                    productFilter = products;
                } else {
                    List<Product> filteredList = new ArrayList<>();
                    for (Product model : products) {
                        if (model.getName().toLowerCase().contains(query.toLowerCase())) {
                            filteredList.add(model);
                        }
                    }
                    productFilter = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = productFilter;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                productFilter = (List<Product>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public  static class ProductItemHorizontalViewHolder extends RecyclerView.ViewHolder{
        private final TextView nameTextView;
        private final TextView priceTextView;
        private final TextView statusNewTextView;
        private final TextView statusNotAvailableTextView;
        private final ImageView productImageView;

        public ProductItemHorizontalViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.name_text_view);
            priceTextView = itemView.findViewById(R.id.price_text_view);
            productImageView = itemView.findViewById(R.id.product_image);
            statusNewTextView = itemView.findViewById(R.id.status_new);
            statusNotAvailableTextView = itemView.findViewById(R.id.status_not_available_text_view);
        }
    }
    public static class EmptyProductStateViewHolder extends RecyclerView.ViewHolder{
        public EmptyProductStateViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}

