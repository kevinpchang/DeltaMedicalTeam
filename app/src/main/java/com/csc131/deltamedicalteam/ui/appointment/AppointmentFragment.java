package com.csc131.deltamedicalteam.ui.appointment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.balysv.materialripple.MaterialRippleLayout;
import com.csc131.deltamedicalteam.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class AppointmentFragment extends Fragment {

    private EditText mFullname, mEmail, mPhone;
    private Spinner mPermission;
    private MaterialRippleLayout mRegisterBtn;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fstore;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_addappointment, container, false);

        // Prepare the data source (list of suggestions)
        String[] purposeItems  = {"Medical Consultation", "Follow-up Visit", "Prescription Refill", "Diagnostic Tests", "Specialist Referral", "Vaccination", "Health Advice", "Chronic Condition Management", "Preventive Care", "Health Education" };


        // Create the adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, purposeItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mPermission = rootView.findViewById(R.id.appointment_purpose_spinner);
        mPermission.setAdapter(adapter);


//        mFullname = rootView.findViewById(R.id.add_patient_fName);
//        mPhone = rootView.findViewById(R.id.phone);
//        mEmail = rootView.findViewById(R.id.add_patient_address);
//        mRegisterBtn = rootView.findViewById(R.id.bt_create_account);
        progressBar = rootView.findViewById(R.id.progressBar);

        // Initialize Firebase
        fAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();

//        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Handle registration logic
//                String email = mEmail.getText().toString().trim();
//
//                String fullName = mFullname.getText().toString();
//                String phone = mPhone.getText().toString();
//                String permission = (String) mPermission.getSelectedItem();
//
//
//                if (TextUtils.isEmpty(email)) {
//                    mEmail.setError("Email is Required.");
//                    return;
//                }
//
//                if (TextUtils.isEmpty(fullName)) {
//                    mFullname.setError("Name is Required.");
//                    return;
//                }
//
//                if (TextUtils.isEmpty(phone)) {
//                    mPhone.setError("com.csc131.deltamedicalteam.model.Patient.Phone is Required.");
//                    return;
//                }
//
//
//
//                progressBar.setVisibility(View.VISIBLE);
//
//            }
//        });


        return rootView;
    }





}


