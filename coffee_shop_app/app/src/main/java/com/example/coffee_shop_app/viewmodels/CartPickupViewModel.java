package com.example.coffee_shop_app.viewmodels;

import android.text.format.DateUtils;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.example.coffee_shop_app.models.AddressDelivery;
import com.example.coffee_shop_app.models.MLocation;
import com.example.coffee_shop_app.models.Store;
import com.example.coffee_shop_app.repository.StoreRepository;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CartPickupViewModel extends ViewModel {
    private CartViewModel cartViewModel;
    private MutableLiveData<Store> storePickup=new MutableLiveData<>();
    private MutableLiveData<Date> timePickup=new MutableLiveData<>();
    private MutableLiveData<List<Date>> dayList=new MutableLiveData<>();
    private MutableLiveData<List<Integer>> hourStartTimeList=new MutableLiveData<>();

    public CartPickupViewModel() {
        cartViewModel=CartViewModel.getInstance();
        CartButtonViewModel.getInstance().getSelectedStore().observeForever(new Observer<Store>() {
            @Override
            public void onChanged(Store store) {
                storePickup.setValue(store);
                initDayTimeList();
            }
        });
//        initDayTimeList();
    }

    public CartViewModel getCartViewModel() {
        return cartViewModel;
    }

    public void setCartViewModel(CartViewModel cartViewModel) {
        this.cartViewModel = cartViewModel;
    }

    public MutableLiveData<Store> getStorePickup() {
        return storePickup;
    }

    public void setStorePickup(MutableLiveData<Store> storePickup) {
        this.storePickup = storePickup;
    }

    public MutableLiveData<Date> getTimePickup() {
        return timePickup;
    }

    public void setTimePickup(MutableLiveData<Date> timePickup) {
        this.timePickup = timePickup;
    }
    public MutableLiveData<List<Date>> getDayList() {
        return dayList;
    }

    public MutableLiveData<List<Integer>> getHourStartTimeList() {
        return hourStartTimeList;
    }
//    @Override
//    public void placeOrder(Store store, AddressDelivery address, Date pickupTime, String status){
//        super.placeOrder(
//                this.storePickup.getValue(),
//                null,
//                this.timePickup.getValue(),
//                "Đang xử lí");
//    }

    // call only there is a store
    public void initDayTimeList(){
        ArrayList<Date> listDay=new ArrayList<>();
        ArrayList<Integer> listHourStartTime=new ArrayList<>();
        Date date = new Date() ;
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm") ;
        boolean isToday=true;
        try{
            if(dateFormat.parse(dateFormat.format(date)).after(dateFormat.parse(dateFormat.format(this.getStorePickup().getValue().getTimeClose())))
            || dateFormat.parse(dateFormat.format(date)).before(dateFormat.parse(dateFormat.format(this.getStorePickup().getValue().getTimeOpen())))){
                isToday=false;
            }

        }catch (Exception e){
            Log.d("PARSE ERROR", "Cart pickup time: "+e.getMessage());
        }
        c.setTime(date);
        for (int i = 0; i < 3; i++) {
            if(i==0 && !isToday){
                continue;
            }
            if(i==0){
                listHourStartTime.add(dateTimeToHour(new Date()));
            }else{
                listHourStartTime.add(dateTimeToHour(storePickup.getValue().getTimeOpen()));
            }
            c.add(Calendar.DATE, 1);
            listDay.add(c.getTime());
        }
        this.dayList.setValue(listDay);
        this.hourStartTimeList.setValue(listHourStartTime);
        initSelectedDate();

    }
    private void initSelectedDate(){
        Date date= getDayList().getValue().get(0);
        int selectedTimeIndex=getHourStartTimeList().getValue().get(0);
        int hour=selectedTimeIndex/2;
        int minute=(selectedTimeIndex%2)*30;
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        getTimePickup().setValue(c.getTime());
    }

    public Integer dateTimeToHour(Date dateTime) {
        DateTime date=new DateTime(dateTime);
        int day=date.getHourOfDay();
        int minute=date.getMinuteOfHour();
        return date.getHourOfDay()
                * 2 + (date.getMinuteOfHour() > 30 ? 2 : 1);
    }

    public String datetimeToHour(Date dateTime) {
        int index=dateTimeToHour(dateTime);
        return indexToTime(index);
    }
    String indexToTime(int index) {
        int hour = (index / 2);
        String hourString= hour < 10 ? "0"+hour : hour+"";
        String minuteString=index % 2 * 30 == 0 ? "00" : String.valueOf(index % 2 * 30);
        return hourString+":" + minuteString;
    }

    public String datetimeToString(int index) {
        Date date = new Date() ;
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy") ;
        if(index==0){
            return "Hôm nay";
        }else if(index==1){
            return "Ngày mai";
        }else if(index==2){
            c.add(Calendar.DATE, 2);
            return dateFormat.format(c.getTime());
        }
        return "";
    }
    public String[] dayListToArray(){
        String[] listString=new String[]{};
        Date date = new Date() ;
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy") ;

        int size=this.dayList.getValue().size();
        c.add(Calendar.DATE, 2);
        String theThirdDay=dateFormat.format(c.getTime());
        if(size==3){
            listString= new String[]{"Hôm nay", "Ngày mai", theThirdDay};
        }else if(size==2){
            listString= new String[]{"Ngày mai", theThirdDay};
        }
        return listString;
    }
    public String[] timeListToArray(int minValue, int maxValue){
        ArrayList<String> listTime=new ArrayList<>();
        for (int i = minValue; i <= maxValue; i++) {
            listTime.add(indexToTime(i));
        }
        return listTime.toArray(new String[0]);
    }
    public String datetimeToPickup(DateTime dateTime) {
        DateTime now = DateTime.now();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy") ;
        String day=dateFormat.format(dateTime.toDate());
        String hour=datetimeToHour(dateTime);
        if (now.getMonthOfYear() == dateTime.getMonthOfYear()
                && dateTime.getYear() == now.getYear()) {
            if (now.getDayOfMonth()== dateTime.getDayOfMonth()) {
                day = "Hôm nay";
            } else if (now.getDayOfMonth() == dateTime.getDayOfMonth() - 1) {
                day = "Ngày mai";
            }
        }

        return hour+", "+day;
    }
    public String datetimeToHour(DateTime dateTime) {
        int hour = dateTime.getHourOfDay();
        int minute=dateTime.getMinuteOfHour();
        String hourString= hour < 10 ? "0"+hour : hour+"";
        String minuteString=minute<10? "0"+minute:minute+"";
        return hourString+ ":"+minuteString;
    }
    public void calculateTotalPrice(){
        cartViewModel.calculateTotalFood();
    }
}
