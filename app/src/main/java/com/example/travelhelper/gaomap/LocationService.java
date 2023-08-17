package com.example.travelhelper.gaomap;

import android.content.Context;
import android.location.Location;

import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;

import java.util.HashMap;
import java.util.Map;

public class LocationService {
    private static LocationService instance;
    private GeocodeSearch geocodeSearch;
    private Map<LatLonPoint, RegeocodeAddress> cache;

    private LocationService(Context context) {
        try {
            geocodeSearch = new GeocodeSearch(context);
            cache = new HashMap<>();
            geocodeSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
                @Override
                public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
                    if (rCode == AMapException.CODE_AMAP_SUCCESS) {
                        if (result != null && result.getRegeocodeAddress() != null
                                && result.getRegeocodeAddress().getCity() != null) {
                            String city = result.getRegeocodeAddress().getCity();
                            RegeocodeAddress regeocodeAddress = result.getRegeocodeAddress();
                            cache.put(result.getRegeocodeQuery().getPoint(), regeocodeAddress);
                            // 在这里处理获取到的城市信息
                        }
                    }
                }

                @Override
                public void onGeocodeSearched(GeocodeResult result, int rCode) {

                }
            });
        } catch (AMapException e) {
            e.printStackTrace();
        }

    }

    public static LocationService getInstance(Context context) {
        if (instance == null) {
            instance = new LocationService(context);
        }
        return instance;
    }

    public void getCityByLocation(Location location, OnGetCityListener listener) {
        LatLonPoint latLonPoint = new LatLonPoint(location.getLatitude(), location.getLongitude());
        RegeocodeAddress regeocodeAddress = cache.get(latLonPoint);
        if (regeocodeAddress != null) {
            listener.onGetCity(regeocodeAddress);
        } else {
            RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200, GeocodeSearch.AMAP);
            geocodeSearch.getFromLocationAsyn(query);
        }
    }

    public interface OnGetCityListener {
        void onGetCity(RegeocodeAddress regeocodeAddress);
    }
}

