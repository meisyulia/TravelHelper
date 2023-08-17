package com.example.travelhelper.bean;

public class FunInfo {
    private int resId;
    private String title;
    private int funcType;

    public FunInfo(int resId, String title, int funcType) {
        this.resId = resId;
        this.title = title;
        this.funcType = funcType;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getFuncType() {
        return funcType;
    }

    public void setFuncType(int funcType) {
        this.funcType = funcType;
    }
}
