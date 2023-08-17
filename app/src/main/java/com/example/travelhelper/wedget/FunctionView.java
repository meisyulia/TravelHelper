package com.example.travelhelper.wedget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.example.travelhelper.databinding.LayoutFunctionBinding;


public class FunctionView extends LinearLayout {


    private LayoutFunctionBinding mBinding;

    public FunctionView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initVew(context);
    }

    private void initVew(Context context) {
        mBinding = LayoutFunctionBinding.inflate(LayoutInflater.from(context),this,true);

    }

    public void initItem(String title,int resId){
        mBinding.tvFunTitle.setText(title);
        mBinding.ivFunIcon.setImageResource(resId);
    }

}
