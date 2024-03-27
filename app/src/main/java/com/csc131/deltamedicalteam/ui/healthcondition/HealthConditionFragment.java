package com.csc131.deltamedicalteam.ui.healthcondition;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.csc131.deltamedicalteam.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HealthConditionFragment extends Fragment {

    private com.csc131.deltamedicalteam.databinding.FragmentHealthConditionBinding binding;
    private Spinner patientSpinner;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_health_condition, container, false);

        patientSpinner = rootView.findViewById(R.id.healthcondition_patient_list_spinner);
        getPatientList();


        return rootView;
    }

    // Initialize Firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Reference to the "patients" collection
    private CollectionReference patientsRef = db.collection("patients");

    // Method to fetch the list of patients
    private void getPatientList() {
        patientsRef.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<String> patientNames = new ArrayList<>();
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        // Get patient name from DocumentSnapshot
                        String firstName = documentSnapshot.getString("fName");
                        String lastName = documentSnapshot.getString("lName");
                        String fullName = firstName + " " + lastName;

                        // Add patient name to the list
                        patientNames.add(fullName);
                    }

                    // Pass the list of patient names to populate the spinner
                    populateSpinner(patientNames);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching documents: " + e.getMessage());
                });
    }

    // Method to populate the spinner with patient names
    private void populateSpinner(List<String> patientNames) {
        // Create an ArrayAdapter using the patientNames list and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, patientNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        patientSpinner.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}