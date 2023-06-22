package com.example.coffee_shop_app.utils;


import com.google.android.gms.maps.model.LatLng;

public class LocationHelper {
    //Singleton
    private static LocationHelper instance;
    public static LocationHelper getInstance()
    {
        if(instance == null)
        {
            instance = new LocationHelper();
        }
        return instance;
    }

    //Property
    private LatLng currentLocation;

    public LatLng getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(LatLng currentLocation) {
        this.currentLocation = currentLocation;
    }

    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2)
    {
        double p = 0.017453292519943295;
        double a = 0.5 -
                Math.cos((lat2 - lat1) * p) / 2 +
                Math.cos(lat1 * p) * Math.cos(lat2 * p) *
                        (1 - Math.cos((lon2 - lon1) * p)) / 2;
        return 12742 * Math.asin(Math.sqrt(a));
    }

    public static double calculateDistance(LatLng latLng1, LatLng latLng2)
    {
        double p = 0.017453292519943295;
        double a = 0.5 -
                Math.cos((latLng2.latitude - latLng1.latitude) * p) / 2 +
                Math.cos(latLng1.latitude * p) * Math.cos(latLng2.latitude * p) *
                        (1 - Math.cos((latLng2.longitude - latLng1.longitude) * p)) / 2;
        return 12742 * Math.asin(Math.sqrt(a));
    }
    private LocationHelper()
    {
        currentLocation = null;
    }
}
