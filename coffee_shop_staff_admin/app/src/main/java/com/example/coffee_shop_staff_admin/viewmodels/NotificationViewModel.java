package com.example.coffee_shop_staff_admin.viewmodels;

import android.net.Uri;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.lifecycle.MutableLiveData;

import com.example.coffee_shop_staff_admin.BR;

public class NotificationViewModel extends BaseObservable {
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
    private boolean hasImage = false;

    public boolean isHasImage() {
        return hasImage;
    }

    public void setHasImage(boolean hasImage) {
        this.hasImage = hasImage;
        notifyPropertyChanged(BR.hasImage);
    }

    @Bindable
    private boolean loading = false;

    public boolean isLoading() {
        return loading;
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
        notifyPropertyChanged(BR.loading);
    }

    @Bindable
    private String title = "";

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        notifyPropertyChanged(BR.title);
    }

    public void onTitleChanged(CharSequence s, int start, int before, int count)
    {
        title = s.toString();
    }


    @Bindable
    private String content = "";

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
        notifyPropertyChanged(BR.content);
    }

    public void onContentChanged(CharSequence s, int start, int before, int count)
    {
        content = s.toString();
    }

    private final MutableLiveData<Uri> imageSource = new MutableLiveData<>();

    public MutableLiveData<Uri> getImageSource() {
        return imageSource;
    }
}
