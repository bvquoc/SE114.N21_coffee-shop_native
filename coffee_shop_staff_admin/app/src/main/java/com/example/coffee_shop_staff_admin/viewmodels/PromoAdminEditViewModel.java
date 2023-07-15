package com.example.coffee_shop_staff_admin.viewmodels;

import android.widget.CompoundButton;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.lifecycle.MutableLiveData;

import com.example.coffee_shop_staff_admin.BR;
import com.example.coffee_shop_staff_admin.models.Promo;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class PromoAdminEditViewModel extends BaseObservable {
    public PromoAdminEditViewModel()
    {
        startTime.observeForever(date -> setStartTimeString(getTimeString(date)));
        startDate.observeForever(date -> setStartDateString(getDateString(date)));
        closeTime.observeForever(date -> setCloseTimeString(getTimeString(date)));
        closeDate.observeForever(date -> setCloseDateString(getDateString(date)));
    }
    private String getDateString(Date date)
    {
        LocalDateTime localDateTimeOpen = LocalDateTime.ofInstant(
                date.toInstant(),
                ZoneId.systemDefault()
        );
        int day = localDateTimeOpen.getDayOfMonth();
        int month = localDateTimeOpen.getMonthValue();
        int year = localDateTimeOpen.getYear();
        return day + "/" + month + "/" + year;
    }
    private String getTimeString(Date date)
    {
        LocalDateTime localDateTimeOpen = LocalDateTime.ofInstant(
                date.toInstant(),
                ZoneId.systemDefault()
        );
        int hour = localDateTimeOpen.getHour();
        int minute = localDateTimeOpen.getMinute();
        return String.format("%02d", hour) + ":" + String.format("%02d", minute);
    }
    public void updateParameter(Promo promo)
    {
        setPromoCode(promo.getPromoCode());

        DecimalFormat formatter = new DecimalFormat("###0.##");
        String minPrice = formatter.format(promo.getMinPrice());
        setMinPrice(minPrice);

        String maxPrice = formatter.format(promo.getMaxPrice());
        setMaxPrice(maxPrice);

        setPercent(formatter.format(promo.getPercent() * 100));

        setDescription(promo.getDescription());

        closeTime.postValue(promo.getDateEnd());
        closeDate.postValue(promo.getDateEnd());

        startTime.postValue(promo.getDateStart());
        startDate.postValue(promo.getDateStart());

        setForNewCustomerValue(promo.isForNewCustomer());

        setCanEditPromoCode(false);

        setButtonText("Lưu");
    }


    @Bindable
    private boolean keyBoardShow = false;

    public boolean isKeyBoardShow() {
        return keyBoardShow;
    }

    public void setKeyBoardShow(boolean keyBoardShow) {
        this.keyBoardShow = keyBoardShow;
        notifyPropertyChanged(BR.keyBoardShow);
    }


    @Bindable
    private String buttonText = "Tạo mã giảm giá";

    public String getButtonText() {
        return buttonText;
    }

    public void setButtonText(String buttonText) {
        this.buttonText = buttonText;
        notifyPropertyChanged(BR.buttonText);
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


    @Bindable
    private boolean updating = false;

    public boolean isUpdating() {
        return updating;
    }

    public void setUpdating(boolean updating) {
        this.updating = updating;
        notifyPropertyChanged(BR.updating);
    }


    private final MutableLiveData<Date> startTime = new MutableLiveData<>();

    public MutableLiveData<Date> getStartTime() {
        return startTime;
    }
    @Bindable
    private String startTimeString = "";

    public String getStartTimeString() {
        return startTimeString;
    }

    public void setStartTimeString(String startTimeString) {
        this.startTimeString = startTimeString;
        notifyPropertyChanged(BR.startTimeString);
    }


    private final MutableLiveData<Date> startDate = new MutableLiveData<>();

    public MutableLiveData<Date> getStartDate() {
        return startDate;
    }
    @Bindable
    private String startDateString = "";

    public String getStartDateString() {
        return startDateString;
    }

    public void setStartDateString(String startDateString) {
        this.startDateString = startDateString;
        notifyPropertyChanged(BR.startDateString);
    }


    private final MutableLiveData<Date> closeTime = new MutableLiveData<>();

    public MutableLiveData<Date> getCloseTime() {
        return closeTime;
    }
    @Bindable
    private String closeTimeString = "";

    public String getCloseTimeString() {
        return closeTimeString;
    }

    public void setCloseTimeString(String closeTimeString) {
        this.closeTimeString = closeTimeString;
        notifyPropertyChanged(BR.closeTimeString);
    }


    private final MutableLiveData<Date> closeDate = new MutableLiveData<>();

    public MutableLiveData<Date> getCloseDate() {
        return closeDate;
    }
    @Bindable
    private String closeDateString = "";

    public String getCloseDateString() {
        return closeDateString;
    }

    public void setCloseDateString(String closeDateString) {
        this.closeDateString = closeDateString;
        notifyPropertyChanged(BR.closeDateString);
    }


    @Bindable
    private String promoCode = "";

    public String getPromoCode() {
        return promoCode;
    }

    public void setPromoCode(String promoCode) {
        this.promoCode = promoCode;
        notifyPropertyChanged(BR.promoCode);
    }

    public void onPromoCodeChanged(CharSequence s, int start, int before, int count)
    {
        promoCode = s.toString();
    }


    @Bindable
    private String percent = "";

    public String getPercent() {
        return percent;
    }

    public void setPercent(String percent) {
        this.percent = percent;
        notifyPropertyChanged(BR.percent);
    }

    public void onPercentChanged(CharSequence s, int start, int before, int count)
    {
        percent = s.toString();
    }


    @Bindable
    private String minPrice = "";

    public String getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(String minPrice) {
        this.minPrice = minPrice;
        notifyPropertyChanged(BR.minPrice);
    }

    public void onMinPriceChanged(CharSequence s, int start, int before, int count) {
        minPrice = s.toString();
    }


    @Bindable
    private String maxPrice = "";

    public String getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(String maxPrice) {
        this.maxPrice = maxPrice;
        notifyPropertyChanged(BR.maxPrice);
    }

    public void onMaxPriceChanged(CharSequence s, int start, int before, int count)
    {
        maxPrice = s.toString();
    }


    @Bindable
    private boolean isForNewCustomerValue = false;

    public boolean isForNewCustomerValue() {
        return isForNewCustomerValue;
    }

    public void setForNewCustomerValue(boolean forNewCustomerValue) {
        isForNewCustomerValue = forNewCustomerValue;
        notifyPropertyChanged(BR.isForNewCustomerValue);
    }
    public void onForNewCustomerValueChanged(CompoundButton buttonView, boolean isChecked) {
        isForNewCustomerValue = isChecked;
    }


    @Bindable
    private String description = "";

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        notifyPropertyChanged(BR.description);
    }

    public void onDescriptionChanged(CharSequence s, int start, int before, int count)
    {
        description = s.toString();
    }


    @Bindable
    private boolean canEditPromoCode = true;

    public boolean isCanEditPromoCode() {
        return canEditPromoCode;
    }

    public void setCanEditPromoCode(boolean canEditPromoCode) {
        this.canEditPromoCode = canEditPromoCode;
        notifyPropertyChanged(BR.canEditPromoCode);
    }
}
