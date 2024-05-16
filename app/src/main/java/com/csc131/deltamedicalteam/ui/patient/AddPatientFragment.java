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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddPatientFragment extends Fragment {

    private EditText mFname, mMname, mLname, mAddress, mDOB, mBloodGroup, mRHfactor, mPhone, mEmail, mEmergencyName, mEmergencyPhone;
    private Spinner mSex;
    private Spinner mMaritalStatus;

    private MaterialRippleLayout mRegisterBtn;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fstore;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_addpatient, container, false);

        // Prepare the data source (list of suggestions)
        String[] maritalStatusItems = {"Unknown", "Single", "Married"};
        String[] sexItems = {"Male", "Female"};

        // Create the adapter
        ArrayAdapter<String> maritalStatusAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, maritalStatusItems);
        maritalStatusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mMaritalStatus = rootView.findViewById(R.id.spinner_marital_status);
        mMaritalStatus.setAdapter(maritalStatusAdapter);

        ArrayAdapter<String> sexAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, sexItems);
        sexAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSex = rootView.findViewById(R.id.spinner_sex);
        mSex.setAdapter(sexAdapter);

        //initialize input fields and ui
        mFname = rootView.findViewById(R.id.add_patient_fName);
        mLname = rootView.findViewById(R.id.add_patient_lName);

        mAddress = rootView.findViewById(R.id.add_patient_address);
        mDOB = rootView.findViewById(R.id.add_patient_dob);

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
                String lName = mLname.getText().toString();

                String address = mAddress.getText().toString();
                String dob = mDOB.getText().toString();
                String sex = (String) mSex.getSelectedItem();
                String maritalStatus = (String) mMaritalStatus.getSelectedItem();

                String phone = mPhone.getText().toString();
                String email = mEmail.getText().toString().trim();
                String emergencyName = mEmergencyName.getText().toString().trim();
                String emergencyPhone = mEmergencyPhone.getText().toString().trim();

                if (TextUtils.isEmpty(fName)) {
                    mFname.setError("Field is Required.");
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

                if (!isValidDateOfBirth(dob)) {
                    mDOB.setError("Invalid date format. Please use MM-DD-YYYY format.");
                    return;
                }

//

                if (maritalStatus.equals("Unknown")) {
                    Toast.makeText(requireContext(), "Marital Status is Required" , Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Field is Required.");
                    return;
                }

                if (!isValidEmail(email)) {
                    mEmail.setError("Invalid email format");
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
//                addPatient(fName, mName, lName, address, dob, bloodGroup, rhFactor, maritalStatus, phone, email, emergencyName, emergencyPhone);
                addPatient(fName, lName, address, sex, dob, maritalStatus, phone, email, emergencyName, emergencyPhone);

            }
        });


        return rootView;
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidDateOfBirth(String dob) {
        // Define the desired date format (MM-DD-YYYY)
        String dateFormat = "MM-dd-yyyy";
        // Set the date format and disable leniency to avoid accepting invalid dates (e.g., February 30th)
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
        sdf.setLenient(false);

        try {
            // Attempt to parse the input date
            Date parsedDate = sdf.parse(dob);
            // Check if the parsed date matches the input string, indicating a valid date format
            return sdf.format(parsedDate).equals(dob);
        } catch (ParseException e) {
            // Parsing failed, indicating an invalid date format
            return false;
        }
    }

//    private void addPatient( String fName, String mName, String lName, String address, String dob, String bloodGroup, String rhFactor, String maritalStatus, String phone2, String email, String emergencyName, String emergencyPhone){
    private void addPatient( String fName, String lName, String address, String sex, String dob, String maritalStatus, String phone, String email, String emergencyName, String emergencyPhone){
        CollectionReference documentReference = fstore.collection("patients");
        Map<String, Object> patientData = new HashMap<>();
        patientData.put("fName", fName);
        patientData.put("lName", lName);
        patientData.put("address", address);
        patientData.put("sex", sex);
        patientData.put("dob", dob);
        patientData.put("maritalStatus", maritalStatus);
        patientData.put("phone", phone);
        patientData.put("email", email);
        patientData.put("emergencyName", emergencyName);
        patientData.put("emergencyPhone", emergencyPhone);
        // Initialize nested objects and arrays

        patientData.put("specificAllergies", new ArrayList<>());
        patientData.put("currentMedications", new ArrayList<>());
        patientData.put("pastMedications", new ArrayList<>());
        patientData.put("currentIllnesses", new ArrayList<>());
        patientData.put("pastIllnesses", new ArrayList<>());
        documentReference.add(patientData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(requireContext(), "Patient Profile created for " + fName + " " + lName, Toast.LENGTH_SHORT).show();
                Navigation.findNavController(requireView()).navigate(R.id.patientManagerFragment);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(requireContext(), "Failed to create Patient Profile ", Toast.LENGTH_SHORT).show();
            }
        });
    }
}



