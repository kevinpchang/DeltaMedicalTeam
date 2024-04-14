package com.csc131.deltamedicalteam.ui.medication;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.csc131.deltamedicalteam.R;
import com.csc131.deltamedicalteam.adapter.CurrentMedicationList;
import com.csc131.deltamedicalteam.adapter.PastMedicationList;
import com.csc131.deltamedicalteam.helper.SwipeItemTouchHelper;
import com.csc131.deltamedicalteam.model.Medication;
import com.csc131.deltamedicalteam.model.Patient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MedicationFragment extends Fragment {

    private com.csc131.deltamedicalteam.databinding.FragmentMedicationBinding binding;

    // Initialize Firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    // Reference to the "patients" collection
    private CollectionReference patientsRef = db.collection("patients");
    private Spinner patientSpinner;

    private RecyclerView recyclerViewCurrentMedication, recyclerViewPastMedication;
    private CurrentMedicationList mCurrentMedicationAdapter;
    private PastMedicationList mPastMedicationAdapter;
    TabLayout tabLayout;
    List<Patient> patientNames = new ArrayList<>();


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_medication, container, false);
//this control item spinner
        patientSpinner = rootView.findViewById(R.id.medication_patient_list_spinner);
        getPatientList();

        //Search for the Recyclerviews associated with past/current list displays
        recyclerViewCurrentMedication = rootView.findViewById(R.id.RecyclerView_current_medication);
        recyclerViewPastMedication    = rootView.findViewById(R.id.RecyclerView_past_medication);

        //Possibly initialize buttons for modifying and adding stuff to firebase, Using buttons
            //currentMedsButton
            //pastMedsButton

        recyclerViewCurrentMedication.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewPastMedication.setLayoutManager(new LinearLayoutManager(getActivity()));

        tabLayout = rootView.findViewById(R.id.medicationTabs);
        tabLayout.getTabAt(0).select();
        /*
        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        Query query = FirebaseFirestore.getInstance()
                .collection("medicine")
                .orderBy("timestamp");
        */

patientSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //retrieves the patient at the given spinner position
        Patient patient = (Patient) parent.getItemAtPosition(position);
        String ID = patient.getDocumentId();
        //uses patient id from spinner and to display current illnesses
        patientsRef.document(ID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            //Current Medication prescriebd
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                List<String> currMedication = (List<String>) documentSnapshot.get("currentMedications");
                List<Medication> currMedicationItems = new ArrayList<>();
                //used to check if array is empty or not
                if (currMedication != null) {
                    for (int i = 0; i < currMedication.size(); i++) {
                        Medication nMeds = new Medication();
                        nMeds.setCurrentMedications(currMedication.get(i));
                        currMedicationItems.add(nMeds);
                    }
                }
                mCurrentMedicationAdapter = new CurrentMedicationList(getActivity(), currMedicationItems);
                recyclerViewCurrentMedication.setAdapter(mCurrentMedicationAdapter);

                SwipeItemTouchHelper swipeItemTouchHelper = new SwipeItemTouchHelper(mCurrentMedicationAdapter);
                // Create an instance of ItemTouchHelper and attach SwipeItemTouchHelper to it
                ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeItemTouchHelper);
                itemTouchHelper.attachToRecyclerView(recyclerViewCurrentMedication);
                swipeItemTouchHelper.setSwipeListener(new SwipeItemTouchHelper.SwipeListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onItemDismiss(int position) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("Confirm Deletion");
                        builder.setMessage("Are you sure you want to remove this current medication?");
                        builder.setPositiveButton("Yes", (dialog, which) -> {
                            // Remove the item from the list
                            Medication removedMedication = mCurrentMedicationAdapter.getCurrentMedication().get(position);
                            mCurrentMedicationAdapter.getCurrentMedication().remove(position);
                            mCurrentMedicationAdapter.notifyItemRemoved(position);

                            // Remove the item from the database
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            DocumentReference patientRef = db.collection("patients").document(patient.getDocumentId());
                            patientRef.update("currentMedications", FieldValue.arrayRemove(removedMedication.getCurrentMedications()))
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(getContext(), "Medication Removed: " + removedMedication.getCurrentMedications(), Toast.LENGTH_SHORT).show();
                                        dialog.dismiss(); // Dismiss the dialog after successful deletion
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e(TAG, "Failed to remove Medication: " + e.getMessage());
                                        // If removal from database fails, add the item back to the list and notify the adapter
                                        mCurrentMedicationAdapter.getCurrentMedication().add(position, removedMedication);
                                        mCurrentMedicationAdapter.notifyItemInserted(position);
                                        Toast.makeText(getContext(), "Failed to remove Medication: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        });
                        builder.setNegativeButton("Cancel", (dialog, which) -> {
                            dialog.dismiss();
                            mCurrentMedicationAdapter.notifyDataSetChanged(); // Refresh the list after canceling
                        });
                        builder.show();
                    }
                });

                //Past medication history prescribed
                List<String> pastMedication = (List<String>) documentSnapshot.get("pastMedications");
                List<Medication> prevIllnessItems = new ArrayList<>();
                if (pastMedication != null) {
                    for (int i = 0; i < pastMedication.size(); i++) {
                        Medication nMeds = new Medication();
                        nMeds.setPastMedications(pastMedication.get(i));
                        prevIllnessItems.add(nMeds);
                    }
                }
                mPastMedicationAdapter = new PastMedicationList(getActivity(), prevIllnessItems);
                recyclerViewPastMedication.setAdapter(mPastMedicationAdapter);

            }


        });
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
});

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // Show corresponding RecyclerView and hide others
                switch (tab.getPosition()) {
                    case 0:
                        recyclerViewCurrentMedication.setVisibility(View.VISIBLE);
                        recyclerViewPastMedication.setVisibility(View.GONE);


                        //mCurrentMedicationAddEditButton.setVisibility(View.VISIBLE);
                        //mPastMedicationAddEditButton.setVisibility(View.GONE);
                        break;
                    case 1:
                        recyclerViewCurrentMedication.setVisibility(View.GONE);
                        recyclerViewPastMedication.setVisibility(View.VISIBLE);


                        //mCurrentMedicationAddEditButton.setVisibility(View.GONE);
                        //mPastMedicationAddEditButton.setVisibility(View.VISIBLE);
                        break;
                }
            }


            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // No action needed
                recyclerViewCurrentMedication.setVisibility(View.VISIBLE);
                recyclerViewPastMedication.setVisibility(View.GONE);


                //mCurrentMedicationAddEditButton.setVisibility(View.VISIBLE);
                //mPastMedicationAddEditButton.setVisibility(View.GONE);
            }


            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // No action needed
            }
        });



        return rootView;
    }






    // Method to fetch the list of patients
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

/*
    private void getMedicationList() {
        CollectionReference medicationRef = db.collection("medicine");
        medicationRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<Medication> medicationList = new ArrayList<>();
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                Medication medication = document.toObject(Medication.class);
                medicationList.add(medication);
            }
            // Create and set up RecyclerView adapter
            mAdapter = new MedicationList(getActivity(), medicationList);
            recyclerView.setAdapter(mAdapter);
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Error getting medication documents: " + e.getMessage());
        });
    }

*/
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}