package com.csc131.deltamedicalteam.ui.profile;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.csc131.deltamedicalteam.R;
import com.csc131.deltamedicalteam.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileMedicationFragment extends Fragment {
    FirebaseFirestore fStore;
    String medDescription, medDosage, medFrequency;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        com.csc131.deltamedicalteam.databinding.FragmentProfileUserBinding binding = com.csc131.deltamedicalteam.databinding.FragmentProfileUserBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Retrieve the passed user information
        ProfileMedicationFragmentArgs args = ProfileMedicationFragmentArgs.fromBundle(getArguments());
        String med = args.getMedication();


        fStore = FirebaseFirestore.getInstance();

//        ImageButton mPrint = view.findViewById(R.id.imageButton_medicationPrint);

        // Display the user information
        TextView nameTextView = view.findViewById(R.id.profile_medicaitonName);
        TextView descriptionTextView = view.findViewById(R.id.textview_description);
        TextView dosageTextView = view.findViewById(R.id.textview_medicationDosage);
        TextView frequencyTextView = view.findViewById(R.id.textview_medicationFrequency);


        EditText descriptionEditText = view.findViewById(R.id.editTextTextMultiLine_medicationDescription);
        EditText dosageEditText = view.findViewById(R.id.editTextTextMultiLine_medicationDosage);
        EditText frequencyEditText = view.findViewById(R.id.editTextTextMultiLine_medicationFrequency);


        Button mEditButton = view.findViewById(R.id.medicationProfile_edit_btn);
        Button mSaveButton = view.findViewById(R.id.medicationProfile_save_btn);



        DocumentReference MedRef = fStore.collection("medications").document(med);
        MedRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    // Access individual fields
                    medDescription = documentSnapshot.getString("description");
                    medDosage = documentSnapshot.getString("dosage");
                    medFrequency = documentSnapshot.getString("frequency");

                    // Now you can use the retrieved data as needed
                    // For example, you can display it in your UI or perform further processing

                    // Set user information in TextViews
                    nameTextView.setText(med);
                    descriptionTextView.setText(medDescription);
                    dosageTextView.setText(medDosage);
                    frequencyTextView.setText(medFrequency);

//        nameEditText.setText(med);
                    descriptionEditText.setText(medDescription);
                    dosageEditText.setText(medDosage);
                    frequencyEditText.setText(medFrequency);
                } else {
                    // Document does not exist
                    Log.d(TAG, "No such document");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle errors
                Log.e(TAG, "Error fetching document: " + e.getMessage());
            }
        });




        mEditButton.setOnClickListener(v -> {
            // Show EditText fields and Save button, hide TextViews and Edit button

//            memberIDTextView.setVisibility(View.GONE);
            descriptionTextView.setVisibility(View.GONE);
            dosageTextView.setVisibility(View.GONE);
            frequencyTextView.setVisibility(View.GONE);


            descriptionEditText.setVisibility(View.VISIBLE);
            dosageEditText.setVisibility(View.VISIBLE);
            frequencyEditText.setVisibility(View.VISIBLE);

            mEditButton.setVisibility(View.GONE);
            mSaveButton.setVisibility(View.VISIBLE);

        });

        mSaveButton.setOnClickListener(v -> {
            // Update Firestore with new values

            String newDescription= descriptionEditText.getText().toString();
            String newDosage = dosageEditText.getText().toString();
            String newFrequency = frequencyEditText.getText().toString();

            DocumentReference medicationRef = fStore.collection("medications").document(med);
            medicationRef.update(
                            "description",newDescription,
                            "dosage", newDosage,
                            "frequency", newFrequency)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Medication updated successfully", Toast.LENGTH_SHORT).show();
                        // Hide EditText fields and Save button, show TextViews and Edit button

                        descriptionTextView.setText(newDescription);
                        dosageTextView.setText(newDosage);
                        frequencyTextView.setText(newFrequency);


                        descriptionTextView.setVisibility(View.VISIBLE);
                        dosageTextView.setVisibility(View.VISIBLE);
                        frequencyTextView.setVisibility(View.VISIBLE);


                        descriptionEditText.setVisibility(View.GONE);
                        dosageEditText.setVisibility(View.GONE);
                        frequencyEditText.setVisibility(View.GONE);

                        mEditButton.setVisibility(View.VISIBLE);
                        mSaveButton.setVisibility(View.GONE);

                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firestore Error", "Error updating document: " + e.getMessage());
                        Toast.makeText(getContext(), "Failed to update profile", Toast.LENGTH_SHORT).show();
                    });
        });
    }

        public void onDestroyView() {
        super.onDestroyView();
    }
}