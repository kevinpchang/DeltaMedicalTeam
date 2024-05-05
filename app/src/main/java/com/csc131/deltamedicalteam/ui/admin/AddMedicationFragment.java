package com.csc131.deltamedicalteam.ui.admin;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.balysv.materialripple.MaterialRippleLayout;
import com.csc131.deltamedicalteam.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddMedicationFragment extends Fragment {

    private EditText mName, mDescription, mDosage, mFrequency;

    private MaterialRippleLayout mRegisterBtn;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fstore;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_addmedication, container, false);




        mName = rootView.findViewById(R.id.add_medication_name);
        mDescription = rootView.findViewById(R.id.editTextTextMultiLine_medicationDescription);
        mDosage = rootView.findViewById(R.id.add_medication_dosage);
        mFrequency = rootView.findViewById(R.id.add_medication_Frequency);
        mRegisterBtn = rootView.findViewById(R.id.bt_create_account);
        progressBar = rootView.findViewById(R.id.progressBar);

        // Initialize Firebase
        fAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = mName.getText().toString();
                String description = mDescription.getText().toString();
                String dosage = mDosage.getText().toString();
                String frequency = mFrequency.getText().toString();

                if (TextUtils.isEmpty(name)) {
                    mName.setError("Name is Required.");
                    return;
                }

                if (TextUtils.isEmpty(description)) {
                    mDescription.setError("Description is Required.");
                    return;
                }

                if (TextUtils.isEmpty(dosage)) {
                    mDosage.setError("Dosage is Required.");
                    return;
                }

                if (TextUtils.isEmpty(frequency)) {
                    mFrequency.setError("Frequency is Required.");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                // Check if illness name already exists
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("medications")
                        .document(name)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.exists()) {
                                    // Illness with this name already exists
                                    Toast.makeText(requireContext(), "Medication with this name already exists.", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                } else {
                                    // Illness does not exist, add it to Firestore
                                    Map<String, Object> illness = new HashMap<>();
                                    illness.put("description", description);
                                    illness.put("dosage", dosage);
                                    illness.put("frequency", frequency);


                                    // Add a document with the illness name as the document ID
                                    db.collection("medications")
                                            .document(name)
                                            .set(illness)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    // Document successfully written
                                                    Toast.makeText(requireContext(), "Medications added successfully.", Toast.LENGTH_SHORT).show();
                                                    progressBar.setVisibility(View.GONE);
                                                    // Navigate to the destination fragment
                                                    Navigation.findNavController(v).navigate(R.id.medicationManagerFragment);
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    // Error writing document
                                                    Toast.makeText(requireContext(), "Error adding illness: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    progressBar.setVisibility(View.GONE);
                                                }
                                            });


                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Error occurred while checking existence
                                Toast.makeText(requireContext(), "Error checking illness: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        });
            }
        });


        return rootView;
    }





}


