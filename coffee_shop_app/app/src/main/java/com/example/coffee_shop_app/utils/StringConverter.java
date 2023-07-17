package com.example.coffee_shop_app.utils;

import org.joda.time.DateTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StringConverter {

    public static String DateToString(Date date){
        String pattern = "dd/MM/yyyy";
        DateFormat df = new SimpleDateFormat(pattern);
        return  df.format(date);
    }

    public static Date StringToDate(String s){
        String pattern = "dd/MM/yyyy";
        DateFormat df = new SimpleDateFormat(pattern);
        Date startDate;
        try{
            startDate = df.parse(s);
        } catch (Exception e) {
            startDate = DateTime.now().toDate();
        }
        return startDate;
    }
}
