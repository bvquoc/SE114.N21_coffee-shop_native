package com.example.coffee_shop_app.viewmodels;

import android.provider.ContactsContract;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.lifecycle.MutableLiveData;

import com.example.coffee_shop_app.BR;
import com.example.coffee_shop_app.models.Notification;
import com.example.coffee_shop_app.repository.NotificationRepository;

import java.util.Date;
import java.util.List;

public class NotificationViewModel extends BaseObservable {
    //Singleton
    private static NotificationViewModel instance;
    public static NotificationViewModel getInstance()
    {
        if(instance == null)
        {
            instance = new NotificationViewModel();
        }
        return instance;
    }
    private NotificationViewModel() {
        NotificationRepository.getInstance().getNotificationListMutableLiveData().observeForever(notifications -> {
            NotificationViewModel.getInstance().setLoading(true);
            if(notifications!=null)
            {
                Date lastDateSeeNoti = getLastTimeSeeNotification().getValue();
                if(lastDateSeeNoti!=null)
                {
                    int numberNotiNotSee = 0;
                    for(Notification notification: notifications) {
                        if(notification.getDateNoti().after(lastDateSeeNoti))
                        {
                            numberNotiNotSee++;
                        }
                    }
                    if(numberNotiNotSee!=0)
                    {
                        setNumberNotiNotRead(numberNotiNotSee);
                        setNumberNotiNotReadString(String.valueOf(numberNotiNotSee));
                    }
                    else
                    {
                        setNumberNotiNotRead(0);
                        setNumberNotiNotReadString("0");
                    }
                }
                else
                {
                    setNumberNotiNotRead(notifications.size());
                    setNumberNotiNotReadString(String.valueOf(notifications.size()));
                }
                listNotificationMutableLiveData.postValue(notifications);
                NotificationViewModel.getInstance().setLoading(false);
            }
        });

        getLastTimeSeeNotification().observeForever(date -> {
            List<Notification> notifications = NotificationRepository.getInstance().getNotificationListMutableLiveData().getValue();
            if(notifications!=null && date!=null)
            {
                int numberNotiNotSee = 0;
                for(Notification notification: notifications) {
                    if(notification.getDateNoti().after(date))
                    {
                        numberNotiNotSee++;
                    }
                }
                if(numberNotiNotSee!=0)
                {
                    setNumberNotiNotRead(0);
                    setNumberNotiNotReadString(String.valueOf(numberNotiNotSee));
                }
                else
                {
                    setNumberNotiNotRead(0);
                    setNumberNotiNotReadString("0");
                }
            }
        });
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

    private final MutableLiveData<Date> lastTimeSeeNotification = new MutableLiveData<>();

    public MutableLiveData<Date> getLastTimeSeeNotification() {
        return lastTimeSeeNotification;
    }


    @Bindable
    private int numberNotiNotRead = 0;

    public int getNumberNotiNotRead() {
        return numberNotiNotRead;
    }

    public void setNumberNotiNotRead(int numberNotiNotRead) {
        this.numberNotiNotRead = numberNotiNotRead;
        notifyPropertyChanged(BR.numberNotiNotRead);
    }

    @Bindable
    private String numberNotiNotReadString = "0";

    public String getNumberNotiNotReadString() {
        return numberNotiNotReadString;
    }

    public void setNumberNotiNotReadString(String numberNotiNotReadString) {
        this.numberNotiNotReadString = numberNotiNotReadString;
        notifyPropertyChanged(BR.numberNotiNotReadString);
    }

    private final MutableLiveData<List<Notification>> listNotificationMutableLiveData = new MutableLiveData<>();

    public MutableLiveData<List<Notification>> getListNotificationMutableLiveData() {
        return listNotificationMutableLiveData;
    }
}
