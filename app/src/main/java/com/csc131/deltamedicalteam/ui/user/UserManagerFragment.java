package com.csc131.deltamedicalteam.ui.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.csc131.deltamedicalteam.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.List;

public class UserManagerFragment extends Fragment {
    private RecyclerView userListRecyclerView;
    private UserAdapter userAdapter;
    private List<mUser> userList;

    private TextView userNameTextView;
    private Button addUserButton;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private ListenerRegistration userListener;

    public UserManagerFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_manager, container, false);

        // Initialize views
        userNameTextView = view.findViewById(R.id.user_name_text_view);
        addUserButton = view.findViewById(R.id.add_user_button);
        userListRecyclerView = view.findViewById(R.id.user_list);

        // Set up RecyclerView
        userList = new ArrayList<>();
        userListRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        userAdapter = new UserAdapter(userList);
        userListRecyclerView.setAdapter(userAdapter);

        // Set up Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Fetch current user information
        fetchAllUsersExceptCurrentUser();

        // Set click listener for add user button
        addUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement logic to add a new user
                // For example, navigate to a new user creation screen
            }
        });

        return view;
    }

    private void fetchAllUsersExceptCurrentUser() {
        if (currentUser != null) {
            db.collection("users")
                    .whereNotEqualTo(FieldPath.documentId(), currentUser.getUid())
                    .addSnapshotListener(requireActivity(), new com.google.firebase.firestore.EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                            if (error != null) {
                                // Handle error
                                return;
                            }

                            // Clear the existing user list
                            userList.clear();

                            // Add all users except the current user to the user list
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                mUser user = documentSnapshot.toObject(mUser.class);
                                userList.add(user);
                            }

                            // Notify the adapter that the data set has changed
                            userAdapter.notifyDataSetChanged();
                        }
                    });
        }
    }
    public class mUser {
        private String userId;
        private String userName;
        // Add other fields as needed

        // Required no-argument constructor
        public mUser() {
        }

        // Getter and setter methods for the fields
        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }
        // Add other getter and setter methods for other fields
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (userListener != null) {
            userListener.remove();
        }
    }
}


