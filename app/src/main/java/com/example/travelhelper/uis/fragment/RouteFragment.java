package com.example.travelhelper.uis.fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelhelper.R;
import com.example.travelhelper.adapter.RecomandAdapter;
import com.example.travelhelper.bean.PositionBean;
import com.example.travelhelper.databinding.FragmentRouteBinding;
import com.example.travelhelper.gaomap.InputTipTask;
import com.example.travelhelper.gaomap.RouteTask;
import com.example.travelhelper.listener.RecomandItemClick;
import com.example.travelhelper.util.common.SharedUtil;

import java.util.ArrayList;

public class RouteFragment extends BaseFragment implements TextWatcher, RecomandItemClick, View.OnFocusChangeListener {

    private static final String TAG = "RouteFragment";
    private com.example.travelhelper.databinding.FragmentRouteBinding mBinding;
    private Context mContext;
    private ArrayList<PositionBean> mPositions;
    private RecomandAdapter mRecomandAdapter;
    private PositionBean mFromBean;
    private EditText mFocusView;
    private int whoIsFocus = -1;
    private String city;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = getContext();
        mBinding = FragmentRouteBinding.inflate(inflater,container,false);
        return mBinding.getRoot();
    }

    @Override
    protected void initData() {
        mPositions = new ArrayList<>();
        mFromBean = new PositionBean();
        /*mFromBean.setCity("广州");
        RouteTask.getInstance(mContext).setStartPoint(mFromBean);*/
    }

    @Override
    protected void initView() {
        mBinding.etFrom.addTextChangedListener(this);
        mBinding.etFrom.setOnFocusChangeListener(this);
        mBinding.etTo.addTextChangedListener(this);
        mBinding.etTo.setOnFocusChangeListener(this);
        LinearLayoutManager LLM = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        mBinding.rvRouteList.setLayoutManager(LLM);
        mRecomandAdapter = new RecomandAdapter(mContext, mPositions);
        mBinding.rvRouteList.setAdapter(mRecomandAdapter);
        mRecomandAdapter.setOnItemClickListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (TextUtils.isEmpty(charSequence)){
            mPositions = null;
            mRecomandAdapter.setDataList(mPositions);
        }
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (TextUtils.isEmpty(charSequence)){
            mPositions = null;
            mRecomandAdapter.setDataList(mPositions);
        }else{
            InputTipTask.getInstance(mContext.getApplicationContext(), mRecomandAdapter).searchTips(charSequence.toString(),
                    SharedUtil.getInstance(mContext).readShared("city",""));
        }

    }

    @Override
    public void afterTextChanged(Editable editable) {
    }

    @Override
    public void onItemClick(int position, PositionBean positionBean) {
        initInputText(positionBean);
    }

    private void initInputText(PositionBean positionBean) {
        if (whoIsFocus==1){
            mBinding.etFrom.setText(positionBean.getPos_name());
            RouteTask.getInstance(mContext).setStartPoint(positionBean);
        }else if (whoIsFocus==2){
            mBinding.etTo.setText(positionBean.getPos_name());
            RouteTask.getInstance(mContext).setEndPoint(positionBean);
        }
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        switch (view.getId()){
            case R.id.et_from:
                whoIsFocus = 1;
                break;
            case R.id.et_to:
                whoIsFocus = 2;
                break;
        }
    }

}
