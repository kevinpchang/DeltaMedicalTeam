package com.csc131.deltamedicalteam.model;

import static com.csc131.deltamedicalteam.utils.Tools.getPatientNameFromId;
import static com.csc131.deltamedicalteam.utils.Tools.getUserNameFromId;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.DocumentSnapshot;

public class Appointment implements Parcelable {

    private String patientDocumentId;
    private String userDocumentId;
    private String purpose;
    private String time;
    private String date;

    private String documentId; // Add this field


    public Appointment() {
        // Default constructor required for Firestore
    }

    public Appointment(String documentId,String patientDocumentId, String userDocumentId, String purpose, String time, String date) {
        this.documentId = documentId;
        this.patientDocumentId = patientDocumentId;
        this.userDocumentId = userDocumentId;
        this.purpose = purpose;
        this.time = time;
        this.date = date;

    }



    protected Appointment(Parcel in) {
        documentId = in.readString();
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

    public String getUserName() {

        return getUserNameFromId(this.userDocumentId);
    }

    public String getPatientName() {
        return getPatientNameFromId(this.patientDocumentId);
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

    // Getter method for documentId
    public String getDocumentId() {
        return documentId;
    }


    public void fromDocumentSnapshot(DocumentSnapshot document) {
        // Check if the DocumentSnapshot is valid and contains data
        if (document != null && document.exists()) {
            this.documentId = document.getId();


            // Retrieve other appointment fields as before
            this.patientDocumentId = document.getString("patientDocumentId");
            this.userDocumentId = document.getString("userDocumentId");
            this.purpose = document.getString("purpose");
            this.time = document.getString("time");
            this.date = document.getString("date");
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
        dest.writeString(documentId);
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



