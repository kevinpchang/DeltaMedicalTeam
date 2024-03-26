package com.csc131.deltamedicalteam.ui.patient;

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
import com.csc131.deltamedicalteam.model.Patient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddPatientFragment extends Fragment {

    private EditText mFname, mMname, mLname, mAddress, mDOB, mBloodGroup, mRHfactor, mMaritalStatus, mPhone, mEmail, mEmergencyName, mEmergencyPhone;
    private Spinner mPermission;
    private MaterialRippleLayout mRegisterBtn;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fstore;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_addpatient, container, false);

        // Prepare the data source (list of suggestions)
        String[] items = {"Doctor", "Nurse"};

        // Create the adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mPermission = rootView.findViewById(R.id.spinner_permission);
        mPermission.setAdapter(adapter);

        //initialize input fields and ui
        mFname = rootView.findViewById(R.id.add_patient_fName);
        mMname = rootView.findViewById(R.id.add_patient_mName);
        mLname = rootView.findViewById(R.id.add_patient_lName);

        mAddress = rootView.findViewById(R.id.add_patient_address);
        mDOB = rootView.findViewById(R.id.add_patient_dob);
        mBloodGroup = rootView.findViewById(R.id.add_patient_bloodGroup);
        mRHfactor = rootView.findViewById(R.id.add_patient_rhFactor);
        mMaritalStatus = rootView.findViewById(R.id.add_patient_maritalStatus);

        mPhone = rootView.findViewById(R.id.add_patient_phone);
        mEmail = rootView.findViewById(R.id.add_patient_email);
        mEmergencyName = rootView.findViewById(R.id.add_patient_eContactName);
        mEmergencyPhone = rootView.findViewById(R.id.add_patient_eContactPhone);

        mRegisterBtn = rootView.findViewById(R.id.bt_create_patient);
        progressBar = rootView.findViewById(R.id.progressBar);

        // Initialize Firebase
        fAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle patient add logic
                String fName = mFname.getText().toString();
                String mName = mMname.getText().toString();
                String lName = mLname.getText().toString();

                String address = mAddress.getText().toString();
                String dob = mDOB.getText().toString();
                String bloodGroup = mBloodGroup.getText().toString();
                String rhFactor = mRHfactor.getText().toString();
                String maritalStatus = mMaritalStatus.getText().toString();

                String phone = mPhone.getText().toString();
                String email = mEmail.getText().toString().trim();
                String emergencyName = mEmergencyName.getText().toString().trim();
                String emergencyPhone = mEmergencyPhone.getText().toString().trim();

                if (TextUtils.isEmpty(fName)) {
                    mFname.setError("Field is Required.");
                    return;
                }

                if (TextUtils.isEmpty(mName)) {
                    mMname.setError("Field is Required.");
                    return;
                }

                if (TextUtils.isEmpty(lName)) {
                    mLname.setError("Field is Required.");
                    return;
                }

                if (TextUtils.isEmpty(address)) {
                    mAddress.setError("Field is Required.");
                    return;
                }

                if (TextUtils.isEmpty(dob)) {
                    mDOB.setError("Field is Required.");
                    return;
                }

                if (TextUtils.isEmpty(bloodGroup)) {
                    mBloodGroup.setError("Field is Required.");
                    return;
                }

                if (TextUtils.isEmpty(rhFactor)) {
                    mRHfactor.setError("Field is Required.");
                    return;
                }

                if (TextUtils.isEmpty(maritalStatus)) {
                    mMaritalStatus.setError("Field is Required.");
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Field is Required.");
                    return;
                }

                if (TextUtils.isEmpty(phone)) {
                    mPhone.setError("Field is Required.");
                    return;
                }

                if (TextUtils.isEmpty(emergencyName)) {
                    mEmergencyName.setError("Field is Required.");
                    return;
                }

                if (TextUtils.isEmpty(emergencyPhone)) {
                    mEmergencyPhone.setError("com.csc131.deltamedicalteam.model.Patient.Phone is required.");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                // Register the patient in Firebase
                addPatient(fName, mName, lName, address, dob, bloodGroup, rhFactor, maritalStatus, phone, email, emergencyName, emergencyPhone);

            }
        });


        return rootView;
    }

    private void addPatient( String fName, String mName, String lName, String address, String dob, String bloodGroup, String rhFactor, String maritalStatus, String phone2, String email, String emergencyName, String emergencyPhone){
        CollectionReference documentReference = fstore.collection("patients");
        Map<String, Object> patientData = new HashMap<>();
        patientData.put("fName", fName);
        patientData.put("mName", mName);
        patientData.put("lName", lName);
        patientData.put("address", address);
        patientData.put("dob", dob);
        patientData.put("bloodGroup", bloodGroup);
        patientData.put("rhFactor", rhFactor);
        patientData.put("maritalStatus", maritalStatus);
        patientData.put("phone number", phone2);
        patientData.put("email", email);
        patientData.put("emergencyName", emergencyName);
        patientData.put("emergencyPhone", emergencyPhone);
        documentReference.add(patientData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(requireContext(), "Patient Profile created for " + fName + " " + lName, Toast.LENGTH_SHORT).show();
                Navigation.findNavController(getView()).navigate(R.id.patientManagerFragment);
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(requireContext(), "Failed to create Patient Profile ", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}



