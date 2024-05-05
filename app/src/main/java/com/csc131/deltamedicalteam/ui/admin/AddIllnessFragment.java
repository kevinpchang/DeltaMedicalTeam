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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddIllnessFragment extends Fragment {

    private EditText mIllnessname;
    private EditText mDescription, mSymtom, mTreatment, mSeverity, mPrevention,mAssociatedCondition ;
    private Spinner mPermission;
    private MaterialRippleLayout mRegisterBtn;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fstore;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_addillness, container, false);




        mIllnessname = rootView.findViewById(R.id.add_illness_name);
        mDescription = rootView.findViewById(R.id.editTextTextMultiLine_description);
        mSymtom = rootView.findViewById(R.id.editTextTextMultiLine_symptoms);
        mTreatment = rootView.findViewById(R.id.editTextTextMultiLine_treatment);
        mSeverity = rootView.findViewById(R.id.editTextTextMultiLine_severity);
        mPrevention = rootView.findViewById(R.id.editTextTextMultiLine_prevention);
        mAssociatedCondition = rootView.findViewById(R.id.editTextTextMultiLine_AssociatedConditions);


        mRegisterBtn = rootView.findViewById(R.id.bt_create_account);
        progressBar = rootView.findViewById(R.id.progressBar);

        // Initialize Firebase
        fAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle registration logic

                String name = mIllnessname.getText().toString();
                String description = mDescription.getText().toString();
                String symptom = mSymtom.getText().toString();
                String treatment = mTreatment.getText().toString();
                String severity = mSeverity.getText().toString();
                String prevention = mPrevention.getText().toString();
                String asscon = mAssociatedCondition.getText().toString();

                if (TextUtils.isEmpty(name)) {
                    mIllnessname.setError("Name is Required.");
                    return;
                }

                if (TextUtils.isEmpty(description)) {
                    mDescription.setError("Description is Required.");
                    return;
                }

                if (TextUtils.isEmpty(symptom)) {
                    mSymtom.setError("Symptom is Required.");
                    return;
                }

                if (TextUtils.isEmpty(treatment)) {
                    mTreatment.setError("Treatment is Required.");
                    return;
                }

                if (TextUtils.isEmpty(severity)) {
                    mSeverity.setError("Severity is Required.");
                    return;
                }

//                if (TextUtils.isEmpty(prevention)) {
//                    mPrevention.setError("Prevention is Required.");
//                    return;
//                }
//
//                if (TextUtils.isEmpty(asscon)) {
//                    mAssociatedCondition.setError("Associated Condition is Required.");
//                    return;
//                }

                progressBar.setVisibility(View.VISIBLE);

                // Check if illness name already exists
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("illnesses")
                        .document(name)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.exists()) {
                                    // Illness with this name already exists
                                    Toast.makeText(requireContext(), "Illness with this name already exists.", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                } else {
                                    // Illness does not exist, add it to Firestore
                                    Map<String, Object> illness = new HashMap<>();
                                    illness.put("description", description);
                                    illness.put("symptom", symptom);
                                    illness.put("treatment", treatment);
                                    illness.put("severity", severity);
                                    illness.put("prevention", prevention);
                                    illness.put("associated_condition", asscon);

                                    // Add a document with the illness name as the document ID
                                    db.collection("illnesses")
                                            .document(name)
                                            .set(illness)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    // Document successfully written
                                                    Toast.makeText(requireContext(), "Illness added successfully.", Toast.LENGTH_SHORT).show();
                                                    progressBar.setVisibility(View.GONE);
                                                    // Navigate to the destination fragment
                                                    Navigation.findNavController(v).navigate(R.id.illnessManagerFragment);
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


