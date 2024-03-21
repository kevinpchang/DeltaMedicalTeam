package com.csc131.deltamedicalteam.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Map;

class Medication implements Parcelable {
    private Map<String, String> pastMedication;
    private String currentMedications;

    // Default constructor
    public Medication() {
        // Default constructor required for Firestore deserialization
    }

    public Medication(Map<String, String> pastMedication, String currentMedications) {
        this.pastMedication = pastMedication;
        this.currentMedications = currentMedications;
    }

    protected Medication(Parcel in) {
        // Read map from parcel
        // You need to implement this part
        // For simplicity, let's skip this part
        currentMedications = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // Write map to parcel
        // You need to implement this part
        // For simplicity, let's skip this part
        dest.writeString(currentMedications);
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

    public Map<String, String> getPastMedication() {
        return pastMedication;
    }

    public String getCurrentMedications() {
        return currentMedications;
    }
}
