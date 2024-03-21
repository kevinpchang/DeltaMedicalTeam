package com.csc131.deltamedicalteam.model;

import android.graphics.drawable.Drawable;

public class User {

    public int image;
    public Drawable imageDrw;
    public String name;
    public String email;
    public String phonenumber;
    public String permission;
    public boolean section = false;

    public User() {
    }

    public User(String name, String email, String phonenumber, String permission, boolean section) {
        this.name = name;
        this.email = email;
        this.phonenumber = phonenumber;
        this.permission = permission;
        this.section = section;
    }

}
