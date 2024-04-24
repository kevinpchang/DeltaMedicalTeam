package com.csc131.deltamedicalteam.model;

import static android.content.ContentValues.TAG;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;

public class Appointment implements Parcelable {

    private String patientDocumentId;
    private String userDocumentId;
    private String purpose;
    private String time;
    private String date;

    private String appointmentId; // Add this field


    public Appointment() {
        // Default constructor required for Firestore
    }

    public Appointment(String appointmentId,String patientDocumentId, String userDocumentId, String purpose, String time, String date) {
        this.appointmentId = appointmentId;
        this.patientDocumentId = patientDocumentId;
        this.userDocumentId = userDocumentId;
        this.purpose = purpose;
        this.time = time;
        this.date = date;

    }

    public Appointment(String patientDocumentId, String userDocumentId, String purpose, String time, String date) {
        this.appointmentId = appointmentId;
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

    // Getter method for appointmentId
    public String getAppointmentId() {
        return appointmentId;
    }


    public void fromDocumentSnapshot(DocumentSnapshot document) {
        // Check if the DocumentSnapshot is valid and contains data
        if (document != null && document.exists()) {
            // Retrieve the appointment ID from the DocumentSnapshot
            appointmentId = document.getId();

            // Check if the appointment ID is not null or empty
            if (appointmentId != null && !appointmentId.isEmpty()) {
                // Log the retrieved appointment ID for debugging
                Log.d(TAG, "Appointment ID retrieved: " + appointmentId);
            } else {
                // Log an error message if the appointment ID is null or empty
                Log.e(TAG, "Appointment ID is null or empty");
            }

            // Retrieve other appointment fields as before
            patientDocumentId = document.getString("patientDocumentId");
            userDocumentId = document.getString("userDocumentId");
            purpose = document.getString("purpose");
            time = document.getString("time");
            date = document.getString("date");
        } else {
            // Log an error message if the DocumentSnapshot is invalid or empty
            Log.e(TAG, "Invalid or empty DocumentSnapshot");
        }
    }
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



