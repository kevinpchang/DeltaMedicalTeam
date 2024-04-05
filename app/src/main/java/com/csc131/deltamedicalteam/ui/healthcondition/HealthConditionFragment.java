package com.csc131.deltamedicalteam.ui.healthcondition;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.csc131.deltamedicalteam.R;
import com.csc131.deltamedicalteam.adapter.CurrentIllnessList;
import com.csc131.deltamedicalteam.adapter.PatientList;
import com.csc131.deltamedicalteam.model.HealthConditions;
import com.csc131.deltamedicalteam.model.Patient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HealthConditionFragment extends Fragment {

    private com.csc131.deltamedicalteam.databinding.FragmentHealthConditionBinding binding;

    // Initialize Firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Reference to the "patients" collection
    private CollectionReference patientsRef = db.collection("patients");
    private Spinner patientSpinner;
    RecyclerView recyclerViewCurrentIllness, recyclerViewMedicalHistory, recyclerViewSpecificAllergies;
    TabLayout tabLayout;
    private CurrentIllnessList mAdapter;
    List<Patient> patientNames = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_health_condition, container, false);

        patientSpinner = rootView.findViewById(R.id.healthcondition_patient_list_spinner);
        getPatientList();

        // Find RecyclerViews
        recyclerViewCurrentIllness = rootView.findViewById(R.id.RecyclerView_current_illness);
        recyclerViewMedicalHistory = rootView.findViewById(R.id.RecyclerView_medical_history);
        recyclerViewSpecificAllergies = rootView.findViewById(R.id.RecyclerView_specific_allergies);

        // Find TabLayout
        tabLayout = rootView.findViewById(R.id.healConditionTabs);

        recyclerViewCurrentIllness.setLayoutManager(new LinearLayoutManager(getActivity()));

      //detects when spinner item is selected
        patientSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //retrieves the patient at the given spinner position
                Patient patient = (Patient) parent.getItemAtPosition(position);
                String ID = patient.getDocumentId();
                //uses patient id from spinner and to display current illnesses
                patientsRef.document(ID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        List<String> currIllness = (List<String>) documentSnapshot.get("currentIllnesses");
                        List<HealthConditions> items = new ArrayList<>();
                        //used to check if array is empty or not
                        if (currIllness != null) {
                            for (int i = 0; i < currIllness.size(); i++) {
                                HealthConditions hCons = new HealthConditions();
                                hCons.setCurrentIllnesses(currIllness.get(i));
                                items.add(hCons);
                            }
                        }

                        mAdapter = new CurrentIllnessList(getActivity(), items);
                        recyclerViewCurrentIllness.setAdapter(mAdapter);
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Set up OnTabSelectedListener
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // Show corresponding RecyclerView and hide others
                switch (tab.getPosition()) {
                    case 0:
                        recyclerViewCurrentIllness.setVisibility(View.VISIBLE);
                        recyclerViewMedicalHistory.setVisibility(View.GONE);
                        recyclerViewSpecificAllergies.setVisibility(View.GONE);
                        break;
                    case 1:
                        recyclerViewCurrentIllness.setVisibility(View.GONE);
                        recyclerViewMedicalHistory.setVisibility(View.VISIBLE);
                        recyclerViewSpecificAllergies.setVisibility(View.GONE);
                        break;
                    case 2:
                        recyclerViewCurrentIllness.setVisibility(View.GONE);
                        recyclerViewMedicalHistory.setVisibility(View.GONE);
                        recyclerViewSpecificAllergies.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // No action needed
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // No action needed
            }
        });

        return rootView;
    }

    // Method to fetch the list of patients, spinner is populated with patient objects
    private void getPatientList() {
        patientsRef.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        // Get patient name from DocumentSnapshot
                        // Get patient id from DocumentSnapshot
                        Patient patient = documentSnapshot.toObject(Patient.class);
                        String documentId = documentSnapshot.getId();
                        patient.setDocumentId(documentId);

                        // Add patient name to the list
                        patientNames.add(patient);
                    }

                    // Pass the list of patient names to populate the spinner
                    populateSpinner(patientNames);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching documents: " + e.getMessage());
                });
    }

    // Method to populate the spinner with patient names
    private void populateSpinner(List<Patient> patientNames) {
        // Create an ArrayAdapter using the patientNames list and a default spinner layout
        ArrayAdapter<Patient> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, patientNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        patientSpinner.setAdapter(adapter);
    }

    private void getPatientID(String ID) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}