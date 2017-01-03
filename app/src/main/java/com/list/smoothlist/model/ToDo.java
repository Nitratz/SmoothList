package com.list.smoothlist.model;

import android.graphics.drawable.Drawable;

/**
 * Created by Backs on 03/01/2017.
 */

public class ToDo {

    private Drawable mLevel;
    private String mDesc;
    private String mTitle;

    public Drawable getLevel() {
        return mLevel;
    }

    public void setLevel(Drawable mLevel) {
        this.mLevel = mLevel;
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
}
