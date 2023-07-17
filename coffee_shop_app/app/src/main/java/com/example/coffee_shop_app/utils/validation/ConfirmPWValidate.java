package com.example.coffee_shop_app.utils.validation;

import androidx.annotation.Nullable;

import com.example.coffee_shop_app.utils.interfaces.Validate;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfirmPWValidate implements Validate {
    String oldPassWord;

    public ConfirmPWValidate(String oldPassWord){
        this.oldPassWord = oldPassWord;
    }
    @Override
    public Boolean validate(@Nullable String value) {
        if(value == null) return  false;
        String regex = "^(?=.*[a-z])(?=.*?[0-9]).{8,}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(value);
        return matcher.matches() && value == oldPassWord;
    }

    @Nullable
    @Override
    public String validator(@Nullable String value) {
        if (value == null || value.isEmpty()) {
            return "Không được bỏ trống trường này!";
        }
        if (!validate(value)) {
            return "Xác nhận mật khẩu sai";
        }
        return null;
    }
}
