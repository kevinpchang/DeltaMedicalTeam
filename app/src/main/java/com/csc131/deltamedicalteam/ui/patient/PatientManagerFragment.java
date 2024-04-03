package com.csc131.deltamedicalteam.ui.patient;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import java.util.Map;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.csc131.deltamedicalteam.R;
import com.csc131.deltamedicalteam.adapter.PatientList;
import com.csc131.deltamedicalteam.model.Patient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class PatientManagerFragment extends Fragment {
    private static final String TAG = "PatientManagerFragment";
    private RecyclerView recyclerView;
    private PatientList mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_patient_manager, container, false);

        recyclerView = view.findViewById(R.id.appointment_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        initComponent();

        // Find the Button and set its click listener
        Button btnAddPatient = view.findViewById(R.id.add_appointment_button);
        btnAddPatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the destination fragment
                Navigation.findNavController(v).navigate(R.id.action_patientManagerFragment_to_nav_add_patient);
            }
        });


        return view;
    }

    private void initComponent() {
        // Initialize Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Reference to the "patients" collection
        CollectionReference patientsRef = db.collection("patients");

        // Query to get all documents from the "patients" collection
        patientsRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()) {
                List<Patient> items = new ArrayList<>();
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    // Convert DocumentSnapshot to Patient object
                    Patient patient = documentSnapshot.toObject(Patient.class);

                    // Add the patient to the list
                    items.add(patient);
                }

                // Set data and list adapter
                mAdapter = new PatientList(getActivity(), items);
                recyclerView.setAdapter(mAdapter);

                // On item list clicked
                mAdapter.setOnItemClickListener(new PatientList.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, Patient obj, int position) {
                        // Inside the click listener where you navigate to ProfilePatientFragment
                        Patient selectedPatient = items.get(position);
                        /*PatientManagerFragmentDirections.ActionPatientManagerFragmentToNavProfilePatient action =
                                PatientManagerFragmentDirections.actionPatientManagerFragmentToNavProfilePatient(selectedPatient);
                        Navigation.findNavController(view).navigate(action);*/
                    }
                });
            } else {
                Log.d(TAG, "No documents found.");
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Error fetching documents: " + e.getMessage());
        });
    }


}
