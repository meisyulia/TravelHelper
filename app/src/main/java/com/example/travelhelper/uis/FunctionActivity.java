package com.example.travelhelper.uis;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.travelhelper.R;
import com.example.travelhelper.constant.FuncType;
import com.example.travelhelper.databinding.ActivityFunctionBinding;
import com.example.travelhelper.databinding.LayoutTopTitleBinding;
import com.example.travelhelper.uis.fragment.RouteFragment;
import com.example.travelhelper.uis.fragment.TransFragment;

public class FunctionActivity extends BaseActivity {

    private com.example.travelhelper.databinding.ActivityFunctionBinding mBinding;
    private int funcType;
    private String title;
    private com.example.travelhelper.databinding.LayoutTopTitleBinding mTitleBinding;
    private String city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_function);
        mBinding = ActivityFunctionBinding.inflate(getLayoutInflater());
        mTitleBinding = LayoutTopTitleBinding.bind(mBinding.getRoot());
        setContentView(mBinding.getRoot());
        initData();
        initView();
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        funcType = intent.getIntExtra("funcType",-1);
        title = intent.getStringExtra("title");
    }

    @Override
    protected void initView() {
        mTitleBinding.ivBack.setOnClickListener(v->{
            onBackPressed();
        });
        mTitleBinding.tvTitle.setText(title);
        if (funcType == FuncType.ROUTE_PLANNING){
            getSupportFragmentManager().beginTransaction().replace(R.id.fcv_function,new RouteFragment(),RouteFragment.class.getSimpleName()).commitAllowingStateLoss();
        }else if (funcType == FuncType.TRANS_SEARCH){
            getSupportFragmentManager().beginTransaction().replace(R.id.fcv_function,new TransFragment(),TransFragment.class.getSimpleName()).commitAllowingStateLoss();
        }
    }
}