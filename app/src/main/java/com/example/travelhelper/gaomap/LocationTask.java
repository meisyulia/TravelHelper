package com.example.travelhelper.gaomap;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.model.MyLocationStyle;
import com.example.travelhelper.util.ToastUtil;

import java.util.HashMap;
import java.util.Map;

public class LocationTask implements AMapLocationListener {

    private static LocationTask locationTask;
    private final Context context;
    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption locationOption;

    public static LocationTask getInstance(Context context){
        if (locationTask == null) {
            synchronized (LocationTask.class){
                locationTask = new LocationTask(context);
            }
        }
        return locationTask;
    }

    private LocationTask(Context context){
        this.context = context;
    }

    //开启单次定位
    public void startSingleLocate() {
        //初始化定位
        try {
            mLocationClient = new AMapLocationClient(context);
            AMapLocationClientOption option=new AMapLocationClientOption();
            option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            option.setOnceLocation(true);
            mLocationClient.setLocationOption(option);
            mLocationClient.startLocation();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //开启连续定位
    public void startContinLacate(){
        try {
            mLocationClient = new AMapLocationClient(context);
            locationOption = new AMapLocationClientOption();
            // 设置定位模式为高精度模式
            locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            // 设置定位监听
            mLocationClient.setLocationListener(this);
            initOption();
            mLocationClient.setLocationOption(locationOption);
            mLocationClient.startLocation();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initOption() {
        // 设置是否需要显示地址信息
        locationOption.setNeedAddress(true);
        /**
         * 设置是否优先返回GPS定位结果，如果30秒内GPS没有返回定位结果则进行网络定位
         * 注意：只有在高精度模式下的单次定位有效，其他方式无效
         */
        //        locationOption.setGpsFirst(true);
        // 设置是否开启缓存
        locationOption.setLocationCacheEnable(true);
        // 设置发送定位请求的时间间隔,最小值为1000，如果小于1000，按照1000算
        locationOption.setInterval(5000);
        locationOption.setOnceLocation(true);
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation.getErrorCode() == 0) {

        }

    }

    //停止定位
    public void stopLocate() {
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
        mLocationClient = null;
    }
}
