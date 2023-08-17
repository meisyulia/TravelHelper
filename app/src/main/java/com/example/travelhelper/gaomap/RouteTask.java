package com.example.travelhelper.gaomap;

import android.content.Context;
import android.util.Log;

import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.example.travelhelper.bean.PositionBean;

import java.util.ArrayList;
import java.util.List;

public class RouteTask implements RouteSearch.OnRouteSearchListener {

    private static final String TAG = "RouteTask";
    private static RouteTask mRouteTask;

    private RouteSearch mRouteSearch;

    private PositionBean mFromPoint;

    private PositionBean mToPoint;

    private List<OnRouteCalculateListener> mListeners = new ArrayList<OnRouteCalculateListener>();
    private DrivePath drivepath;

    public interface OnRouteCalculateListener {
        public void onRouteCalculate(float cost, float distance, int duration,DrivePath drivepath);

    }

    public static RouteTask getInstance(Context context) {
        if (mRouteTask == null) {
            mRouteTask = new RouteTask(context);
        }
        return mRouteTask;
    }

    public PositionBean getStartPoint() {
        return mFromPoint;
    }

    public void setStartPoint(PositionBean fromPoint) {
        mFromPoint = fromPoint;
    }

    public PositionBean getEndPoint() {
        return mToPoint;
    }

    public void setEndPoint(PositionBean toPoint) {
        mToPoint = toPoint;
    }

    private RouteTask(Context context) {
        try {
            mRouteSearch = new RouteSearch(context);
        } catch (AMapException e) {
            e.printStackTrace();
            Log.i(TAG, "RouteTask: ed=>"+e);
        }
        mRouteSearch.setRouteSearchListener(this);
    }

    public void search() {
        if (mFromPoint == null || mToPoint == null) {
            return;
        }
        Log.e(":", "search: "+mFromPoint+","+mToPoint);

        RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(new LatLonPoint(mFromPoint.getLatitude(),
                mFromPoint.getLongitude()), new LatLonPoint(mToPoint.getLatitude(),
                mToPoint.getLongitude()));
        RouteSearch.DriveRouteQuery driveRouteQuery = new RouteSearch.DriveRouteQuery(fromAndTo,
                RouteSearch.DrivingDefault, null, null, "");

        mRouteSearch.calculateDriveRouteAsyn(driveRouteQuery);
    }

    public void search(PositionBean fromPoint, PositionBean toPoint) {

        mFromPoint = fromPoint;
        mToPoint = toPoint;
        search();

    }

    public void addRouteCalculateListener(OnRouteCalculateListener listener) {
        synchronized (this) {
            if (mListeners.contains(listener))
                return;
            mListeners.add(listener);
        }
    }

    public void removeRouteCalculateListener(OnRouteCalculateListener listener) {
        synchronized (this) {
            mListeners.remove(listener);
        }
    }


    //驾车路线规划回调
    @Override
    public void onDriveRouteSearched(DriveRouteResult driveRouteResult,
                                     int resultCode) {
        if (resultCode == 1000 && driveRouteResult != null) {
            synchronized (this) {
                for (OnRouteCalculateListener listener : mListeners) {

                    List<DrivePath> drivepaths = driveRouteResult.getPaths();
                    float distance = 0;
                    int duration = 0;
                    if (drivepaths.size() > 0) {
                        drivepath = drivepaths.get(0);

                        distance = drivepath.getDistance() / 1000; //getDistance单位：米

                        duration = (int) (drivepath.getDuration() / 60); //getDuration单位：秒
                    }

                    float cost = driveRouteResult.getTaxiCost();

                    listener.onRouteCalculate(cost, distance, duration,drivepath);
                }
                List<DrivePath> paths = driveRouteResult.getPaths();


            }
        }
        //这里可以根据需求对查询错误情况进行相应的提示或者逻辑处理
    }
    @Override
    public void onWalkRouteSearched(WalkRouteResult arg0, int arg1) {}
    @Override
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {}
    @Override
    public void onBusRouteSearched(BusRouteResult arg0, int arg1) {}
}

