package com.csc131.deltamedicalteam.model;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class PatientRepository {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference patientsRef = db.collection("patients");

    public void getPatientList(PatientListCallback callback) {
        patientsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Patient> patientList = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Patient patient = document.toObject(Patient.class);
                    patient.fromDocumentSnapshot(document);
                    patientList.add(patient);
                }
                callback.onCallback(patientList);
            } else {
                callback.onCallback(null);
            }
        });
    }

    public interface PatientListCallback {
        void onCallback(List<Patient> patientList);
    }
}
