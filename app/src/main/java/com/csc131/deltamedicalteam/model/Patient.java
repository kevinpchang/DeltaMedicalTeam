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

public class Patient implements Parcelable {
    private int imageResource;
    private String documentId;
    private String address;


    private String ageFormat;
    private String bloodGroup;
    // private String dob; // Temporarily disable dob field
    private String fName;
    private HealthConditions healthConditions;
    private String lName;
    private String maritalStatus;

    private String cellPhone;
    private Medication medication;
    private String rhFactor;

    public Patient() {
        // Default constructor required for Firestore
    }

    public Patient(String documentId, String address, String ageFormat, String bloodGroup, /* String dob, */ String fName, HealthConditions healthConditions, String lName, String maritalStatus, Medication medication, String cell, String rhFactor, int imageResource) {
        this.documentId = documentId;
        this.address = address;
        this.ageFormat = ageFormat;
        this.bloodGroup = bloodGroup;
        // this.dob = dob; // Temporarily disable dob field
        this.fName = fName;
        this.healthConditions = healthConditions;
        this.lName = lName;
        this.maritalStatus = maritalStatus;
        this.medication = medication;
        this.cellPhone = cell;
        this.rhFactor = rhFactor;
        this.imageResource = imageResource;
    }

    protected Patient(Parcel in) {
        documentId = in.readString();
        address = in.readString();
        ageFormat = in.readString();
        bloodGroup = in.readString();
        // dob = in.readString(); // Temporarily disable dob field
        fName = in.readString();
        healthConditions = in.readParcelable(HealthConditions.class.getClassLoader());
        lName = in.readString();
        maritalStatus = in.readString();
        medication = in.readParcelable(Medication.class.getClassLoader());
        cellPhone = in.readString();
        rhFactor = in.readString();
        imageResource = in.readInt();
    }

    public static final Creator<Patient> CREATOR = new Creator<Patient>() {
        @Override
        public Patient createFromParcel(Parcel in) {
            return new Patient(in);
        }

        @Override
        public Patient[] newArray(int size) {
            return new Patient[size];
        }
    };

    public String getDocumentId() {
        return documentId;
    }

    public String getAddress() {
        return address;
    }

    public String getAgeFormat() {
        return ageFormat;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    // public String getDob() {
    //     return dob;
    // } // Temporarily disable dob field

    public String getfName() {
        return fName;
    }

    public HealthConditions getHealthConditions() {
        return healthConditions;
    }

    public String getlName() {
        return lName;
    }

    public String getName() {
        return fName + " " + lName;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public Medication getMedication() {
        return medication;
    }

    public String getCellPhone() { return cellPhone;}

    public String getRhFactor() {
        return rhFactor;
    }

    // Getter and setter for imageResource
    public int getImage() {
        return imageResource;
    }

    public void setImage(int imageResource) {
        this.imageResource = imageResource;
    }

    public void fromDocumentSnapshot(DocumentSnapshot document) {
        documentId = document.getId();
        address = document.getString("address");
        ageFormat = document.getString("ageFormat");
        bloodGroup = document.getString("bloodGroup");
        fName = document.getString("fName");
        lName = document.getString("lName");
        maritalStatus = document.getString("maritalStatus");
        rhFactor = document.getString("rhFactor");

        // Extract nested objects
        healthConditions = document.toObject(HealthConditions.class);
        medication = document.toObject(Medication.class);
    }

    // Add method to convert to Map for Firestore
    public Map<String, Object> toMap() {
        // Convert all fields to a Map
        // You need to implement this according to your document structure
        return null;
    }

    // Parcelable methods
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(documentId);
        dest.writeString(address);
        dest.writeString(ageFormat);
        dest.writeString(bloodGroup);
        // dest.writeString(dob); // Temporarily disable dob field
        dest.writeString(fName);
        dest.writeParcelable(healthConditions, flags);
        dest.writeString(lName);
        dest.writeString(maritalStatus);
        dest.writeParcelable(medication, flags);
        dest.writeString(cellPhone);
        dest.writeString(rhFactor);
        dest.writeInt(imageResource);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}



