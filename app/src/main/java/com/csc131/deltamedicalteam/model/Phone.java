package com.csc131.deltamedicalteam.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Phone implements Parcelable {
    private long residence;
    private long mobile;
    public Phone() {
        // Default constructor required for Firestore deserialization
    }
    public Phone(long residence, long mobile) {
        this.residence = residence;
        this.mobile = mobile;
    }

    protected Phone(Parcel in) {
        residence = in.readLong();
        mobile = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(residence);
        dest.writeLong(mobile);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Phone> CREATOR = new Creator<Phone>() {
        @Override
        public Phone createFromParcel(Parcel in) {
            return new Phone(in);
        }

        @Override
        public Phone[] newArray(int size) {
            return new Phone[size];
        }
    };

    public long getResidence() {
        return residence;
    }

    public void setResidence(long residence) {
        this.residence = residence;
    }

    public long getMobile() {
        return mobile;
    }

    public void setMobile(long mobile) {
        this.mobile = mobile;
    }
}


