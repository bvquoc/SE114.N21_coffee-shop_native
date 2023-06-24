package com.example.coffee_shop_app.viewmodels;

import android.util.Log;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PickupTimeViewModel extends BaseObservable {
    @Bindable
    private int hourStartTime;
    @Bindable
    private Date newDate;
    @Bindable
    private Date openTime;
    @Bindable
    private Date selectedDate;
    @Bindable
    private Date closeTime;

    public PickupTimeViewModel(Date openTime, Date closeTime) {
        this.openTime = openTime;
        this.closeTime = closeTime;
        selectedDate=new Date();
        Date date = new Date() ;
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm") ;
        try{
            if(dateFormat.parse(dateFormat.format(date)).after(dateFormat.parse(dateFormat.format(closeTime))))
            {
                System.out.println("Current time is greater than 12.07");
            }else{
                System.out.println("Current time is less than 12.07");
            }
        }catch (Exception e){
            Log.d("PARSE ERROR", "PickupTimeViewModel: "+e.getMessage());
        }

    }
}
