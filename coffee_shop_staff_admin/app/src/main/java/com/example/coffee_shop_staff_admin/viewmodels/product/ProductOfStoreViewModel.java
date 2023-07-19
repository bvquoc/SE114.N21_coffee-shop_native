package com.example.coffee_shop_staff_admin.viewmodels.product;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.lifecycle.MutableLiveData;

import com.example.coffee_shop_staff_admin.BR;
import com.example.coffee_shop_staff_admin.models.Food;
import com.example.coffee_shop_staff_admin.models.FoodChecker;
import com.example.coffee_shop_staff_admin.models.Store;
import com.example.coffee_shop_staff_admin.models.StoreProduct;
import com.example.coffee_shop_staff_admin.models.Topping;
import com.example.coffee_shop_staff_admin.repositories.FoodRepository;
import com.example.coffee_shop_staff_admin.repositories.StoreRepository;
import com.example.coffee_shop_staff_admin.repositories.ToppingRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductOfStoreViewModel extends BaseObservable {
    private static ProductOfStoreViewModel instance;

    public static ProductOfStoreViewModel getInstance() {
        if (instance == null) {
            instance = new ProductOfStoreViewModel();
        }
        return instance;
    }

    public MutableLiveData<List<StoreProduct>> getDrinkListLiveData() {
        return drinkListLiveData;
    }

    public MutableLiveData<List<StoreProduct>> getToppingListLiveData() {
        return toppingListLiveData;
    }

    private final MutableLiveData<List<StoreProduct>> drinkListLiveData = new MutableLiveData<>(new ArrayList<>());

    private final MutableLiveData<List<StoreProduct>> toppingListLiveData = new MutableLiveData<>(new ArrayList<>());
    StoreRepository storeRepository = StoreRepository.getInstance();
    public ProductOfStoreViewModel() {
        StoreRepository.getInstance().getCurrentStoreLiveData().observeForever(store -> {
            FoodRepository.getInstance().getFoodListMutableLiveData().observeForever(listFood -> {
                setLoading(true);
//            turn to FoodChecker and post value
                Map<String, List<String>> stateFood = store.getStateFood();
                List<StoreProduct> transList = new ArrayList<>();
                for (Food item : listFood) {
                    List<String> itemState = stateFood.get(item.getId());
                    Boolean isStocking = true;
                    if (itemState != null) {
                        isStocking = !itemState.isEmpty() && itemState.size() < item.getToppings().size();
                    }
                    transList.add(new FoodChecker(item, isStocking, store.getId(), item.getId(), itemState));
                }
                drinkListLiveData.postValue(transList);
                setLoading(false);
            });

            ToppingRepository.getInstance().getToppingListMutableLiveData().observeForever(listTopping -> {
                setLoading(true);
//            turn to StoreProduct and post value
                List<String> stateTopping = store.getStateTopping();
                List<StoreProduct> transList = new ArrayList<>();
                for (Topping item : listTopping) {
                    Boolean isStocking = true;
                    if (stateTopping != null) {
                        isStocking = !stateTopping.stream().anyMatch(it -> it.equals(item.getId()));
                    }
                    transList.add(new StoreProduct(item, isStocking, store.getId()));
                }
                toppingListLiveData.postValue(transList);
                setLoading(false);
            });
        });
    }

    public void onUpdateProduct(StoreProduct product) {
        Store storeRef = StoreRepository.getInstance().getCurrentStoreLiveData().getValue();
        if(product instanceof FoodChecker){

            Map<String, List<String>> stateFood = storeRef.getStateFood();

            if(stateFood == null){
                stateFood = new HashMap<>();
            }

            FoodChecker productTemp = (FoodChecker) product;

            Food food = (Food) product.getProduct();

            if(product.getStocking()){
                productTemp.setBlockSize(null);
            }
            else{
                productTemp.setBlockSize(new ArrayList<>());
            }

            if(productTemp.getBlockSize() == null){
                stateFood.remove(food.getId());
            }
            else if (productTemp.getBlockSize().size() == food.getSizes().size() || productTemp.getBlockSize().isEmpty()){
                product.setStocking(false);
                productTemp.setStocking(false);
                stateFood.put(food.getId(), new ArrayList<>());
            }
            else {
                stateFood.put(food.getId(), productTemp.getBlockSize());
            }

            storeRef.setStateFood(stateFood);
        }
        else {
            Boolean check = false;
            List<String> stateTopping = new ArrayList<>();
            Topping topping = (Topping) product.getProduct();
            if(storeRef.getStateTopping() != null) {
                for (String item : storeRef.getStateTopping()) {

                    if (item.equals(topping.getId())) {
                        if (!product.getStocking()) {
                            stateTopping.add(topping.getId());
                        }
                        check = true;
                    } else {
                        stateTopping.add(item);
                    }
                }
            }
            if(!check){
                if(!product.getStocking()){
                    stateTopping.add(topping.getId());
                }
            }
            storeRef.setStateTopping(stateTopping);
        }

        onUpdateProductByPass(storeRef, product);
    }
    public void onUpdateProductByPass(Store storeRef, StoreProduct product){

        storeRepository.updateProduct(storeRef, params -> {

            StoreRepository.getInstance().getCurrentStoreLiveData().postValue(storeRef);

            List<StoreProduct> newList = new ArrayList<>();
            if(product instanceof FoodChecker) {
                for (StoreProduct item : drinkListLiveData.getValue()) {
                    if(((Food) item.getProduct()).getId().equals(((Food) product.getProduct()).getId())){
                        newList.add(product);
                    }
                    else {
                        newList.add(item);
                    }
                }
                drinkListLiveData.postValue(newList);
            }
            else {
                for (StoreProduct item : toppingListLiveData.getValue()) {
                    if(((Topping) item.getProduct()).getId().equals(((Topping) product.getProduct()).getId())){
                        newList.add(product);
                    }
                    else {
                        newList.add(item);
                    }
                }
                toppingListLiveData.postValue(newList);
            }
        }, params -> {

        });
    }
    @Bindable
    private boolean loading = true;

    public boolean isLoading() {
        return loading;
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
        notifyPropertyChanged(BR.loading);
    }
}
