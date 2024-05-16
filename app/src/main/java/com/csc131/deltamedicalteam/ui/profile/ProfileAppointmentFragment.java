package com.csc131.deltamedicalteam.ui.profile;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.csc131.deltamedicalteam.R;
import com.csc131.deltamedicalteam.model.Appointment;
import com.csc131.deltamedicalteam.model.Patient;
import com.csc131.deltamedicalteam.model.User;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileAppointmentFragment extends Fragment {
    String appTime, appDate, appPurpose, appPatientID, appDoctorID, appNote;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        com.csc131.deltamedicalteam.databinding.FragmentProfileAppointmentBinding binding = com.csc131.deltamedicalteam.databinding.FragmentProfileAppointmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Retrieve the passed patient information
        ProfileAppointmentFragmentArgs args = ProfileAppointmentFragmentArgs.fromBundle(getArguments());
        Appointment appointment = args.getAppointment();

        FirebaseFirestore fStore = FirebaseFirestore.getInstance();

        // Display the patient information
        TextView timeTextView = view.findViewById(R.id.appointment_time);
        TextView purposeTextView = view.findViewById(R.id.appointment_purpose);
        TextView patientIDTextView = view.findViewById(R.id.patient_id);
        TextView patientNameTextView = view.findViewById(R.id.patient_name);
        TextView doctorIDTextView = view.findViewById(R.id.doctor_id);
        TextView doctorNameTextView = view.findViewById(R.id.doctor_name);
        TextView noteTextView = view.findViewById(R.id.appointment_note);

        EditText noteEditText = view.findViewById(R.id.editTextTextMultiLine_note);

        Button saveBtn = view.findViewById(R.id.button_save);
        Button editBtn = view.findViewById(R.id.button_edit);


        DocumentReference AppointmentRef = fStore.collection("appointments").document(appointment.getDocumentId());
        AppointmentRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // Access individual fields
                appTime = documentSnapshot.getString("time");
                appDate = documentSnapshot.getString("date");
                String fullDate = appDate + " - " + appTime;
                appPurpose = documentSnapshot.getString("purpose");
                appPatientID = documentSnapshot.getString("patientDocumentId");

                appDoctorID = documentSnapshot.getString("userDocumentId");

                appNote = documentSnapshot.getString("note");
                // Now you can use the retrieved data as needed
                // For example, you can display it in your UI or perform further processing

                // Set user information in TextViews
                timeTextView.setText(fullDate);
                purposeTextView.setText(appPurpose);



//                patientNameTextView.setText(patientName);
//                doctorNameTextView.setText(DoctorName);
                User.getUserFromId(appDoctorID, user -> {
                    // Display user name
                    if (user != null) {
                        doctorNameTextView.setText(user.getName());
                        doctorIDTextView.setText(user.getMemberID());
                    }
                });

                Patient.getPatientFromId(appPatientID, patient -> {
                    // Display patient name
                    if (patient != null) {
                        patientNameTextView.setText(patient.getName());
                        patientIDTextView.setText(patient.getMemberID());
                    }
                });

                noteTextView.setText(appNote);


                noteEditText.setText(appNote);
            } else {
                // Document does not exist
                Log.d(TAG, "No such document");
            }
        }).addOnFailureListener(e -> {
            // Handle errors
            Log.e(TAG, "Error fetching document: " + e.getMessage());
        });

        editBtn.setOnClickListener(v -> {
            // Show EditText fields and Save button, hide TextViews and Edit button

            noteTextView.setVisibility(View.GONE);


            noteEditText.setVisibility(View.VISIBLE);

            editBtn.setVisibility(View.GONE);
            saveBtn.setVisibility(View.VISIBLE);

        });

        saveBtn.setOnClickListener(v -> {
            // Update Firestore with new values

            String newNote= noteEditText.getText().toString();

            AppointmentRef.update(
                            "note",newNote)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Note updated successfully", Toast.LENGTH_SHORT).show();
                        // Hide EditText fields and Save button, show TextViews and Edit button

                        noteTextView.setText(newNote);


                        noteTextView.setVisibility(View.VISIBLE);


                        noteEditText.setVisibility(View.GONE);

                        editBtn.setVisibility(View.VISIBLE);
                        saveBtn.setVisibility(View.GONE);

                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firestore Error", "Error updating document: " + e.getMessage());
                        Toast.makeText(getContext(), "Failed to update profile", Toast.LENGTH_SHORT).show();
                    });
        });


    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}