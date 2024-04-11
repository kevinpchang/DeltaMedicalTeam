package com.csc131.deltamedicalteam.model;

import android.graphics.drawable.Drawable;

public class Appointment {

    public String patientId;
    public String date;
    public String time;
    public String purpose;


    public Appointment() {
        // Default constructor required for Firestore
    }

    public Appointment(String patientId, String date, String time, String purpose) {
        this.patientId = patientId;
        this.date = date;
        this.time = time;
        this.purpose = purpose;
    }

    public String getDate(){return date;}
    public String getTime(){return time;}
    public String getPurpose(){return purpose;}
    public String getPatient(){return patientId;}

    public void setDate(String date){
        this.date = date;
    }
    public void setTime(String time){
        this.time = time;
    }
    public void setPurpose(String purpose){
        this.purpose = purpose;
    }
    public void setPatient(String patientId){
        this.patientId = patientId;
    }


}
