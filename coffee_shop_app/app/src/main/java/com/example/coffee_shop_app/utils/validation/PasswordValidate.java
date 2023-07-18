package com.example.coffee_shop_app.utils.validation;

import androidx.annotation.Nullable;

import com.example.coffee_shop_app.utils.interfaces.Validate;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswordValidate implements Validate {
    @Override
    public Boolean validate(@Nullable String value) {
        if(value == null) return  false;
        String regex = "^(?=.*[a-z])(?=.*?[0-9]).{8,}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }

    @Nullable
    @Override
    public String validator(@Nullable String value) {
        if (value == null || value.isEmpty()) {
            return "Không được bỏ trống trường này!";
        }
        if (!validate(value)) {
            return "Cần it nhất 8 ký tự, một số và một chữ!";
        }
        return null;
    }
}
