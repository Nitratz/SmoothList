package com.list.smoothlist.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.ByteArrayOutputStream;

public class ToDo implements Parcelable {

    private int mId;

    private short isDone;
    private Bitmap mBanner;
    private boolean mFromDB;
    private int mLevelNb;
    private String mDesc;
    private String mTitle;
    private String mDate;

    public ToDo() {
        mBanner = null;
        mFromDB = false;
        mId = -1;
        isDone = 0;
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

    public ToDo setBanner(Bitmap mBanner) {
        this.mBanner = mBanner;
        return this;
    }

    public ToDo setBannerFromArray(byte[] array) {
        if (array != null)
            mBanner = BitmapFactory.decodeByteArray(array, 0, array.length);
        return this;
    }

    public Bitmap getBanner() {
        return mBanner;
    }

    public byte[] getBannerArray() {
        if (mBanner == null)
            return null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        mBanner.compress(Bitmap.CompressFormat.PNG, 100, bos);
        return bos.toByteArray();
    }

    protected ToDo(Parcel in) {
        mId = in.readInt();
        isDone = (short) in.readValue(short.class.getClassLoader());
        mBanner = (Bitmap) in.readValue(Bitmap.class.getClassLoader());
        mFromDB = in.readByte() != 0x00;
        mLevelNb = in.readInt();
        mDesc = in.readString();
        mTitle = in.readString();
        mDate = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeValue(isDone);
        dest.writeValue(mBanner);
        dest.writeByte((byte) (mFromDB ? 0x01 : 0x00));
        dest.writeInt(mLevelNb);
        dest.writeString(mDesc);
        dest.writeString(mTitle);
        dest.writeString(mDate);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<ToDo> CREATOR = new Parcelable.Creator<ToDo>() {
        @Override
        public ToDo createFromParcel(Parcel in) {
            return new ToDo(in);
        }

        @Override
        public ToDo[] newArray(int size) {
            return new ToDo[size];
        }
    };
}