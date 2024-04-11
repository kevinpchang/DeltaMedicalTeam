package com.csc131.deltamedicalteam.ui.appointment;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.balysv.materialripple.MaterialRippleLayout;
import com.csc131.deltamedicalteam.R;
import com.csc131.deltamedicalteam.model.Appointment;
import com.csc131.deltamedicalteam.utils.PatientUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddAppointmentFragment extends Fragment {

    // Declare variables
    private Spinner mPurpose, patientSpinner;
    private String time, date, purpose;
    private ProgressBar progressBar;
    private FirebaseFirestore fstore;

    

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_addappointment, container, false);

        // Initialize Firestore
        fstore = FirebaseFirestore.getInstance();

        // Find views
        mPurpose = rootView.findViewById(R.id.appointment_purpose_spinner);
        patientSpinner = rootView.findViewById(R.id.patient_list_spinner);
        progressBar = rootView.findViewById(R.id.progressBar);

        // Set up purpose spinner
        setupPurposeSpinner();

        // Get patient list for spinner
        getPatientList();

        // Handle adding appointment when button clicked
        handleAddAppointment();

        return rootView;
    }

    // Method to set up the purpose spinner
    private void setupPurposeSpinner() {
        String[] purposeItems = {"Analysis", "X-Rays", "Vaccination", "Doctor Visit"};
        ArrayAdapter<String> purposeAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, purposeItems);
        purposeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mPurpose.setAdapter(purposeAdapter);
    }

    // Method to fetch the list of patients for the spinner
    private void getPatientList() {
        CollectionReference patientsRef = fstore.collection("patients");
        patientsRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<String> patientNames = new ArrayList<>();
            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                String firstName = documentSnapshot.getString("fName");
                String lastName = documentSnapshot.getString("lName");
                String fullName = firstName + " " + lastName;
                patientNames.add(fullName);
            }
            // Populate spinner with patient names
            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, patientNames);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            patientSpinner.setAdapter(adapter);
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Error fetching patient documents: " + e.getMessage());
        });
    }

    // Method to handle adding appointment
    private void handleAddAppointment() {
        Activity rootView = null;
        Button mAddAppointmentBtn = rootView.findViewById(R.id.bt_create_appointment);
        mAddAppointmentBtn.setOnClickListener(v -> {
            // Get appointment data
            String purposeData = mPurpose.getSelectedItem().toString();
            String patientData = patientSpinner.getSelectedItem().toString();

            // Show progress bar
            progressBar.setVisibility(View.VISIBLE);

            // Add appointment to Firestore
            addAppointment(patientData, date, time, purposeData);
        });
    }

    // Method to add appointment to Firestore
    private void addAppointment(String patientData, String date, String time, String purposeData) {
        CollectionReference appointmentRef = fstore.collection("appointments");
        Appointment appointment = new Appointment(patientData, date, time, purposeData);
        appointmentRef.add(appointment)
                .addOnSuccessListener(documentReference -> {
                    // Hide progress bar on success
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(requireContext(), "Appointment successfully scheduled", Toast.LENGTH_SHORT).show();
                    // Navigate to another fragment if needed
                    Navigation.findNavController(requireView()).navigate(R.id.patientManagerFragment);
                })
                .addOnFailureListener(e -> {
                    // Hide progress bar on failure
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(requireContext(), "Failed to schedule appointment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}



