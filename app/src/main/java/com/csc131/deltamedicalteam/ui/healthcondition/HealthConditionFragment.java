package com.csc131.deltamedicalteam.ui.healthcondition;

<<<<<<< Updated upstream
=======
import static android.content.ContentValues.TAG;

import android.content.DialogInterface;
>>>>>>> Stashed changes
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
<<<<<<< Updated upstream
import android.widget.TextView;
=======
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
>>>>>>> Stashed changes

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
<<<<<<< Updated upstream
import androidx.lifecycle.ViewModelProvider;
=======
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.csc131.deltamedicalteam.R;
import com.csc131.deltamedicalteam.adapter.CurrentAllergiesList;
import com.csc131.deltamedicalteam.adapter.CurrentIllnessList;
import com.csc131.deltamedicalteam.model.HealthConditions;
import com.csc131.deltamedicalteam.model.Patient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
>>>>>>> Stashed changes

public class HealthConditionFragment extends Fragment {

    private com.csc131.deltamedicalteam.databinding.FragmentHealthConditionBinding binding;

<<<<<<< Updated upstream
=======
    // Initialize Firestore
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Reference to the "patients" collection
    private final CollectionReference patientsRef = db.collection("patients");
    private Button mAddEditButton;
    private Spinner patientSpinner;
    RecyclerView recyclerViewCurrentIllness, recyclerViewMedicalHistory, recyclerViewSpecificAllergies;
    TabLayout tabLayout;
    private CurrentIllnessList mAdapter;
    private CurrentAllergiesList mAllergiesAdapter;
    List<Patient> patientNames = new ArrayList<>();

>>>>>>> Stashed changes
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HealthConditionViewModel homeViewModel =
                new ViewModelProvider(this).get(HealthConditionViewModel.class);

        binding = com.csc131.deltamedicalteam.databinding.FragmentHealthConditionBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

<<<<<<< Updated upstream
        final TextView textView = binding.textHealthCondition;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

=======
        // Find RecyclerViews
        recyclerViewCurrentIllness = rootView.findViewById(R.id.RecyclerView_current_illness);
        recyclerViewMedicalHistory = rootView.findViewById(R.id.RecyclerView_medical_history);
        recyclerViewSpecificAllergies = rootView.findViewById(R.id.RecyclerView_specific_allergies);

        //button
        Button illnessButton = rootView.findViewById(R.id.current_illness_addbutton);
        illnessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a custom dialog
                AlertDialog.Builder IllnessDialog = new AlertDialog.Builder(v.getContext());
                View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.fragment_health_condition, null);
                IllnessDialog.setView(dialogView);
                EditText currentIllnessEditText = dialogView.findViewById(R.id.RecyclerView_current_illness);
                IllnessDialog.setTitle("Add/Edit Illness information");
                IllnessDialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String currentIllness = currentIllnessEditText.getText().toString();


                        // Save patient information to Firebase
                        DatabaseReference patientsRef = FirebaseDatabase.getInstance().getReference("patients");
                        String patientId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                        patientsRef.child(patientId).child("currentIllness").setValue(currentIllness);


                        Toast.makeText(v.getContext(), "Current Illness Saved Successfully", Toast.LENGTH_SHORT).show();
                    }
                });
                IllnessDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog = IllnessDialog.create();
                alertDialog.show();
            }
        });
        Button MedicalHistoryButton = rootView.findViewById(R.id.medical_history_addbutton);
        illnessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder medicalHistoryDialog = new AlertDialog.Builder(v.getContext());
                View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.fragment_health_condition, null);
                medicalHistoryDialog.setView(dialogView);

                EditText medicalHistoryEditText = dialogView.findViewById(R.id.RecyclerView_medical_history);
                medicalHistoryDialog.setTitle("Add/Edit Medical history information");

                medicalHistoryDialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String medicalHistory = medicalHistoryEditText.getText().toString();


                        // Save patient information to Firebase
                        DatabaseReference patientsRef = FirebaseDatabase.getInstance().getReference("patients");
                        String patientId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                        patientsRef.child(patientId).child("medicalHistory").setValue(medicalHistory);

                        Toast.makeText(v.getContext(), "Medical History Saved Successfully", Toast.LENGTH_SHORT).show();
                    }
                });
                medicalHistoryDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog = medicalHistoryDialog.create();
                alertDialog.show();
            }
        });
        Button SpecificAllergyButton = rootView.findViewById(R.id.specific_allergies_addbutton);
        illnessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a custom dialog
                AlertDialog.Builder AllergyDialog = new AlertDialog.Builder(v.getContext());
                View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.fragment_health_condition, null);
                AllergyDialog.setView(dialogView);

                EditText allergiesEditText = dialogView.findViewById(R.id.RecyclerView_specific_allergies);
                AllergyDialog.setTitle("Add/Edit Specific Allergy information");

                AllergyDialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String allergies = allergiesEditText.getText().toString();
                        DatabaseReference patientsRef = FirebaseDatabase.getInstance().getReference("patients");
                        String patientId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                         patientsRef.child(patientId).child("specificAllergies").setValue(allergies);
                        Toast.makeText(v.getContext(), "Medical History Saved Successfully", Toast.LENGTH_SHORT).show();
                    }
                });
                AllergyDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog = AllergyDialog.create();
                alertDialog.show();
            }
        });
        // Find TabLayout
        tabLayout = rootView.findViewById(R.id.healConditionTabs);

        recyclerViewCurrentIllness.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewSpecificAllergies.setLayoutManager(new LinearLayoutManager(getActivity()));

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

                        //currentAllegies

                        List<String> currAllergies = (List<String>) documentSnapshot.get("specificAllergies");
                        List<HealthConditions> allergiesitems = new ArrayList<>();
                        //used to check if array is empty or not
                        if (currAllergies != null) {
                            for (int i = 0; i < currAllergies.size(); i++) {
                                HealthConditions hCons = new HealthConditions();
                                hCons.setSpecificAllergies(currAllergies.get(i));
                                allergiesitems.add(hCons);
                            }
                        }
                        mAllergiesAdapter = new CurrentAllergiesList(getActivity(), allergiesitems);
                        recyclerViewSpecificAllergies.setAdapter(mAllergiesAdapter);
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

>>>>>>> Stashed changes
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}