package com.example.coffee_shop_app.viewmodels;

import android.os.Handler;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.lifecycle.MutableLiveData;

import com.example.coffee_shop_app.BR;
import com.example.coffee_shop_app.models.MLocation;
import com.example.coffee_shop_app.repository.LocationAPI;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapViewModel extends BaseObservable {
    public MapViewModel(){}
    private Handler handler = new Handler();
    private Runnable searchRunable;
    @Bindable
    private Boolean isLoading = true;

    public Boolean getIsLoading() {
        return isLoading;
    }

    public void setIsLoading(Boolean isLoading) {
        this.isLoading = isLoading;
        notifyPropertyChanged(BR.isLoading);
    }

    @Bindable
    private String formattedAddress = "Không thể tìm thấy địa chỉ";

    public String getFormattedAddress() {
        return formattedAddress;
    }

    public void setFormattedAddress(String formattedAddress) {
        this.formattedAddress = formattedAddress;
        notifyPropertyChanged(BR.formattedAddress);
    }

    @Bindable
    private Boolean isExistLocation = false;

    public Boolean getIsExistLocation() {
        return isExistLocation;
    }
    public void setIsExistLocation(Boolean isExistLocation) {
        this.isExistLocation = isExistLocation;
        notifyPropertyChanged(BR.isExistLocation);
    }

    @Bindable
    private MutableLiveData<LatLng> selectedLatLng = new MutableLiveData<>();

    public MutableLiveData<LatLng> getSelectedLatLng() {
        return selectedLatLng;
    }

    @Bindable
    private String searchText;

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
        notifyPropertyChanged(BR.searchText);
    }

    public void onSearchTextChanged(CharSequence s, int start, int before, int count)
    {
        if (searchRunable!=null)
        {
            handler.removeCallbacks(searchRunable);
        }
        searchText = s.toString();
        if(searchText.isEmpty())
        {
            suggestLocationList.postValue(new ArrayList<MLocation>());
            return;
        }

        searchRunable = new Runnable() {
            @Override
            public void run() {
                LocationAPI.getLocationResponse(searchText, new Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                        suggestLocationList.postValue(null);
                    }

                    @Override
                    public void onResponse(Response response) {
                        try
                        {
                            if(response.isSuccessful())
                            {
                                ResponseBody responseBody = response.body();
                                if(responseBody!=null)
                                {
                                    String responseJson = responseBody.string();

                                    Gson gson = new Gson();
                                    JsonObject responseObj = gson.fromJson(responseJson, JsonObject.class);
                                    JsonArray featuresArray = responseObj.getAsJsonArray("features");
                                    List<MLocation> locationList = new ArrayList<MLocation>();
                                    for(int i = 0; i < featuresArray.size(); i++)
                                    {
                                        JsonObject featureObj = featuresArray.get(i).getAsJsonObject();
                                        locationList.add(MLocation.fromJson(featureObj));
                                    }
                                    suggestLocationList.postValue(locationList);
                                    return;
                                }
                            }
                            suggestLocationList.postValue(null);
                        }
                        catch(Exception e)
                        {
                            suggestLocationList.postValue(null);
                        }
                    }
                });
            }
        };
        handler.postDelayed(searchRunable, 500);
    }

    MutableLiveData<List<MLocation>> suggestLocationList = new MutableLiveData<>(new ArrayList<MLocation>());

    public MutableLiveData<List<MLocation>> getSuggestLocationList() {
        return suggestLocationList;
    }
}
