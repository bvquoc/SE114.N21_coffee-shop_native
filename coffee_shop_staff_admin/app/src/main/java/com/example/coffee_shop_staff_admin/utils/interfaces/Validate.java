package com.example.coffee_shop_staff_admin.utils.interfaces;

import org.jetbrains.annotations.Nullable;

public interface Validate {
    Boolean validate(@Nullable String value);
    @Nullable
    String validator(@Nullable String value);
}
