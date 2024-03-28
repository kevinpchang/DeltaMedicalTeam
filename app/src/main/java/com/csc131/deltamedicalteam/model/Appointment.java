package com.csc131.deltamedicalteam.model;

import android.graphics.drawable.Drawable;

public class Appointment {

    public String patient;
    public String date;
    public String time;
    public String purpose;

    public Appointment() {
        // Default constructor required for Firestore
    }

    public Appointment(String patient, String date, String time, String purpose) {
        this.patient = patient;
        this.date = date;
        this.time = time;
        this.purpose = purpose;
    }

    public String getPatient(){return patient;}
    public String getDate(){return date;}
    public String getTime(){return time;}
    public String getPurpose(){return purpose;}

}
