package com.csc131.deltamedicalteam.ui.patient;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.csc131.deltamedicalteam.R;
import com.csc131.deltamedicalteam.adapter.PatientList;
import com.csc131.deltamedicalteam.helper.SwipeItemTouchHelper;
import com.csc131.deltamedicalteam.model.HealthConditions;
import com.csc131.deltamedicalteam.model.Patient;
import com.csc131.deltamedicalteam.model.User;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class PatientManagerFragment extends Fragment {
    private static final String TAG = "PatientManagerFragment";
    private RecyclerView recyclerView;
    private PatientList mAdapter;
    private List<Patient> items = new ArrayList<>();
    private SearchView searchView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_patient_manager, container, false);

        recyclerView = view.findViewById(R.id.patient_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        searchView = view.findViewById(R.id.searchView);
        searchView.clearFocus();

        initComponent();

        // Find the Button and set its click listener
        Button btnAddPatient = view.findViewById(R.id.add_patient_button);
        btnAddPatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the destination fragment
                Navigation.findNavController(v).navigate(R.id.action_patientManagerFragment_to_nav_add_patient);
            }
        });

        SwipeItemTouchHelper swipeCurrentIllness = new SwipeItemTouchHelper(mAdapter);
        // Create an instance of ItemTouchHelper and attach SwipeItemTouchHelper to it
        ItemTouchHelper itemPatient = new ItemTouchHelper(swipeCurrentIllness);
        itemPatient.attachToRecyclerView(recyclerView);
        swipeCurrentIllness.setSwipeListener(new SwipeItemTouchHelper.SwipeListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onItemDismiss(int position) {
                Patient currentPatient = mAdapter.getPatients().get(position);
                String patientId = mAdapter.getPatients().get(position).getDocumentId();
                String removedPatient = mAdapter.getPatients().get(position).toString();
                AlertDialog.Builder confirmDelete = new AlertDialog.Builder(getContext());
                confirmDelete.setTitle("Confirm Deletion");
                confirmDelete.setMessage("Are you sure you want to remove this Patient?");
                confirmDelete.setPositiveButton("Yes", (dialog, which) -> {
                    // Remove the item from the list
                    mAdapter.getPatients().remove(position);
                    mAdapter.notifyItemRemoved(position);
                    // Remove the item from the database
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    DocumentReference patientRef = db.collection("patients").document(patientId);
                    patientRef.delete()
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(getContext(), "Allergy Removed: " + removedPatient, Toast.LENGTH_SHORT).show();
                                dialog.dismiss(); // Dismiss the dialog after successful deletion
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Failed to remove allergy: " + e.getMessage());
                                // If removal from database fails, add the item back to the list and notify the adapter
                                mAdapter.getPatients().add(position, currentPatient);
                                mAdapter.notifyItemInserted(position);
                                Toast.makeText(getContext(), "Failed to remove allergy: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                });
                confirmDelete.setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                    mAdapter.notifyDataSetChanged(); // Refresh the list after canceling
                });
                confirmDelete.setOnDismissListener(dialog -> {
                    mAdapter.notifyDataSetChanged();
                });
                confirmDelete.show();
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
                items = new ArrayList<>();

                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    // Convert DocumentSnapshot to Patient object
                    Patient patient = documentSnapshot.toObject(Patient.class);
                    patient.setDocumentId(documentSnapshot.getId());


                    // Add the patient to the list
                    items.add(patient);
                }

                // Set data and list adapter
                mAdapter = new PatientList(getActivity(), items);
                recyclerView.setAdapter(mAdapter);

                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        Log.d("PatientManagerFragment", "Query submitted: " + query);
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        Log.d("PatientManagerFragment", "Query changed: " + newText);
                        filterList(newText);
                        return true;
                    }
                });

                // On item list clicked
                mAdapter.setOnItemClickListener(new PatientList.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, Patient obj, int position) {
                        // Inside the click listener where you navigate to ProfilePatientFragment
                        Patient selectedPatient = items.get(position);
                        PatientManagerFragmentDirections.ActionPatientManagerFragmentToNavProfilePatient action =
                                PatientManagerFragmentDirections.actionPatientManagerFragmentToNavProfilePatient(selectedPatient);
                        Navigation.findNavController(view).navigate(action);
                    }
                });
            } else {
                Log.d(TAG, "No documents found.");
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Error fetching documents: " + e.getMessage());
        });
    }

    public void filterList(String text) {
        List<Patient> filteredList = new ArrayList<>();

        for (Patient data : items) {
            // Check if any field matches the query
            if (data.getName().toLowerCase().contains(text.toLowerCase()) ||
                    data.getEmail().toLowerCase().contains(text.toLowerCase()) ||
                    data.getCellPhone().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(data);
            }
        }

        if (filteredList.isEmpty()) {
            Toast.makeText(getContext(),"No data found", Toast.LENGTH_SHORT).show();
        } else {
            mAdapter.setFilteredList(filteredList);

        }

    }


}
