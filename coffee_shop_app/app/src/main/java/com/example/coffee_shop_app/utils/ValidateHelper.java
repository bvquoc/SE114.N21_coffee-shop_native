package com.example.coffee_shop_app.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidateHelper {
    public static boolean validatePhone(String phone) {
        String regex = "^(?:[+0]9)?[0-9]{10}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(phone);
        return matcher.matches();
    }
    public static boolean validateText(String text) {
        return text != null && !text.isEmpty();
    }
}
