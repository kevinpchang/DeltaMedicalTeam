package com.csc131.deltamedicalteam.ui.user;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.csc131.deltamedicalteam.MainActivity;
import com.csc131.deltamedicalteam.R;
import com.csc131.deltamedicalteam.adapter.AdapterListBasic;
import com.csc131.deltamedicalteam.model.People;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class UserManagerFragment extends Fragment {
    private static final String TAG = "UserManagerFragment";
    private RecyclerView recyclerView;
    private AdapterListBasic mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_manager, container, false);

        recyclerView = view.findViewById(R.id.user_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        initComponent();

        return view;
    }

    private void initComponent() {
        // Initialize Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Reference to the "users" collection
        CollectionReference usersRef = db.collection("users");

        // Query to get all documents from the "users" collection
        usersRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    List<People> items = new ArrayList<>();
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        // Retrieve user information from Firestore document
                        String name = documentSnapshot.getString("fName");
                        String email = documentSnapshot.getString("email");
                        String phoneNumber = documentSnapshot.getString("phone");
                        String permission = documentSnapshot.getString("permission");

                        // Create People object with user information
                        People people = new People(name, email, phoneNumber, permission, false);
                        items.add(people);
                    }

                    // Set data and list adapter
                    mAdapter = new AdapterListBasic(getActivity(), items);
                    recyclerView.setAdapter(mAdapter);

                    // On item list clicked
                    mAdapter.setOnItemClickListener(new AdapterListBasic.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, People obj, int position) {
                            Snackbar.make(view, "Item " + obj.name + " clicked", Snackbar.LENGTH_SHORT).show();
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

}
