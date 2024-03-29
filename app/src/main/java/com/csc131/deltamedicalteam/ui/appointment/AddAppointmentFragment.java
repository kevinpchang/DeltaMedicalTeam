package com.csc131.deltamedicalteam.ui.appointment;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.balysv.materialripple.MaterialRippleLayout;
import com.csc131.deltamedicalteam.R;
import com.csc131.deltamedicalteam.utils.PatientUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddAppointmentFragment extends Fragment {

    private Spinner mPurpose, patientSpinner;
    private String time, date, purpose;

    private TimePicker timePicker1;
    private MaterialRippleLayout mAddAppointmentBtn;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fstore;
    private ProgressBar progressBar;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_addappointment, container, false);


        // Prepare the data source (list of suggestions)
        String[] purposeItems  = {"Analysis", "X-Rays",  "Vaccination", "Doctor Visit"};

        // Create the purposeAdapter
        ArrayAdapter<String> purposeAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, purposeItems);
        purposeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mPurpose = rootView.findViewById(R.id.appointment_purpose_spinner);
        mPurpose.setAdapter(purposeAdapter);

        patientSpinner = rootView.findViewById(R.id.patient_list_spinner);
        getPatientList();

      /*  mAddAppointmentBtn = rootView.findViewById(R.id.your_button_id);

        patient = rootView.findViewById(R.id.add_patient_dob);
        time = rootView.findViewById(R.id.add_patient_dob);
        date = rootView.findViewById(R.id.add_patient_dob);
        purpose = rootView.findViewById(R.id.add_patient_dob); */


        progressBar = rootView.findViewById(R.id.progressBar);

        //Intialize Firebase
        fAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();


        mAddAppointmentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle adding appointment

                String purposeData = mPurpose.getSelectedItem().toString();
                String patientData = patientSpinner.getSelectedItem().toString();

                progressBar.setVisibility(View.VISIBLE);
                // Register the appointment in Firebase
                addAppointment(patientData, date, time, purposeData);

            }
        });

        return rootView;
    }





    // Initialize Firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    // Reference to the "Appointments" collection in Firebase
    private CollectionReference AppointmentsRef = db.collection("Appointment");

    // Method to fetch the list of patients
    private void getPatientList() {
        AppointmentsRef.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<String> patientNames = new ArrayList<>();
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        // Get patient name from DocumentSnapshot
                        String firstName = documentSnapshot.getString("fName");
                        String lastName = documentSnapshot.getString("lName");
                        String fullName = firstName + " " + lastName;

                        // Add patient name to the list
                        patientNames.add(fullName);
                    }

                    // Pass the list of patient names to populate the spinner
                    populateSpinner(patientNames);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching documents: " + e.getMessage());
                });
    }




    //    private void addPatient( String email, String emergencyName, String emergencyPhone){
    private void addAppointment( String patientData, String date, String time, String purposeData){
        CollectionReference documentReference = fstore.collection("appointment");
        Map<String, Object> appointmentData = new HashMap<>();
        //appointmentData.put(,);
       // appointmentData.put("emergencyPhone", emergencyPhone);
        documentReference.add(appointmentData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
             //           Toast.makeText(requireContext(), "Appointment successfully scheduled for " + , Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(getView()).navigate(R.id.patientManagerFragment);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(requireContext(), "Failed to create the appointment ", Toast.LENGTH_SHORT).show();
                    }
                });
    }



    // Method to populate the spinner with patient names
    private void populateSpinner(List<String> patientNames) {
        // Create an ArrayAdapter using the patientNames list and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, patientNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        patientSpinner.setAdapter(adapter);
    }





}


