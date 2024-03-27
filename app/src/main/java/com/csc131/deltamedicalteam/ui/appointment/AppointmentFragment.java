package com.csc131.deltamedicalteam.ui.appointment;

import static android.content.ContentValues.TAG;

import static com.csc131.deltamedicalteam.utils.PatientUtils.populateSpinner;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.balysv.materialripple.MaterialRippleLayout;
import com.csc131.deltamedicalteam.R;
import com.csc131.deltamedicalteam.adapter.PatientList;
import com.csc131.deltamedicalteam.model.Patient;
import com.csc131.deltamedicalteam.model.PatientRepository;
import com.csc131.deltamedicalteam.utils.PatientUtils;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AppointmentFragment extends Fragment {

    private Spinner mPurpose, patientSpinner;

    private ProgressBar progressBar;

    

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_addappointment, container, false);


        // Prepare the data source (list of suggestions)
        String[] purposeItems  = {"Analysis", "X-Rays",  "Vaccination", "Doctor Visit"};


        // Create the adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, purposeItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mPurpose = rootView.findViewById(R.id.appointment_purpose_spinner);
        mPurpose.setAdapter(adapter);


        patientSpinner = rootView.findViewById(R.id.patient_list_spinner);
        getPatientList();



        progressBar = rootView.findViewById(R.id.progressBar);


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

}


