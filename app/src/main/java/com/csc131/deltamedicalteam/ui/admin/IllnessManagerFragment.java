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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class IllnessManagerFragment extends Fragment {
    private static final String TAG = "IllnessManagerFragment";
    private RecyclerView recyclerView;
    private StringList mAdapter;
    private final List<String> items = new ArrayList<>();
    private SearchView searchView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_illness_manager, container, false);

        recyclerView = view.findViewById(R.id.illness_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        searchView = view.findViewById(R.id.searchView);
        searchView.clearFocus();

        initComponent();

        // Find the Button and set its click listener
        Button btnAddIllness = view.findViewById(R.id.add_illness_button);
        btnAddIllness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the destination fragment
                Navigation.findNavController(v).navigate(R.id.action_illnessManagerFragment_to_nav_add_illness);
            }
        });

        return view;
    }

    private void initComponent() {
        // Initialize Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Reference to the "illnesses" collection
        CollectionReference illnessesRef = db.collection("illnesses");

        // Query to get all documents from the "illnesses" collection
        illnessesRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    List<String> illnessList = new ArrayList<>();
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        String documentId = documentSnapshot.getId(); // Get the Document ID
                        illnessList.add(documentId); // Add Document ID to the list
                    }
                    mAdapter = new StringList(getActivity(), illnessList);
                    recyclerView.setAdapter(mAdapter);

                    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String query) {
                            Log.d("IllnessManagerFragment", "Query submitted: " + query);
                            return false;
                        }

                        @Override
                        public boolean onQueryTextChange(String newText) {
                            Log.d("IllnessManagerFragment", "Query changed: " + newText);
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
                            builder.setMessage("Are you sure you want to remove this Illness?");
                            builder.setPositiveButton("Yes", (dialog, which) -> {
                                // Remove the item from the list
                                String removedIllness = mAdapter.getCurrentMedication().get(position); // Get the Illness at the specified position
                                Log.d(TAG, "Illness name removed: " + removedIllness);
                                mAdapter.getCurrentMedication().remove(position); // Remove the Illness from the list
                                mAdapter.notifyItemRemoved(position); // Notify the adapter about the removal

                                // Additional logic to handle the removal from the database (already implemented in your code)
                                DocumentReference illnessRef = illnessesRef.document(removedIllness);
                                illnessRef.delete()
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(getContext(), "Illness Removed: " + removedIllness, Toast.LENGTH_SHORT).show();
                                            dialog.dismiss(); // Dismiss the dialog after successful deletion

                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e(TAG, "Failed to remove Illness: " + e.getMessage());
                                            // If removal from database fails, add the item back to the list and notify the adapter
                                            mAdapter.getCurrentMedication().add(position, removedIllness);
                                            mAdapter.notifyItemInserted(position);
                                            Toast.makeText(getContext(), "Failed to remove Illness: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

        for (String data : items) {
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
