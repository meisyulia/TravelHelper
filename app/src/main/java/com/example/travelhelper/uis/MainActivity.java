package com.example.travelhelper.uis;

import static com.example.travelhelper.constant.FuncType.ROUTE_PLANNING;
import static com.example.travelhelper.constant.FuncType.TRANS_SEARCH;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
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

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends BaseActivity implements OnItemClickListener, AMapLocationListener {

    private static final String TAG = "MainActivity";
    private com.example.travelhelper.databinding.ActivityMainBinding mBinding;
    private static final int REQUEST_PERMISSIONS = 9527;
    private AMap aMap;
    private ArrayList<FunInfo> mFunInfos;
    private MyLocationStyle myLocationStyle;
    private double latitude;
    private double longitude;
    private String city;
    private String formatAddress;
    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        initMap(savedInstanceState);
        initLocation();
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
        aMap.showIndoorMap(true);
    }

    protected String[] permissions = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
    };

    @Override
    protected void initData() {
        //checkAllPermission();
        //requestPermission();
        PermissionUtil.checkPermissions(this,permissions);
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

   /* private void myLocation() {
        aMap.showIndoorMap(true);
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.interval(5000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        //aMap.getUiSettings().setMyLocationButtonEnabled(true); //设置默认定位按钮是否显示，非必需设置。
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        //设置定位消息监听
        aMap.setOnMyLocationChangeListener(this);
    }*/


    /**
     * 初始化定位
     */
    private void initLocation() {
        //初始化定位
        try {
            mLocationClient = new AMapLocationClient(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mLocationClient != null) {
            //设置定位回调监听
            mLocationClient.setLocationListener(this);
            //初始化AMapLocationClientOption对象
            mLocationOption = new AMapLocationClientOption();
            //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //获取最近3s内精度最高的一次定位结果：
            //设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
            mLocationOption.setOnceLocationLatest(true);
            //设置是否返回地址信息（默认返回地址信息）
            mLocationOption.setNeedAddress(true);
            //设置定位请求超时时间，单位是毫秒，默认30000毫秒，建议超时时间不要低于8000毫秒。
            mLocationOption.setHttpTimeOut(20000);
            //关闭缓存机制，高精度定位会产生缓存。
            mLocationOption.setLocationCacheEnable(false);
            //给定位客户端对象设置定位参数
            mLocationClient.setLocationOption(mLocationOption);
        }
    }

    private void checkAllPermission() {
        String[] permissions;
        /*if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU){
            permissions = new String[]{
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_AUDIO,
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.CHANGE_WIFI_STATE,
                    Manifest.permission.FOREGROUND_SERVICE,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
            };
        }else{
            permissions = new String[]{
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
            };
        }*/

        permissions = new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };
        boolean isPermission = PermissionUtil.checkMultiPermission(this,permissions, REQUEST_PERMISSIONS);
        if (!isPermission){
            Toast.makeText(this, "需要允许权限才能正常使用哦", Toast.LENGTH_SHORT).show();
        }else{
            mLocationClient.startLocation();
        }
    }

    @AfterPermissionGranted(REQUEST_PERMISSIONS)
    private void requestPermission(){
       String[] permissions = new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
       if (EasyPermissions.hasPermissions(this,permissions)){
           //

       }else{
           Toast.makeText(this, "需要权限才能正常使用哦", Toast.LENGTH_SHORT).show();
       }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //设置权限请求结果
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mBinding.map.onDestroy();
        mLocationClient.stopLocation();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mBinding.map.onResume();
        //myLocation();
        //checkAllPermission();
        mLocationClient.startLocation();
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
    /*@Override
    public void onMyLocationChange(Location location) {
        // 定位回调监听
        if(location != null) {
            Log.e("amap", "onMyLocationChange 定位成功， lat: " + location.getLatitude() + " lon: " + location.getLongitude());
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            Bundle bundle = location.getExtras();
            if(bundle != null) {
                int errorCode = bundle.getInt(MyLocationStyle.ERROR_CODE);
                String errorInfo = bundle.getString(MyLocationStyle.ERROR_INFO);
                // 定位类型，可能为GPS WIFI等，具体可以参考官网的定位SDK介绍
                int locationType = bundle.getInt(MyLocationStyle.LOCATION_TYPE);
                Log.e("amap", "定位信息， code: " + errorCode + " errorInfo: " + errorInfo + " locationType: " + locationType );
            } else {
                Log.e("amap", "定位信息， bundle is null ");

            }
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
    }*/

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

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode()==0){
                city = aMapLocation.getCity();
                formatAddress = aMapLocation.getAddress();
                mBinding.tvAddress.setText("当前定位："+formatAddress);
                Log.i(TAG, "onLocationChanged: city="+city);
                SharedUtil.getInstance(MainActivity.this).writeShared("city",city);
                SharedUtil.getInstance(MainActivity.this).writeShared("address",formatAddress);
            }else{
                //定位失败：
                mBinding.tvAddress.setText("定位失败："+aMapLocation.getErrorCode());
            }
        }else{
            mBinding.tvAddress.setText("无法进行定位！");
        }
    }
}