package com.csc131.deltamedicalteam.ui.healthcondition;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.csc131.deltamedicalteam.R;
import com.csc131.deltamedicalteam.adapter.CurrentAllergiesList;
import com.csc131.deltamedicalteam.adapter.CurrentIllnessList;
import com.csc131.deltamedicalteam.adapter.MedicalHistoryList;
import com.csc131.deltamedicalteam.helper.SwipeItemTouchHelper;
import com.csc131.deltamedicalteam.model.HealthConditions;
import com.csc131.deltamedicalteam.model.Patient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HealthConditionFragment extends Fragment {

    // Initialize Firestore
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Reference to the "patients" collection
    private final CollectionReference patientsRef = db.collection("patients");
    private Button mCurrentIllnessAddEditButton, mAllergiesAddEditButton;
    private Spinner patientSpinner;
    RecyclerView recyclerViewCurrentIllness, recyclerViewMedicalHistory, recyclerViewSpecificAllergies;
    TabLayout tabLayout;
    private CurrentIllnessList mCurrentIllnessAdapter;
    private MedicalHistoryList mMedicalHistoryAdapter;
    private CurrentAllergiesList mAllergiesAdapter;

    private Patient mCurrentPatient;
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

        //button
        mCurrentIllnessAddEditButton = rootView.findViewById(R.id.current_illness_addbutton);
        mAllergiesAddEditButton = rootView.findViewById(R.id.allergies_addbutton);

        // Find TabLayout
        tabLayout = rootView.findViewById(R.id.healConditionTabs);

        recyclerViewCurrentIllness.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewSpecificAllergies.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewMedicalHistory.setLayoutManager(new LinearLayoutManager(getActivity()));


      //detects when spinner item is selected
        patientSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //retrieves the patient at the given spinner position
                mCurrentPatient = (Patient) parent.getItemAtPosition(position);
                String ID = mCurrentPatient.getDocumentId();
                //uses patient id from spinner and to display current illnesses
                patientsRef.document(ID).get().addOnSuccessListener(documentSnapshot -> {
                    List<String> currIllness = (List<String>) documentSnapshot.get("currentIllnesses");
                    List<HealthConditions> currIllnessItems = new ArrayList<>();
                    //used to check if array is empty or not
                    if (currIllness != null) {
                        for (int i = 0; i < currIllness.size(); i++) {
                            HealthConditions hCons = new HealthConditions();
                            hCons.setCurrentIllnesses(currIllness.get(i));
                            currIllnessItems.add(hCons);
                        }
                    }
                    mCurrentIllnessAdapter = new CurrentIllnessList(getActivity(), currIllnessItems);
                    recyclerViewCurrentIllness.setAdapter(mCurrentIllnessAdapter);

                    //Medical History
                    List<String> prevIllness = (List<String>) documentSnapshot.get("previousIllnesses");
                    List<HealthConditions> prevIllnessItems = new ArrayList<>();
                    if (prevIllness != null) {
                        for (int i = 0; i < prevIllness.size(); i++) {
                            HealthConditions hCons = new HealthConditions();
                            hCons.setPreviousIllnesses(prevIllness.get(i));
                            prevIllnessItems.add(hCons);
                        }
                    }
                    mMedicalHistoryAdapter = new MedicalHistoryList(getActivity(), prevIllnessItems);
                    recyclerViewMedicalHistory.setAdapter(mMedicalHistoryAdapter);

                    //currentAllegies

                    List<String> currAllergies = (List<String>) documentSnapshot.get("specificAllergies");

// Check if the list of allergies is not null and not empty
                    if (currAllergies != null && !currAllergies.isEmpty()) {
                        // Create the adapter with the list of allergies
                        mAllergiesAdapter = new CurrentAllergiesList(currAllergies);

                        // Set the adapter to the RecyclerView
                        recyclerViewSpecificAllergies.setAdapter(mAllergiesAdapter);
                    } else {
                        // Create a dummy string to indicate no allergies were found
                        String dummyAllergy = "No allergies found";

                        // Create a list containing the dummy string
                        List<String> dummyList = new ArrayList<>();
                        dummyList.add(dummyAllergy);

                        // Create the adapter with the dummy list
                        mAllergiesAdapter = new CurrentAllergiesList(dummyList);

                        // Set the adapter to the RecyclerView
                        recyclerViewSpecificAllergies.setAdapter(mAllergiesAdapter);
                    }
                });


                SwipeItemTouchHelper swipeItemTouchHelper = new SwipeItemTouchHelper(mAllergiesAdapter);
                // Create an instance of ItemTouchHelper and attach SwipeItemTouchHelper to it
                ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeItemTouchHelper);
                itemTouchHelper.attachToRecyclerView(recyclerViewSpecificAllergies);
                swipeItemTouchHelper.setSwipeListener(new SwipeItemTouchHelper.SwipeListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onItemDismiss(int position) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("Confirm Deletion");
                        builder.setMessage("Are you sure you want to remove this allergy?");
                        builder.setPositiveButton("Yes", (dialog, which) -> {
                            // Remove the item from the list
                            String removedAllergy = mAllergiesAdapter.getAllergies().get(position);
                            mAllergiesAdapter.getAllergies().remove(position);
                            mAllergiesAdapter.notifyItemRemoved(position);

                            // Remove the item from the database
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            DocumentReference patientRef = db.collection("patients").document(mCurrentPatient.getDocumentId());
                            patientRef.update("specificAllergies", FieldValue.arrayRemove(removedAllergy))
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(getContext(), "Allergy Removed: " + removedAllergy, Toast.LENGTH_SHORT).show();
                                        dialog.dismiss(); // Dismiss the dialog after successful deletion
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e(TAG, "Failed to remove allergy: " + e.getMessage());
                                        // If removal from database fails, add the item back to the list and notify the adapter
                                        mAllergiesAdapter.getAllergies().add(position, removedAllergy);
                                        mAllergiesAdapter.notifyItemInserted(position);
                                        Toast.makeText(getContext(), "Failed to remove allergy: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        });
                        builder.setNegativeButton("Cancel", (dialog, which) -> {
                            dialog.dismiss();
                            mAllergiesAdapter.notifyDataSetChanged(); // Refresh the list after canceling
                        });
                        builder.show();
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

                        mCurrentIllnessAddEditButton.setVisibility(View.VISIBLE);
                        mAllergiesAddEditButton.setVisibility(View.GONE);
                        break;
                    case 1:
                        recyclerViewCurrentIllness.setVisibility(View.GONE);
                        recyclerViewMedicalHistory.setVisibility(View.VISIBLE);
                        recyclerViewSpecificAllergies.setVisibility(View.GONE);

                        mCurrentIllnessAddEditButton.setVisibility(View.GONE);
                        mAllergiesAddEditButton.setVisibility(View.GONE);
                        break;
                    case 2:
                        recyclerViewCurrentIllness.setVisibility(View.GONE);
                        recyclerViewMedicalHistory.setVisibility(View.GONE);
                        recyclerViewSpecificAllergies.setVisibility(View.VISIBLE);

                        mCurrentIllnessAddEditButton.setVisibility(View.GONE);
                        mAllergiesAddEditButton.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // No action needed
                recyclerViewCurrentIllness.setVisibility(View.VISIBLE);
                recyclerViewMedicalHistory.setVisibility(View.GONE);
                recyclerViewSpecificAllergies.setVisibility(View.GONE);

                mCurrentIllnessAddEditButton.setVisibility(View.VISIBLE);
                mAllergiesAddEditButton.setVisibility(View.GONE);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // No action needed
            }
        });

        mAllergiesAddEditButton.setOnClickListener(v -> {
            EditText allergiesInput = new EditText(v.getContext());
            AlertDialog.Builder addAllergiesDialog = new AlertDialog.Builder(v.getContext());
            addAllergiesDialog.setTitle("Add Allergies");
            addAllergiesDialog.setMessage("Enter the allergy you want to add:");
            addAllergiesDialog.setView(allergiesInput);

            addAllergiesDialog.setPositiveButton("Add", (dialog, which) -> {
                // Extract the allergy entered by the user
                String allergy = allergiesInput.getText().toString().trim();

                // Perform validation
                if (TextUtils.isEmpty(allergy)) {
                    Toast.makeText(v.getContext(), "Allergy cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check if the allergy already exists in the database
                boolean allergyExists = false;
                for (String existingAllergy : mCurrentPatient.getSpecificAllergies()) {
                    if (existingAllergy.equals(allergy)) {
                        allergyExists = true;
                        break;
                    }
                }

                if (allergyExists) {
                    Toast.makeText(v.getContext(), "Allergy already exists", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Update the specificAllergies field in Firestore
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference patientRef = db.collection("patients").document(mCurrentPatient.getDocumentId());
                patientRef.update("specificAllergies", FieldValue.arrayUnion(allergy))
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(v.getContext(), "Allergy Added: " + allergy, Toast.LENGTH_SHORT).show();
                            // Refresh the list of specific allergies
                            refreshSpecificAllergies();

                        })
                        .addOnFailureListener(e -> Toast.makeText(v.getContext(), "Failed to add allergy: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            });

            addAllergiesDialog.setNegativeButton("Cancel", (dialog, which) -> {
                // Close the dialog
            });

            addAllergiesDialog.create().show();
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
                .addOnFailureListener(e -> Log.e(TAG, "Error fetching documents: " + e.getMessage()));
    }

    // Method to populate the spinner with patient names
    private void populateSpinner(List<Patient> patientNames) {
        // Create an ArrayAdapter using the patientNames list and a default spinner layout
        ArrayAdapter<Patient> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, patientNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        patientSpinner.setAdapter(adapter);
    }

    // Method to refresh the list of specific allergies in the RecyclerView
    private void refreshSpecificAllergies() {
        // Retrieve the updated list of specific allergies from the database
        FirebaseFirestore.getInstance().collection("patients").document(mCurrentPatient.getDocumentId())
                .get().addOnSuccessListener(documentSnapshot -> {
                    // Get the list of specific allergies from the document snapshot
                    List<String> updatedAllergies = (List<String>) documentSnapshot.get("specificAllergies");

                    // Update the adapter with the updated list of allergies
                    if (updatedAllergies != null && !updatedAllergies.isEmpty()) {
                        mAllergiesAdapter.updateAllergies(updatedAllergies);
                    } else {
                        // Create a dummy list if allergies are empty
                        List<String> dummyList = new ArrayList<>();
                        dummyList.add("No allergies found");
                        mAllergiesAdapter.updateAllergies(dummyList);
                    }
                });
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}