package com.example.travelhelper.gaomap;

import android.content.Context;
import android.util.Log;

import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.PoiItem;

import com.amap.api.services.poisearch.PoiResult;

import com.amap.api.services.poisearch.PoiSearch;

import com.example.travelhelper.adapter.RecomandAdapter;
import com.example.travelhelper.bean.PositionBean;

import java.util.ArrayList;

public class PoiSearchTask implements PoiSearch.OnPoiSearchListener {

    private static final String TAG = "PoiSearchTask";
    private final Context mContext;
    private final RecomandAdapter mRecommadAdapter;

    public PoiSearchTask(Context context, RecomandAdapter recomandAdapter){
        mContext = context;
        mRecommadAdapter = recomandAdapter;
    }
    public void search(String keyword,String city){
        PoiSearch.Query query = new PoiSearch.Query(keyword, "", city);
        query.setPageSize(10);
        query.setPageNum(0);
        try {
            PoiSearch poiSearch = new PoiSearch(mContext, query);
            poiSearch.setOnPoiSearchListener(this);
            poiSearch.searchPOIAsyn();
        } catch (AMapException e) {
            e.printStackTrace();
            Log.i(TAG, "search: poiSearch->e="+e);
        }
    }
    @Override
    public void onPoiSearched(PoiResult result, int rCode) {
        if (rCode == AMapException.CODE_AMAP_SUCCESS && result!=null){
            ArrayList<PoiItem> pois = result.getPois();
            if (pois == null) {
                return;
            }
            ArrayList<PositionBean> positionBeans = new ArrayList<>();
            for (PoiItem poiItem : pois) {
                //Log.i(TAG, "onPoiSearched: poiItem.getTitle="+poiItem.getTitle()+",getSnippet"+poiItem.getSnippet());
                PositionBean positionBean = new PositionBean(poiItem.getLatLonPoint().getLatitude(), poiItem.getLatLonPoint().getLongitude(),
                        poiItem.getTitle(), poiItem.getSnippet(),poiItem.getAdName(), poiItem.getCityName());
                positionBeans.add(positionBean);
            }
        }
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }
}
