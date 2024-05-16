package com.csc131.deltamedicalteam.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class Patient implements Parcelable {

    private String documentId;
    private String address;
     private String dob;
    private String fName;
    private String lName;
    private String sex;
    private String maritalStatus;
    private String phone;

    private String emergencyName,emergencyPhone;
    private String email;

    private List<String> specificAllergies;
    private List<String> currentMedications;
    private List<String> pastMedications;
    private List<String> currentIllnesses;
    private List<String> pastIllnesses;

    private String profilePictureUrl;
  


    public Patient() {
        // Default constructor required for Firestore
    }

    public Patient(String documentId, String fName, String lName, String dob,   String sex, String maritalStatus, String address, String phone,  String email, String emergencyName, String emergencyPhone,  List<String> specificAllergies, List<String> currentMedications, List<String> pastMedications,List<String> currentIllnesses, List<String> pastIllnesses) {
        this.documentId = documentId;
        this.fName = fName;
        this.lName = lName;
        this.dob = dob;
        this.sex = sex;

        this.address = address;
        this.maritalStatus = maritalStatus;
        this.phone = phone;
        this.email = email;

        this.emergencyName = emergencyName;
        this.emergencyPhone = emergencyPhone;

        this.specificAllergies = specificAllergies;
        this.currentMedications = currentMedications;
        this.pastMedications = pastMedications;
        this.currentIllnesses = currentIllnesses;
        this.pastIllnesses = pastIllnesses;
    }

    protected Patient(Parcel in) {
        documentId = in.readString();
        fName = in.readString();
        lName = in.readString();
        dob = in.readString(); // Temporarily disable dob field
        sex = in.readString();
        maritalStatus = in.readString();
        address = in.readString();
        phone = in.readString();
        email = in.readString();
        emergencyName = in.readString();
        emergencyPhone = in.readString();

        currentMedications = in.createStringArrayList();
        pastMedications = in.createStringArrayList();
        currentIllnesses = in.createStringArrayList();
        pastIllnesses = in.createStringArrayList();
        specificAllergies = in.createStringArrayList();
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


//    public void setDocumentId(String documentId) { this.documentId = documentId;}

    @NonNull
    @Override
    public String toString() {
        return this.getMemberID() + " - "+ fName + " " + lName; // Return the name when the object is converted to a string
    }

    public  String getDocumentId() {
        return documentId;
    }

    public String getMemberID() {
        // Ensure permission and userID are not null
        if (documentId != null) {
            // Extract the first 3 characters of the permission string
            String permissionPrefix = "PAT";
            // Extract the last 5 characters of the userID string
            String userIDSuffix = documentId.substring(Math.max(0, documentId.length() - 5));
            // Concatenate the prefix and suffix and convert to uppercase
            return (permissionPrefix + userIDSuffix).toUpperCase();
        } else {
            // Handle cases where either permission or userID is null
            return null;
        }
    }

    public String getName() {  return fName + " " + lName;}

    public String getSex() { return sex;}

    public String getDob() {
        return dob;
    }


    public String getMaritalStatus() {
        return maritalStatus;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() { return phone;}
    public String getEmail() { return email; }

    public String getEmergencyName() { return emergencyName;}

    public String getEmergencyPhone() { return emergencyPhone;}



    public List<String> getSpecificAllergies() {
        return specificAllergies;
    }

    public List<String> getCurrentIllnesses() {
        return currentIllnesses;
    }

    public List<String> getItems() {
        return currentMedications;
    }

    public List<String> getPastIllnesses() {
        return pastIllnesses;
    }

    public List<String> getPastMedications() {
        return pastMedications;
    }

    public void setCurrentIllnesses(List<String> currentIllnesses) {
        this.currentIllnesses = currentIllnesses;
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

                        assert patient != null;
                        patient.fromDocumentSnapshot(documentSnapshot);
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




    public void fromDocumentSnapshot(DocumentSnapshot documentSnapshot) {
        if (documentSnapshot != null && documentSnapshot.exists()) {
            // Extract data from the documentSnapshot
            this.documentId = documentSnapshot.getId();

            this.fName = documentSnapshot.getString("fName");
            this.lName = documentSnapshot.getString("lName");
            this.dob = documentSnapshot.getString("dob");
            this.sex = documentSnapshot.getString("sex");
            this.maritalStatus = documentSnapshot.getString("maritalStatus");

            this.email = documentSnapshot.getString("email");
            this.phone = documentSnapshot.getString("phone");
            this.address = documentSnapshot.getString("address");
            this.emergencyName = documentSnapshot.getString("emergencyName");
            this.emergencyPhone = documentSnapshot.getString("emergencyPhone");

            // Extract specificAllergies as List<String>
            this.specificAllergies = (List<String>) documentSnapshot.get("specificAllergies");
            this.currentIllnesses = (List<String>) documentSnapshot.get("currentIllnesses");
            this.pastIllnesses = (List<String>) documentSnapshot.get("pastIllnesses");
            this.currentMedications = (List<String>) documentSnapshot.get("currentMedications");
            this.pastMedications = (List<String>) documentSnapshot.get("pastMedications");

        }
    }


    // Parcelable methods
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(documentId);
        dest.writeString(fName);
        dest.writeString(lName);
        dest.writeString(dob);
        dest.writeString(sex);
        dest.writeString(maritalStatus);

        dest.writeString(phone);
        dest.writeString(address);
        dest.writeString(emergencyName);
        dest.writeString(emergencyPhone);


        dest.writeStringArray(specificAllergies != null ? (String[]) specificAllergies.toArray(new String[0]) : null);
        dest.writeStringArray(currentMedications != null ? (String[]) currentMedications.toArray(new String[0]) : null);
        dest.writeStringArray(pastMedications != null ? (String[]) pastMedications.toArray(new String[0]) : null);
        dest.writeStringArray(currentIllnesses != null ? (String[]) currentIllnesses.toArray(new String[0]) : null);
        dest.writeStringArray(pastIllnesses != null ? (String[]) pastIllnesses.toArray(new String[0]) : null);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }



}



