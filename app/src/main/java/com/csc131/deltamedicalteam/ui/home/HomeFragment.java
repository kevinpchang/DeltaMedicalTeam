package com.csc131.deltamedicalteam.ui.home;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.csc131.deltamedicalteam.MainActivity;
import com.csc131.deltamedicalteam.R;
import com.csc131.deltamedicalteam.databinding.FragmentHomeBinding;
import com.csc131.deltamedicalteam.utils.Tools;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private FirebaseFirestore db;
    TextView patientCountTextView;
    TextView appointmentCountTextView,userCountTextView, userName;

    String userID;
    FirebaseAuth fAuth;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        // Initialize FirebaseFirestore instance
        db = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        userID = fAuth.getCurrentUser().getUid();
        // Initialize the TextView
        patientCountTextView = root.findViewById(R.id.home_patientCount);
        appointmentCountTextView = root.findViewById(R.id.home_appointmentCount);
        userCountTextView = root.findViewById(R.id.home_userCount);
        userName = root.findViewById(R.id.user_name);

        CardView userContent = root.findViewById(R.id.home_userContent);

        Tools.checkAdminPermission(new MainActivity.PermissionCallback() {
            @Override
            public void onPermissionCheck(boolean isAdmin) {
                Log.e(TAG, "isAdmin: " + isAdmin);
                if (!isAdmin) {
                    // Update UI from UI thread
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Set visibility of userContent to VISIBLE
                            userContent.setVisibility(View.VISIBLE);
                        }
                    });
                    Log.e(TAG, "userContent is visible");
                }
            }
        });

        DocumentReference documentReference = db.collection("users").document(userID);
        documentReference.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {

                String fName = documentSnapshot.getString("fName");
                String lName = documentSnapshot.getString("lName");
                String permission = documentSnapshot.getString("permission");

                String fullName = fName + " " + lName;

                // Extract first three characters of permission and last five characters of userID
                String memberID = (permission.substring(0, Math.min(permission.length(), 3)) +
                        userID.substring(userID.length() - 5)).toUpperCase();

                userName.setText(permission + " - " + fullName);

            }
        });

        // Fetch patient count from the database and update the TextView
        fetchCount();

        return root;
    }

    private void fetchCount() {
        // Query to get the count of documents in the "patients" collection
        db.collection("patients").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // Get the count of documents
                    int count = task.getResult().size();

                    // Update the TextView with the fetched patient count
                    patientCountTextView.setText("Patient: " + count);
                } else {
                    // Handle errors
                    patientCountTextView.setText("Patient count could not be retrieved");
                }
            }
        });

        db.collection("appointments").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // Get the count of documents
                    int count = task.getResult().size();

                    // Update the TextView with the fetched patient count
                    appointmentCountTextView.setText("Appointment: " + count);
                } else {
                    // Handle errors
                    appointmentCountTextView.setText("Appointment count could not be retrieved");
                }
            }
        });

        db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // Get the count of documents
                    int count = task.getResult().size();

                    // Update the TextView with the fetched patient count
                    userCountTextView.setText("User: " + count);
                } else {
                    // Handle errors
                    userCountTextView.setText("User count could not be retrieved");
                }
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}