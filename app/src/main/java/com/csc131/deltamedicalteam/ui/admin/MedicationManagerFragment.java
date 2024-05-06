package com.csc131.deltamedicalteam.ui.admin;

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
import com.csc131.deltamedicalteam.adapter.StringList;
import com.csc131.deltamedicalteam.helper.SwipeItemTouchHelper;
import com.csc131.deltamedicalteam.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MedicationManagerFragment extends Fragment {
    private static final String TAG = "MedicationManagerFragment";
    private RecyclerView recyclerView;
    private StringList mAdapter;

    private List<String> medicationList = new ArrayList<>();
    private SearchView searchView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_medication_manager, container, false);

        recyclerView = view.findViewById(R.id.medication_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        searchView = view.findViewById(R.id.searchView);
        searchView.clearFocus();

        initComponent();

        // Find the Button and set its click listener
        Button btnAddMedication = view.findViewById(R.id.add_medication_button);
        btnAddMedication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the destination fragment
                Navigation.findNavController(v).navigate(R.id.action_medicationManagerFragment_to_nav_add_medication);
            }
        });

        return view;
    }

    private void initComponent() {
        // Initialize Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Reference to the "medications" collection
        CollectionReference medicationsRef = db.collection("medications");

        // Query to get all documents from the "medications" collection
        medicationsRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    medicationList = new ArrayList<>();
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        String documentId = documentSnapshot.getId(); // Get the Document ID
                        medicationList.add(documentId); // Add Document ID to the list
                    }
                    mAdapter = new StringList(getActivity(), medicationList);
                    recyclerView.setAdapter(mAdapter);

                    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String query) {
                            Log.d("MedicationManagerFragment", "Query submitted: " + query);
                            return false;
                        }

                        @Override
                        public boolean onQueryTextChange(String newText) {
                            Log.d("MedicationManagerFragment", "Query changed: " + newText);
                            filterList(newText);
                            return true;
                        }
                    });

                    SwipeItemTouchHelper swipeItemTouchHelper = new SwipeItemTouchHelper(mAdapter);
                    // Create an instance of ItemTouchHelper and attach SwipeItemTouchHelper to it
                    ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeItemTouchHelper);
                    itemTouchHelper.attachToRecyclerView(recyclerView);
                    swipeItemTouchHelper.setSwipeListener(new SwipeItemTouchHelper.SwipeListener() {
                        @SuppressLint("NotifyDataSetChanged")
                        @Override
                        public void onItemDismiss(int position) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setTitle("Confirm Deletion");
                            builder.setMessage("Are you sure you want to remove this Medication?");
                            builder.setPositiveButton("Yes", (dialog, which) -> {
                                // Remove the item from the list
                                String removedMedication = mAdapter.getItem().get(position); // Get the Medication at the specified position
                                Log.d(TAG, "Medication name removed: " + removedMedication);
                                mAdapter.getItem().remove(position); // Remove the Medication from the list
                                mAdapter.notifyItemRemoved(position); // Notify the adapter about the removal

                                // Additional logic to handle the removal from the database (already implemented in your code)
                                DocumentReference medicationRef = medicationsRef.document(removedMedication);
                                medicationRef.delete()
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(getContext(), "Medication Removed: " + removedMedication, Toast.LENGTH_SHORT).show();
                                            dialog.dismiss(); // Dismiss the dialog after successful deletion

                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e(TAG, "Failed to remove Medication: " + e.getMessage());
                                            // If removal from database fails, add the item back to the list and notify the adapter
                                            mAdapter.getItem().add(position, removedMedication);
                                            mAdapter.notifyItemInserted(position);
                                            Toast.makeText(getContext(), "Failed to remove Medication: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        });
                            });

                            builder.setNegativeButton("Cancel", (dialog, which) -> {
                                dialog.dismiss();
                                mAdapter.notifyDataSetChanged(); // Refresh the list after canceling
                            });
                            builder.setOnDismissListener(dialog -> mAdapter.notifyDataSetChanged());
                            builder.show();
                        }
                    });
                } else {
                    Log.d(TAG, "No documents found.");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Error fetching documents: " + e.getMessage());
            }
        });
    }

    public void filterList(String text) {
        List<String> filteredList = new ArrayList<>();

        for (String data : medicationList) {
            // Check if any field matches the query
            if (data.toLowerCase().contains(text.toLowerCase())) {
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
