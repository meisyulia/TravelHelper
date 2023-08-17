package com.example.travelhelper.gaomap;

import android.content.Context;
import android.util.Log;
import android.widget.SimpleAdapter;

import com.amap.api.services.core.AMapException;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.example.travelhelper.adapter.RecomandAdapter;
import com.example.travelhelper.bean.PositionBean;
import com.example.travelhelper.util.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.security.auth.login.LoginException;

public class InputTipTask implements Inputtips.InputtipsListener {
    private static final String TAG = "InputTipTask";
    private static InputTipTask mInputTipTask;
    private final Context mContext;
    private Inputtips mInputTips;
    private RecomandAdapter mAdapter;

    public static InputTipTask getInstance(Context context, RecomandAdapter adapter){
        if(mInputTipTask==null){
            mInputTipTask=new InputTipTask(context);
        }        //单例情况，多次进入DestinationActivity传进来的RecomandAdapter对象会不是同一个
        mInputTipTask.setRecommandAdapter(adapter);
        return mInputTipTask;
    }
    public void setRecommandAdapter(RecomandAdapter adapter){
        mAdapter=adapter;
    }
    private InputTipTask(Context context ){
        mContext = context;
       /* try {
            mInputTips=new Inputtips(context, this);
        } catch (AMapException e) {
            e.printStackTrace();
            Log.i(TAG, "InputTipTask: AMapException e="+e);
        }*/
    }
    public void searchTips(String keyWord, String city) {
       /* try {

            mInputTips.requestInputtips(keyWord, city);
        } catch (AMapException e) {
            e.printStackTrace();
        }*/
        InputtipsQuery inputtipsQuery = new InputtipsQuery(keyWord, city);
        inputtipsQuery.setCityLimit(true);
        mInputTips = new Inputtips(mContext, inputtipsQuery);
        mInputTips.setInputtipsListener(this);
        mInputTips.requestInputtipsAsyn();

    }

    @Override
    public void onGetInputtips(List<Tip> tipList, int rCode) {
        Log.i(TAG, "onGetInputtips: rCode="+rCode);
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            if(tipList != null) {
                int size = tipList.size();
                ArrayList<PositionBean> positionBeans = new ArrayList<>();
                for (int i = 0; i < size; i++) {
                    Tip tip = tipList.get(i);
                    if(tip != null) {
                        /*Log.i(TAG, "onGetInputtips: tip--->"+tip.toString());
                        Log.i(TAG, "onGetInputtips: tip.getAddress--->"+tip.getAddress()+",getPoint="+tip.getPoint());*/
                        //getAdcode是提示区域编码
                        positionBeans.add(new PositionBean(tip.getPoint().getLatitude(),tip.getPoint().getLongitude(),tip.getName(),tip.getAddress(),tip.getDistrict(),tip.getAdcode()));
                    }
                }
                mAdapter.setDataList(positionBeans);
                /*PoiSearchTask poiSearchTask = new PoiSearchTask(mContext.getApplicationContext(), mAdapter);
                for(int i = 0;i<positionBeans.size();i++){
                    PositionBean entity =mAdapter.getItemData(i);
                    poiSearchTask.search(entity.getPos_address(),RouteTask.getInstance(mContext.getApplicationContext()).getStartPoint().getCity());
                }*/
            }

        } else {
            ToastUtil.showerror(mContext.getApplicationContext(), rCode);
        }
    }
}
