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
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;

import com.csc131.deltamedicalteam.R;
import com.csc131.deltamedicalteam.adapter.UserList;
import com.csc131.deltamedicalteam.helper.SwipeItemTouchHelper;
import com.csc131.deltamedicalteam.model.Patient;
import com.csc131.deltamedicalteam.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class UserManagerFragment extends Fragment {
    private static final String TAG = "UserManagerFragment";
    private RecyclerView recyclerView;
    private UserList mAdapter;
    private List<User> items = new ArrayList<>();
    private SearchView searchView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_manager, container, false);

        recyclerView = view.findViewById(R.id.user_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        searchView = view.findViewById(R.id.searchView);
        searchView.clearFocus();


        initComponent();

        // Find the Button and set its click listener
        Button btnAddUser = view.findViewById(R.id.add_user_button);
        btnAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the destination fragment
                Navigation.findNavController(v).navigate(R.id.action_userManagerFragment_to_nav_add_user);
            }
        });

        // Implement search functionality




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
                    items = new ArrayList<>();
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        User user = documentSnapshot.toObject(User.class);

                        if (user != null) {
                            user.fromDocumentSnapshot(documentSnapshot);
                            // Fetch profile picture URL from the user object
                            String profilePictureUrl = user.getProfilePictureUrl();
                            // Set the profile picture URL to the user object
                            user.setProfilePictureUrl(profilePictureUrl);
                            // Add the user to the list
                            items.add(user);
                        }
                    }

                    // Set data and list adapter
                    mAdapter = new UserList(getActivity(), items);
                    recyclerView.setAdapter(mAdapter);

                    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String query) {
                            Log.d("UserManagerFragment", "Query submitted: " + query);
                            return false;
                        }

                        @Override
                        public boolean onQueryTextChange(String newText) {
                            Log.d("UserManagerFragment", "Query changed: " + newText);
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
                            builder.setMessage("Are you sure you want to remove this User?");
                            builder.setPositiveButton("Yes", (dialog, which) -> {

                                // Remove the item from the list
                                User removedUser = mAdapter.getUsers().get(position); // Get the User at the specified position
                                Log.d(TAG, "ID got: " + removedUser.getDocumentId());
                                mAdapter.getUsers().remove(position); // Remove the User from the list
                                mAdapter.notifyItemRemoved(position); // Notify the adapter about the removal


                                // Additional logic to handle the removal from the database (already implemented in your code)
                                FirebaseFirestore db = FirebaseFirestore.getInstance();

                                DocumentReference userRef = db.collection("users").document(removedUser.getDocumentId());
                                userRef.delete()
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(getContext(), "User Removed: " + removedUser.getDocumentId(), Toast.LENGTH_SHORT).show();
                                            dialog.dismiss(); // Dismiss the dialog after successful deletion

                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e(TAG, "Failed to remove User: " + e.getMessage());
                                            // If removal from database fails, add the item back to the list and notify the adapter
                                            mAdapter.getUsers().add(position, removedUser);
                                            mAdapter.notifyItemInserted(position);
                                            Toast.makeText(getContext(), "Failed to remove User: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        });
                                refreshUsers();
                            });

                            builder.setNegativeButton("Cancel", (dialog, which) -> {
                                dialog.dismiss();
                                mAdapter.notifyDataSetChanged(); // Refresh the list after canceling
                            });
                            builder.setOnDismissListener(dialog -> mAdapter.notifyDataSetChanged());
                            builder.show();

                        }
                    });

                    // On item list clicked
                    mAdapter.setOnItemClickListener(new UserList.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, User obj, int position) {
                            // Inside the click listener where you navigate to ProfileUserFragment
                            User selectedUser = items.get(position);
                            UserManagerFragmentDirections.ActionUserManagerFragmentToNavProfileUser action =
                                    UserManagerFragmentDirections.actionUserManagerFragmentToNavProfileUser(selectedUser);
                            Navigation.findNavController(view).navigate(action);
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
        List<User> filteredList = new ArrayList<>();

        for (User data : items) {
            // Check if any field matches the query
            if (data.getName().toLowerCase().contains(text.toLowerCase()) ||
                        data.getEmail().toLowerCase().contains(text.toLowerCase()) ||
                        data.getPhone().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(data);
            }
        }

        if (filteredList.isEmpty()) {
            Toast.makeText(getContext(),"No data found", Toast.LENGTH_SHORT).show();
        } else {
            mAdapter.setFilteredList(filteredList);

        }

    }

    private void refreshUsers() {
        // Retrieve the updated list of users from the database
        FirebaseFirestore.getInstance().collection("users")
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
                    List<User> updatedUsers = new ArrayList<>();
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        // Convert DocumentSnapshot to User object
                        User mUser = documentSnapshot.toObject(User.class);
                        // Call fromDocumentSnapshot() method to populate additional fields
                        assert mUser != null;
                        mUser.fromDocumentSnapshot(documentSnapshot);
                        // Add the User to the list
                        updatedUsers.add(mUser);
                    }
                    // Notify the adapter with the updated list of users
                    if (mAdapter != null) {
                        mAdapter.updateUsers(updatedUsers);
                    } else {
                        Log.e(TAG, "Adapter is null");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error getting Users: " + e.getMessage());
                });

    }


}
