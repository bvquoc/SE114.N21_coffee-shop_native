package com.example.coffee_shop_staff_admin.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidateHelper {
    public static boolean validatePhone(String phone) {
        String regex = "^(?:[+0]9)?[0-9]{10}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(phone);
        return matcher.matches();
    }
    public static boolean validateDouble(String number) {
        String regex = "[0-9]+(\\.){0,1}[0-9]*";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(number);
        return matcher.matches();
    }
    public static boolean validateText(String text) {
        return text != null && !text.isEmpty();
    }
}
