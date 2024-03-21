package com.csc131.deltamedicalteam.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Map;

public class User implements Parcelable {
    private String documentId;
    private String email;
    private String fName;
    private String permission;
    private String phone;

    public User() {
        // Default constructor required for Firestore deserialization
    }

    public User(String documentId, String email, String fName, String permission, String phone) {
        this.documentId = documentId;
        this.email = email;
        this.fName = fName;
        this.permission = permission;
        this.phone = phone;
    }

    protected User(Parcel in) {
        documentId = in.readString();
        email = in.readString();
        fName = in.readString();
        permission = in.readString();
        phone = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getDocumentId() {
        return documentId;
    }

    public String getEmail() {
        return email;
    }

    public String getfName() {
        return fName;
    }

    public String getPermission() {
        return permission;
    }

    public String getPhone() {
        return phone;
    }

    public void fromDocumentSnapshot(DocumentSnapshot document) {
        documentId = document.getId();
        email = document.getString("email");
        fName = document.getString("fName");
        permission = document.getString("permission");
        phone = document.getString("phone");
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
        dest.writeString(email);
        dest.writeString(fName);
        dest.writeString(permission);
        dest.writeString(phone);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
