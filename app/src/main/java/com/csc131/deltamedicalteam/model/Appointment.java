package com.csc131.deltamedicalteam.model;

import android.graphics.drawable.Drawable;

public class Appointment {

    public int image;
    public Drawable imageDrw;
    public String name;
    public String phonenumber;
    public String address;
    public boolean section = false;

    public Appointment() {
    }

    public Appointment(String name, String phonenumber, String address, boolean section) {
        this.name = name;
        this.phonenumber = phonenumber;
        this.address = address;
        this.section = section;
    }

}
