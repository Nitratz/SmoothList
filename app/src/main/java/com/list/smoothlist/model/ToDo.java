package com.list.smoothlist.model;

import android.graphics.drawable.Drawable;

public class ToDo {

    private int mId = -1;

    private Drawable mLevel;
    private boolean mFromDB;
    private int mLevelNb;
    private String mDesc;
    private String mTitle;

    public ToDo() { mFromDB = false; }

    public Drawable getLevel() {
        return mLevel;
    }

    public void setLevel(Drawable mLevel) {
        this.mLevel = mLevel;
    }

    public int getLevelNb() {
        return mLevelNb;
    }

    public void setLevelNb(int mLevelNb) {
        this.mLevelNb = mLevelNb;
    }

    public String getDesc() {
        return mDesc;
    }

    public void setDesc(String mDesc) {
        this.mDesc = mDesc;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }


    public boolean isFromDB() {
        return mFromDB;
    }

    public void setFromDB(boolean mFromDB) {
        this.mFromDB = mFromDB;
    }

    public int getId() {
        return mId;
    }

    public void setId(int mId) {
        this.mId = mId;
    }

}
