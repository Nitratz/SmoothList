package com.list.smoothlist.model;

import android.graphics.drawable.Drawable;

public class ToDo {

    private int mId = -1;
    private boolean mSelected;

    private Drawable mLevel;
    private short isDone = 0;
    private boolean mFromDB;
    private int mLevelNb;
    private String mDesc;
    private String mTitle;
    private String mDate = "";

    public ToDo() { mFromDB = false; }

    public Drawable getLevel() {
        return mLevel;
    }

    public ToDo setLevel(Drawable mLevel) {
        this.mLevel = mLevel;
        return this;
    }

    public int getLevelNb() {
        return mLevelNb;
    }

    public ToDo setLevelNb(int mLevelNb) {
        this.mLevelNb = mLevelNb;
        return this;
    }

    public String getDesc() {
        return mDesc;
    }

    public ToDo setDesc(String mDesc) {
        this.mDesc = mDesc;
        return this;
    }

    public String getTitle() {
        return mTitle;
    }

    public ToDo setTitle(String mTitle) {
        this.mTitle = mTitle;
        return this;
    }

    public boolean isFromDB() {
        return mFromDB;
    }

    public ToDo setFromDB(boolean mFromDB) {
        this.mFromDB = mFromDB;
        return this;
    }

    public int getId() {
        return mId;
    }

    public short isDone() {
        return isDone;
    }

    public ToDo setId(int mId) {
        this.mId = mId;
        return this;
    }

    public ToDo setDone(short done) {
        isDone = done;
        return this;
    }

    public String getDate() {
        return mDate;
    }

    public ToDo setDate(String mDate) {
        this.mDate = mDate;
        return this;
    }

    public boolean isSelected() {
        return mSelected;
    }

    public void setSelected(boolean mSelected) {
        this.mSelected = mSelected;
    }
}
