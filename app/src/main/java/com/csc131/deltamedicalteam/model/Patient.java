package com.csc131.deltamedicalteam.model;

import android.graphics.drawable.Drawable;

public class Patient {

    public int image;
    public Drawable imageDrw;
    public String name;
    public String phonenumber;
    public String address;
    public boolean section = false;

    public Patient() {
    }

    public Patient(String name, String phonenumber, String address, boolean section) {
        this.name = name;
        this.phonenumber = phonenumber;
        this.address = address;
        this.section = section;
    }

}
