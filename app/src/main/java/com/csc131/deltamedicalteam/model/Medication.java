package com.csc131.deltamedicalteam.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Medication implements Parcelable {
    private String pastMedications;
    private String currentMedications;

    // Default constructor
    public Medication() {
        // Default constructor required for Firestore deserialization
    }

    public Medication(String currentMedications, String pastMedications) {
        this.currentMedications = currentMedications;
        this.pastMedications = pastMedications;
    }
/*
    protected Medication(Parcel in) {
        // Read map from parcel
        // You need to implement this part
        // For simplicity, let's skip this part
        currentMedications = in.readMap(String, String);
        pastMedication = in.readMap(String, String);
    }
*/
    protected Medication(Parcel in) {
        currentMedications = in.readString();
        pastMedications = in.readString();
    }




    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // Write map to parcel
        // You need to implement this part
        // For simplicity, let's skip this part
        dest.writeString(currentMedications);
        dest.writeString(pastMedications);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Medication> CREATOR = new Creator<Medication>() {
        @Override
        public Medication createFromParcel(Parcel in) {
            return new Medication(in);
        }

        @Override
        public Medication[] newArray(int size) {
            return new Medication[size];
        }
    };

    public String getCurrentMedications() {
        return currentMedications;
    }
    public String getPastMedications() {
        return pastMedications;
    }

    public void setCurrentMedications(String currentMedications) {
        this.currentMedications = currentMedications;
    }
    public void setPastMedications(String pastMedications) {
        this.pastMedications = pastMedications;
    }


}

