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
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.csc131.deltamedicalteam.R;
import com.csc131.deltamedicalteam.adapter.CurrentIllnessList;
import com.csc131.deltamedicalteam.adapter.CurrentMedicationList;
import com.csc131.deltamedicalteam.adapter.PastMedicationList;
import com.csc131.deltamedicalteam.helper.SwipeItemTouchHelper;
import com.csc131.deltamedicalteam.model.HealthConditions;
import com.csc131.deltamedicalteam.model.Medication;
import com.csc131.deltamedicalteam.model.Patient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MedicationFragment extends Fragment {

    private com.csc131.deltamedicalteam.databinding.FragmentMedicationBinding binding;

    // Initialize Firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    // Reference to the "patients" collection
    private final CollectionReference patientsRef = db.collection("patients");

    private Button mCurrentMedicationAddEditButton;
    private Spinner patientSpinner;
    String currentMedicationSelector;

    private RecyclerView recyclerViewCurrentMedication, recyclerViewPastMedication;
    private CurrentMedicationList mCurrentMedicationAdapter;
    private PastMedicationList mPastMedicationAdapter;
    TabLayout tabLayout;
    List<Patient> patientNames = new ArrayList<>();

    Patient mCurrentPatient;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_medication, container, false);
//this control item spinner
        patientSpinner = rootView.findViewById(R.id.medication_patient_list_spinner);
        getPatientList();

        //Search for the Recyclerviews associated with past/current list displays
        recyclerViewCurrentMedication = rootView.findViewById(R.id.RecyclerView_current_medication);
        recyclerViewPastMedication    = rootView.findViewById(R.id.RecyclerView_past_medication);

        //Possibly initialize buttons for modifying and adding stuff to firebase, Using buttons
        mCurrentMedicationAddEditButton = rootView.findViewById(R.id.current_medication_edit);

        recyclerViewCurrentMedication.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewPastMedication.setLayoutManager(new LinearLayoutManager(getActivity()));

        tabLayout = rootView.findViewById(R.id.medicationTabs);
        tabLayout.getTabAt(0).select();

patientSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //retrieves the patient at the given spinner position
        mCurrentPatient = (Patient) parent.getItemAtPosition(position);
        String ID = mCurrentPatient.getDocumentId();
        //uses patient id from spinner and to display current illnesses
        patientsRef.document(ID).get().addOnSuccessListener(documentSnapshot ->  {

                List<String> currMedication = (List<String>) documentSnapshot.get("currentMedications");
                List<Medication> currMedicationItems = new ArrayList<>();
                //used to check if array is empty or not
                if (currMedication != null) {
                    for (int i = 0; i < currMedication.size(); i++) {
                        Medication cMeds = new Medication();
                        cMeds.setCurrentMedications(currMedication.get(i));
                        currMedicationItems.add(cMeds);
                    }
                }
                mCurrentMedicationAdapter = new CurrentMedicationList(getActivity(), currMedicationItems);
                recyclerViewCurrentMedication.setAdapter(mCurrentMedicationAdapter);

                // On item list clicked
            mCurrentMedicationAdapter.setOnItemClickListener(new CurrentMedicationList.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, Medication obj, int position) {
                        // Inside the click listener where you navigate to ProfilePatientFragment
                        Medication Meds = currMedicationItems.get(position);
                        MedicationFragmentDirections.ActionMedicationManagerFragmentToNavProfileCurrentMedication action =
                                MedicationFragmentDirections.actionMedicationManagerFragmentToNavProfileCurrentMedication(Meds);
                        Navigation.findNavController(view).navigate(action);
                    }
                });

                //Past medication history prescribed
                List<String> pastMedication = (List<String>) documentSnapshot.get("pastMedications");
                List<Medication> pastMedicationItems = new ArrayList<>();
                if (pastMedication != null) {
                    for (int i = 0; i < pastMedication.size(); i++) {
                        Medication pMeds = new Medication();
                        pMeds.setPastMedications(pastMedication.get(i));
                        pastMedicationItems.add(pMeds);
                    }
                }
                mPastMedicationAdapter = new PastMedicationList(getActivity(), pastMedicationItems);
                recyclerViewPastMedication.setAdapter(mPastMedicationAdapter);



        });


        SwipeItemTouchHelper swipeCurrentMedication = new SwipeItemTouchHelper(mCurrentMedicationAdapter);
        // Create an instance of ItemTouchHelper and attach SwipeItemTouchHelper to it
        ItemTouchHelper itemCurrentMedication = new ItemTouchHelper(swipeCurrentMedication);
        itemCurrentMedication.attachToRecyclerView(recyclerViewCurrentMedication);
        swipeCurrentMedication.setSwipeListener(new SwipeItemTouchHelper.SwipeListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onItemDismiss(int position) {

                Medication cMed = mCurrentMedicationAdapter.getHealthConditions().get(position);
                String removedMedication = mCurrentMedicationAdapter.getCurrentMedication().get(position).getCurrentMedications();
                AlertDialog.Builder confirmDelete = new AlertDialog.Builder(getContext());
                confirmDelete.setTitle("Confirm Deletion");
                confirmDelete.setMessage("Are you sure you want to remove this current medication?");
                confirmDelete.setPositiveButton("Yes", (dialog, which) -> {
                    // Remove the item from the list
                    mCurrentMedicationAdapter.getCurrentMedication().remove(position);
                    mCurrentMedicationAdapter.notifyItemRemoved(position);

                    // Get a reference to your Firestore collection
                    // CollectionReference patientsRef = FirebaseFirestore.getInstance().collection("patients");
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    DocumentReference patientRef = db.collection("patients").document(mCurrentPatient.getDocumentId());

                    patientRef.update("currentMedications", FieldValue.arrayRemove(removedMedication))
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(getContext(), "Medication Removed: " + removedMedication, Toast.LENGTH_SHORT).show();
                                dialog.dismiss();  // Dismiss the dialog after successful deletion
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getContext(), "Failed to remove medication", Toast.LENGTH_SHORT).show();
                                // Add the removed item back to the list if removal failed
                                mCurrentMedicationAdapter.getCurrentMedication().add(position, cMed);
                                mCurrentMedicationAdapter.notifyItemInserted(position);
                            });


                    AlertDialog.Builder confirmTransfer = new AlertDialog.Builder(getContext());
                    confirmTransfer.setTitle("Transfer");
                    confirmTransfer.setMessage("Would you like to move this to Past Medication history?");
                    confirmTransfer.setPositiveButton("Yes", (transferDialog, transferWhich) -> {
                        patientRef.update("pastMedications", FieldValue.arrayUnion(removedMedication))
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(getContext(), "Medication Removed: " + removedMedication, Toast.LENGTH_SHORT).show();
                                    transferDialog.dismiss(); // Dismiss the dialog after successful deletion
                                })


                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "Failed to remove Medication: " + e.getMessage());
                                    // If removal from database fails, add the item back to the list and notify the adapter
                                    mCurrentMedicationAdapter.getCurrentMedication().add(position, cMed);
                                    mCurrentMedicationAdapter.notifyItemInserted(position);
                                    Toast.makeText(getContext(), "Failed to remove medication: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                        refreshMedications();
                    });
                    confirmTransfer.setNegativeButton("No", (transferDialog, transferWhich) -> {
                        transferDialog.dismiss();
                    });
                    confirmTransfer.show();
                });
                confirmDelete.setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                    mCurrentMedicationAdapter.notifyDataSetChanged(); // Refresh the list after canceling
                });
                confirmDelete.setOnDismissListener(dialog -> {
                    mCurrentMedicationAdapter.notifyDataSetChanged();
                });
                confirmDelete.show();
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
                        mCurrentMedicationAddEditButton.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        recyclerViewCurrentMedication.setVisibility(View.GONE);
                        recyclerViewPastMedication.setVisibility(View.VISIBLE);
                        mCurrentMedicationAddEditButton.setVisibility(View.GONE);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // No action needed
                recyclerViewCurrentMedication.setVisibility(View.VISIBLE);
                recyclerViewPastMedication.setVisibility(View.GONE);
                mCurrentMedicationAddEditButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // No action needed
            }
        });

        mCurrentMedicationAddEditButton.setOnClickListener(v -> {
            Spinner medicationSelector = new Spinner(v.getContext());
            AlertDialog.Builder currentMedicationDialog = new AlertDialog.Builder(v.getContext());
            currentMedicationDialog.setTitle("Add a Medication");
            currentMedicationDialog.setMessage("Select the Medication you want to add:");
            currentMedicationDialog.setView(medicationSelector);


            //populate spinner for dialog
            CollectionReference ref = db.collection("medications");
            ref.get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        List<String> medicationList = new ArrayList<>();
                        for(QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots) {
                            String documentId = documentSnapshot.getId(); // Get the Document ID
                            medicationList.add(documentId); // Add Document ID to the list
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, medicationList);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        medicationSelector.setAdapter(adapter);
                    });

            medicationSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    currentMedicationSelector = (String) parent.getItemAtPosition(position);
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
            currentMedicationDialog.setPositiveButton("Add", (dialog, which) -> {
                DocumentReference documentReference = patientsRef.document(mCurrentPatient.getDocumentId());
                documentReference.get().addOnSuccessListener(documentSnapshot -> {
                    if(documentSnapshot.exists()){
                        documentReference.update("currentMedications", FieldValue.arrayUnion(currentMedicationSelector));
                    }
                    refreshMedications();
                });
            });
            currentMedicationDialog.setNegativeButton("Cancel", (dialog, which) -> {
                dialog.dismiss();
            });
            currentMedicationDialog.show();
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


    private void refreshMedications() {
        // Retrieve the updated list of specific allergies from the database
        FirebaseFirestore.getInstance().collection("patients").document(mCurrentPatient.getDocumentId())
                .get().addOnSuccessListener(documentSnapshot -> {
                    //current medications
                    List<String> currentMedication = (List<String>) documentSnapshot.get("currentMedications");
                    List<Medication> updatedCurrentMedication = new ArrayList<>();
                    if (currentMedication != null && !currentMedication.isEmpty()) {
                        for (int i = 0; i < currentMedication.size(); i++) {
                            Medication cMeds = new Medication();
                            cMeds.setCurrentMedications(currentMedication.get(i));
                            updatedCurrentMedication.add(cMeds);
                        }
                        mCurrentMedicationAdapter.updateCurrentMedication(updatedCurrentMedication);
                    }else {
                        // Create a dummy list if allergies are empty
                        List<String> tempdummyList = new ArrayList<>();
                        List<Medication> dummyList = new ArrayList<>();
                        tempdummyList.add("No Medications found");
                        Medication dummycMeds = new Medication();
                        dummycMeds.setCurrentMedications(tempdummyList.get(0));
                        dummyList.add(dummycMeds);
                        mCurrentMedicationAdapter.updateCurrentMedication(dummyList);
                    }

                    List<String> pastMedications = (List<String>) documentSnapshot.get("pastMedications");
                    List<Medication> updatedPastMedications = new ArrayList<>();
                    if (pastMedications != null && !pastMedications.isEmpty()) {
                        for (int i = 0; i < pastMedications.size(); i++) {
                            Medication cMeds = new Medication();
                            cMeds.setPastMedications(pastMedications.get(i));
                            updatedPastMedications.add(cMeds);
                        }

                        mPastMedicationAdapter.updatePastMedication(updatedPastMedications);
                    }else {
                        // Create a dummy list if allergies are empty
                        List<String> tempdummyList = new ArrayList<>();
                        List<Medication> dummyList = new ArrayList<>();
                        tempdummyList.add("No Medications found");
                        Medication dummycMeds = new Medication();
                        dummycMeds.setPastMedications(tempdummyList.get(0));
                        dummyList.add(dummycMeds);
                        mPastMedicationAdapter.updatePastMedication(dummyList);
                    }
                });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}