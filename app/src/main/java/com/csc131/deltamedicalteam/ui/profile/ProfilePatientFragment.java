package com.csc131.deltamedicalteam.ui.profile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.csc131.deltamedicalteam.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;


public class ProfilePatientFragment extends Fragment {


    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    String userID;
    private com.csc131.deltamedicalteam.databinding.FragmentProfileBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = com.csc131.deltamedicalteam.databinding.FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();



        // Find the profileEmail view by its ID
        TextView emailProfile = root.findViewById(R.id.profile_email);
        TextView mName = root.findViewById(R.id.profile_name);
        TextView mPermission = root.findViewById(R.id.profile_permission);
        //init Database
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        userID = fAuth.getCurrentUser().getUid();

        //pulling bd from table users
        DocumentReference documentReference = fStore.collection("users").document(userID);

        // Create an EventListener<DocumentSnapshot> instance
        EventListener<DocumentSnapshot> eventListener = new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    // Handle any errors that occur during the retrieval of the document
                    Log.e("Firestore Error", "Error fetching user document: " + error.getMessage());
                    return;
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    // Document exists, retrieve and set the text of TextViews with user's information
                    String fullName = documentSnapshot.getString("fName");
                    String email = documentSnapshot.getString("email");
                    String permission = documentSnapshot.getString("permission");

                    // Set the text of emailProfile TextView
                    if (email != null) {
                        emailProfile.setText(email);
                    } else {
                        Log.e("Firestore Error", "Email is null");
                        emailProfile.setText("Click to setup Email"); //do this feature later
                    }

                    // Set the text of emailProfile TextView
                    if (fullName != null) {
                        mName.setText(fullName);
                    } else {
                        Log.e("Firestore Error", "fullName is null");
                        mName.setText("Click to setup fullName"); //do this feature later
                    }

                    // Set the text of emailProfile TextView
                    if (permission != null) {
                        mPermission.setText(permission);
                    } else {
                        Log.e("Firestore Error", "permission is null");
                        mPermission.setText("Click to setup permission"); //do this feature later
                    }

                    // Similarly, handle other fields like fullName, permission, etc.
                    // Make sure to handle null cases for other fields as well

                } else {
                    // Document does not exist or snapshot is null
                    Log.e("Firestore Error", "User document does not exist or snapshot is null");
                }
            }
        };

        // Add the event listener to the documentReference
        documentReference.addSnapshotListener(eventListener);


        // Now you can use emailProfile to work with the TextView

        return root;


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}