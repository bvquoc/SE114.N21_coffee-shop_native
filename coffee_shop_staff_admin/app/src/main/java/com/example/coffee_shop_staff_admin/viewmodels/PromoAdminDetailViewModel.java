package com.example.coffee_shop_staff_admin.viewmodels;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.example.coffee_shop_staff_admin.BR;

public class PromoAdminDetailViewModel extends BaseObservable {
    @Bindable
    private boolean loadingPromo = true;

    public boolean isLoadingPromo() {
        return loadingPromo;
    }

    public void setLoadingPromo(boolean loadingPromo) {
        this.loadingPromo = loadingPromo;
        notifyPropertyChanged(BR.loadingPromo);
    }

    @Bindable
    private boolean loadingStore = true;

    public boolean isLoadingStore() {
        return loadingStore;
    }

    public void setLoadingStore(boolean loadingStore) {
        this.loadingStore = loadingStore;
        notifyPropertyChanged(BR.loadingStore);
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

    @Bindable
    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
        notifyPropertyChanged(BR.code);
    }

    @Bindable
    private String dateStart;

    public String getDateStart() {
        return dateStart;
    }

    public void setDateStart(String dateStart) {
        this.dateStart = dateStart;
        notifyPropertyChanged(BR.dateStart);
    }

    @Bindable
    private String dateEnd;

    public String getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(String dateEnd) {
        this.dateEnd = dateEnd;
        notifyPropertyChanged(BR.dateEnd);
    }

    @Bindable
    private String minPrice;

    public String getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(String minPrice) {
        this.minPrice = minPrice;
        notifyPropertyChanged(BR.minPrice);
    }

    @Bindable
    private String maxPrice;

    public String getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(String maxPrice) {
        this.maxPrice = maxPrice;
        notifyPropertyChanged(BR.maxPrice);
    }

    @Bindable
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        notifyPropertyChanged(BR.description);
    }

    @Bindable
    private boolean forNewCustomer;

    public boolean isForNewCustomer() {
        return forNewCustomer;
    }

    public void setForNewCustomer(boolean forNewCustomer) {
        this.forNewCustomer = forNewCustomer;
        notifyPropertyChanged(BR.forNewCustomer);
        if(forNewCustomer)
        {
            setStatusText("Khách hàng mới");
        }
        else
        {
            setStatusText("Tất cả khách hàng");
        }
    }

    @Bindable
    private String statusText;

    public String getStatusText() {
        return statusText;
    }

    private void setStatusText(String statusText) {
        this.statusText = statusText;
        notifyPropertyChanged(BR.statusText);
    }


    @Bindable
    private boolean canEdit;

    public boolean isCanEdit() {
        return canEdit;
    }

    public void setCanEdit(boolean canEdit) {
        this.canEdit = canEdit;
        notifyPropertyChanged(BR.canEdit);
    }
}
