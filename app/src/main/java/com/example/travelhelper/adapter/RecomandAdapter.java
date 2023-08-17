package com.example.travelhelper.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelhelper.bean.PositionBean;
import com.example.travelhelper.databinding.ItemLayoutBinding;
import com.example.travelhelper.listener.RecomandItemClick;

import java.util.ArrayList;

public class RecomandAdapter extends RecyclerView.Adapter {

    private final Context context;
    private ArrayList<PositionBean> positionBeans;
    private RecomandItemClick recomandItemClick;

    public RecomandAdapter(Context context, ArrayList<PositionBean> positionBeans){
        this.context = context;
        this.positionBeans = positionBeans;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemLayoutBinding binding = ItemLayoutBinding.inflate(LayoutInflater.from(context),parent,false);
        return new ItemHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ItemHolder itemHolder = (ItemHolder) holder;
        PositionBean positionBean = positionBeans.get(position);
        if (positionBean!=null){
            itemHolder.mBinding.poiFieldId.setText(positionBean.getPos_name());
            itemHolder.mBinding.poiValueId.setText(positionBean.getPos_district()+" "+positionBean.getPos_address());
            itemHolder.mBinding.llItem.setOnClickListener(v->{
                if (recomandItemClick!=null){
                    recomandItemClick.onItemClick(position,positionBean);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return positionBeans!=null&&positionBeans.size()>0?positionBeans.size():0;
    }


    private class ItemHolder extends RecyclerView.ViewHolder {

        private final com.example.travelhelper.databinding.ItemLayoutBinding mBinding;

        public ItemHolder(ItemLayoutBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }
    }

    public void setDataList(ArrayList<PositionBean> positionBeans){
        this.positionBeans = positionBeans;
        notifyDataSetChanged();
    }

    public ArrayList<PositionBean> getDataList(){
        return this.positionBeans;
    }

    public PositionBean getItemData(int position){
        return this.positionBeans.get(position);
    }

    public void setOnItemClickListener(RecomandItemClick recomandItemClick){
        this.recomandItemClick = recomandItemClick;
    }
}
