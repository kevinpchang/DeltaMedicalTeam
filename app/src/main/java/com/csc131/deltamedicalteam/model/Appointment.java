package com.csc131.deltamedicalteam.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Map;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Appointment implements Parcelable {

    private String patientDocumentId;
    private String userDocumentId;
    private String purpose;
    private String time;
    private String date;


    public Appointment() {
        // Default constructor required for Firestore
    }

    public Appointment(String patientDocumentId, String userDocumentId, String purpose, String time, String date) {
        this.patientDocumentId = patientDocumentId;
        this.userDocumentId = userDocumentId;
        this.purpose = purpose;
        this.time = time;
        this.date = date;

    }

    protected Appointment(Parcel in) {
        patientDocumentId = in.readString();
        userDocumentId = in.readString();
        purpose = in.readString();
        time = in.readString();
        date = in.readString();

    }

    public static final Creator<Appointment> CREATOR = new Creator<Appointment>() {
        @Override
        public Appointment createFromParcel(Parcel in) {
            return new Appointment(in);
        }

        @Override
        public Appointment[] newArray(int size) {
            return new Appointment[size];
        }
    };

    public String getPatientDocumentId() {
        return patientDocumentId;
    }

    public String getUserDocumentId() {
        return userDocumentId;
    }

    public String getPurpose() {
        return purpose;
    }

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }


//    public void fromDocumentSnapshot(DocumentSnapshot document) {
//        patient_id = document.getString("patient_id");
//        user_id = document.getString("user_id");
//        purpose = document.getString("purpose");
//        time = document.getString("time");
//
//    }
//
//    // Add method to convert to Map for Firestore
//    public Map<String, Object> toMap() {
//        // Convert all fields to a Map
//        // You need to implement this according to your document structure
//        return null;
//    }

    // Parcelable methods
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(patientDocumentId);
        dest.writeString(userDocumentId);
        dest.writeString(purpose);
        dest.writeString(time);
        dest.writeString(date);
    }

    @Override
    public int describeContents() {
        return 0;
    }

}



