package com.example.travelhelper.uis;

import static com.example.travelhelper.constant.FuncType.ROUTE_PLANNING;
import static com.example.travelhelper.constant.FuncType.TRANS_SEARCH;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.MapsInitializer;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.example.travelhelper.R;
import com.example.travelhelper.adapter.FunAdapter;
import com.example.travelhelper.bean.FunInfo;
import com.example.travelhelper.constant.FuncType;
import com.example.travelhelper.databinding.ActivityMainBinding;
import com.example.travelhelper.gaomap.LocationService;
import com.example.travelhelper.listener.OnItemClickListener;
import com.example.travelhelper.util.common.PermissionUtil;
import com.example.travelhelper.util.common.SharedUtil;
import com.example.travelhelper.util.common.StatusBarUtil;

import java.util.ArrayList;

public class MainActivity extends BaseActivity implements AMap.OnMyLocationChangeListener, OnItemClickListener {

    private static final String TAG = "MainActivity";
    private com.example.travelhelper.databinding.ActivityMainBinding mBinding;
    private AMap aMap;
    private ArrayList<FunInfo> mFunInfos;
    private MyLocationStyle myLocationStyle;
    private double latitude;
    private double longitude;
    private String city;
    private String formatAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        updatePrivacy();
        initMap(savedInstanceState);
        initData();
        initView();

    }

    private void initMap(Bundle savedInstanceState) {
        mBinding.map.onCreate(savedInstanceState);// 此方法必须重写
        aMap = mBinding.map.getMap();
        // 隐藏缩放控件
        aMap.getUiSettings().setZoomControlsEnabled(false);
        // 设置缩放级别
        aMap.moveCamera(CameraUpdateFactory.zoomTo(17));
    }

    @Override
    protected void initData() {
        checkAllPermission();
        mFunInfos = new ArrayList<>();
        mFunInfos.add(new FunInfo(R.drawable.route_planning,"路线规划", ROUTE_PLANNING));
        mFunInfos.add(new FunInfo(R.drawable.trans,"公交搜索",TRANS_SEARCH));
    }

    @Override
    protected void initView() {
        //StatusBarUtil.setImmersiveStatusBar(this,true);
        StatusBarUtil.setStatusBarColor(this,getResources().getColor(R.color.white));
        GridLayoutManager gLM = new GridLayoutManager(this, 2);
        mBinding.rvFunction.setLayoutManager(gLM);
        FunAdapter funAdapter = new FunAdapter(this, mFunInfos);
        mBinding.rvFunction.setAdapter(funAdapter);
        funAdapter.setOnItemClickListener(this);
    }

    private void myLocation() {
        aMap.showIndoorMap(true);
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.interval(5000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        //aMap.getUiSettings().setMyLocationButtonEnabled(true); //设置默认定位按钮是否显示，非必需设置。
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        //设置定位消息监听
        aMap.setOnMyLocationChangeListener(this);
    }


    private void updatePrivacy() {
        MapsInitializer.updatePrivacyShow(this,true,true);
        MapsInitializer.updatePrivacyAgree(this,true);
    }

    private void checkAllPermission() {
        boolean isPermission = PermissionUtil.checkMultiPermission(this, new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.FOREGROUND_SERVICE,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
        }, 4096);
        if (!isPermission){
            Toast.makeText(this, "需要允许权限才能正常使用哦", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mBinding.map.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mBinding.map.onResume();
        myLocation();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mBinding.map.onPause();
        aMap.setMyLocationEnabled(false); //不定位
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mBinding.map.onSaveInstanceState(outState);
    }

    @Override
    public void onMyLocationChange(Location location) {
        // 定位回调监听
        if(location != null) {
            Log.e("amap", "onMyLocationChange 定位成功， lat: " + location.getLatitude() + " lon: " + location.getLongitude());
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            /*Bundle bundle = location.getExtras();
            if(bundle != null) {
                int errorCode = bundle.getInt(MyLocationStyle.ERROR_CODE);
                String errorInfo = bundle.getString(MyLocationStyle.ERROR_INFO);
                // 定位类型，可能为GPS WIFI等，具体可以参考官网的定位SDK介绍
                int locationType = bundle.getInt(MyLocationStyle.LOCATION_TYPE);
                Log.e("amap", "定位信息， code: " + errorCode + " errorInfo: " + errorInfo + " locationType: " + locationType );
            } else {
                Log.e("amap", "定位信息， bundle is null ");

            }*/
            LocationService.getInstance(this).getCityByLocation(location, new LocationService.OnGetCityListener() {

                @Override
                public void onGetCity(RegeocodeAddress regeocodeAddress) {
                    city = regeocodeAddress.getCity();
                    formatAddress = regeocodeAddress.getFormatAddress();
                   // Log.i(TAG, "onGetCity: city="+ city +",formatAddress="+ formatAddress);
                    mBinding.tvAddress.setText("当前定位："+formatAddress);
                    SharedUtil.getInstance(MainActivity.this).writeShared("city",city);
                    SharedUtil.getInstance(MainActivity.this).writeShared("address",formatAddress);
                }
            });
        } else {
            Log.e("amap", "定位失败");
        }
    }

    @Override
    public void OnItemClick(int position) {
        FunInfo funInfo = mFunInfos.get(position);
        int funcType = funInfo.getFuncType();
        String title = funInfo.getTitle();
        Intent intent = new Intent(this, FunctionActivity.class);
        intent.putExtra("funcType",funcType);
        intent.putExtra("title",title);
        startActivity(intent);
    }
}