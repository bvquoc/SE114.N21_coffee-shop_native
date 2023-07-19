package com.example.coffee_shop_staff_admin.models;

import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.List;

public class FoodChecker<T> extends StoreProduct implements Serializable {
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Nullable
    public List<String> getBlockSize() {
        return blockSize;
    }

    public void setBlockSize(@Nullable List<String> blockSize) {
        this.blockSize = blockSize;
    }

    String id;
    @Nullable
    List<String> blockSize;
    public FoodChecker(T product, Boolean isStocking, String store, String id, List<String> blockSize) {
        super(product, isStocking, store);
        this.id = id;
        this.blockSize = blockSize;
    }
}
