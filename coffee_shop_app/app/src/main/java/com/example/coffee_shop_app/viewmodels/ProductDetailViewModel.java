package com.example.coffee_shop_app.viewmodels;

import android.util.Log;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.example.coffee_shop_app.BR;
import com.example.coffee_shop_app.models.CartFood;
import com.example.coffee_shop_app.repository.ProductRepository;

public class ProductDetailViewModel extends BaseObservable {
    public ProductDetailViewModel(CartFood cartFood, boolean isFavorite) {
        this.cartFood = cartFood;
        this.favorite=isFavorite;
    }
    @Bindable
    private CartFood cartFood;

    @Bindable
    public String getNote(){
        return cartFood.getNote();
    }

    public void setNote(String value){
        if(!cartFood.getNote().equals(value)){
            cartFood.setNote(value);
            notifyPropertyChanged(BR.note);

            Log.d("NOTE", "setNote: "+cartFood.getNote());
        }
    }
    @Bindable
    private boolean minusEnable;

    public boolean isMinusEnable() {
        return minusEnable;
    }

    public void setMinusEnable(boolean minusEnable) {
        this.minusEnable = minusEnable;
        notifyPropertyChanged(BR.minusEnable);
    }

    @Bindable
    private boolean favorite;

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
        notifyPropertyChanged(BR.favorite);
    }

    public CartFood getCartFood() {
        return cartFood;
    }
    public void increaseQuantity(){
        if(!minusEnable){
            setMinusEnable(true);
        }
        cartFood.setQuantity(cartFood.getQuantity()+1);
    }
    public void decreaseQuantity(){
        int current=cartFood.getQuantity();

        if(current!=1){
            if(current-1==1){
                setMinusEnable(false);
                cartFood.setQuantity(current-1);
            }else{
                cartFood.setQuantity(current-1);
            }
        }
    }

    public void flipIsFavorite(){
        //TODO: add to user favorite list
        boolean value=!this.favorite;
        setFavorite(value);
        ProductRepository.getInstance().updateFavorite(cartFood.getProduct().getId(), value);
    }

}
