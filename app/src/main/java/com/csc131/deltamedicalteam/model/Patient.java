package com.csc131.deltamedicalteam.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class Patient implements Parcelable {
    private int imageResource;
    private String documentId;
    private String address;
    private String ageFormat;
    private String bloodGroup;
     private String dob; // Temporarily disable dob field
    private String fName;
    private String lName;
    private String sex;
    private String maritalStatus;
    private String cellPhone;
    private Medication medication;
    private String rhFactor;
    private String email;

    private List<String> specificAllergies;
    private HealthConditions healthConditions;
  


    public Patient() {
        // Default constructor required for Firestore
    }

    public Patient(String documentId, String address, String ageFormat, String bloodGroup, /* String dob, */ String fName, String lName, String sex, String maritalStatus, Medication medication, String cell, String rhFactor, int imageResource, String email, List<String> specificAllergies, HealthConditions healthConditions) {
        this.documentId = documentId;
        this.address = address;
        this.ageFormat = ageFormat;
        this.bloodGroup = bloodGroup;
        //this.dob = dob; // Temporarily disable dob field
        this.fName = fName;
        this.lName = lName;
        this.sex = sex;
        this.maritalStatus = maritalStatus;
        this.medication = medication;
        this.cellPhone = cell;
        this.rhFactor = rhFactor;
        this.imageResource = imageResource;
        this.email = email;
        this.specificAllergies = specificAllergies;
        this.healthConditions = healthConditions;
    }

    protected Patient(Parcel in) {
        documentId = in.readString();
        address = in.readString();
        ageFormat = in.readString();
        bloodGroup = in.readString();
         dob = in.readString(); // Temporarily disable dob field
        fName = in.readString();
        healthConditions = in.readParcelable(HealthConditions.class.getClassLoader());
        lName = in.readString();
        maritalStatus = in.readString();
        medication = in.readParcelable(Medication.class.getClassLoader());
        cellPhone = in.readString();
        rhFactor = in.readString();
        imageResource = in.readInt();
        email = in.readString();
        specificAllergies = in.createStringArrayList();
    }

    public  String getDocumentId() {
        return documentId;
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


    public void setDocumentId(String documentId) { this.documentId = documentId;}

    public String getAddress() {
        return address;
    }

    public String getAgeFormat() {
        return ageFormat;
    }

//    public String getBloodGroup() {
//        return bloodGroup;
//    }

     public String getDob() {
        return dob;
    } // Temporarily disable dob field

    public String getfName() {
        return fName;
    }

//    public HealthConditions getHealthConditions() {
//        return healthConditions;
//    }

    public String getlName() {
        return lName;
    }

    public String getName() {  return fName + " " + lName;}

    public String getSex() { return sex;}
    public String getEmail() { return email; }

    @NonNull
    @Override
    public String toString() {  return fName + " " + lName;}

    public String getMaritalStatus() {
        return maritalStatus;
    }

//    public Medication getMedication() {
//        return medication;
//    }

    public String getCellPhone() { return cellPhone;}

//    public String getRhFactor() {
//        return rhFactor;
//    }




    // Getter and setter for imageResource
    public int getImage() {
        return imageResource;
    }

    public void setImage(int imageResource) {
        this.imageResource = imageResource;
    }

    public List<String> getSpecificAllergies() {
        return specificAllergies;
    }

    public void setSpecificAllergies(List<String> specificAllergies) {
        this.specificAllergies = specificAllergies;
    }

    public static void getPatientFromId(String patientId, OnSuccessListener<Patient> onSuccessListener) {
        // Initialize Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Reference to the collection where patients are stored
        db.collection("patients").document(patientId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Convert the document snapshot to a Patient object
                        Patient patient = documentSnapshot.toObject(Patient.class);
                        // Invoke the success listener with the patient object
                        onSuccessListener.onSuccess(patient);
                    } else {
                        // Document with the given ID does not exist
                        onSuccessListener.onSuccess(null);
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                    onSuccessListener.onSuccess(null);
                });
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
        // Convert specificAllergies List<String> to String array before writing to parcel
        dest.writeStringArray(specificAllergies != null ? (String[]) specificAllergies.toArray(new String[0]) : null);
    }

    @Override
    public int describeContents() {
        return 0;
    }



}



