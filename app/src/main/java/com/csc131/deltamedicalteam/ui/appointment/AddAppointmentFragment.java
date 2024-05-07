package com.csc131.deltamedicalteam.ui.appointment;

import static android.content.ContentValues.TAG;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.balysv.materialripple.MaterialRippleLayout;
import com.csc131.deltamedicalteam.R;
import com.csc131.deltamedicalteam.model.Appointment;
import com.csc131.deltamedicalteam.model.Patient;
import com.google.android.material.datepicker.MaterialDatePicker;


import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;
import java.util.UUID;

public class AddAppointmentFragment extends Fragment {

    private Spinner mPurpose, patientSpinner;

    private ProgressBar progressBar;

    List<Patient> patientNames = new ArrayList<>();

    

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_addappointment, container, false);


        // Prepare the data source (list of suggestions)
        String[] purposeItems  = {"Analysis", "X-Rays",  "Vaccination", "Doctor Visit"};


        // Create the adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, purposeItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mPurpose = rootView.findViewById(R.id.appointment_purpose_spinner);
        mPurpose.setAdapter(adapter);


        patientSpinner = rootView.findViewById(R.id.patient_list_spinner);
        getPatientList();



        progressBar = rootView.findViewById(R.id.progressBar);

        Button datePickButton = rootView.findViewById(R.id.date_pick);

        datePickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDatePicker.Builder<Long> materialDateBuilder = MaterialDatePicker.Builder.datePicker();
                materialDateBuilder.setTitleText("Select date");
                MaterialDatePicker<Long> materialDatePicker = materialDateBuilder.build();
                materialDatePicker.show(getParentFragmentManager(), "DATE_PICKER");
                materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                    @Override
                    public void onPositiveButtonClick(Long selection) {
                        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("PST"));
                        calendar.setTimeInMillis(selection);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
                        String selectedDate = dateFormat.format(calendar.getTime());
                        datePickButton.setText("DATE PICK: " + selectedDate);
                    }
                });
            }
        });

        Button timePickButton = rootView.findViewById(R.id.time_pick);
        timePickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a TimePickerDialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(), android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                // Round the selected minute to the nearest 15 minutes
                                minute = roundToNearest15Minutes(minute);

                                // Handle the selected time
                                String selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                                timePickButton.setText("TIME PICK: " + selectedTime);
                            }
                        }, Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE), true);
                // Show the TimePickerDialog
                timePickerDialog.show();
            }
        });

        MaterialRippleLayout createAppointmentButton = rootView.findViewById(R.id.bt_create_appointment);

        createAppointmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the selected date and time from the buttons
                String selectedDate = ((Button) rootView.findViewById(R.id.date_pick)).getText().toString().replace("DATE PICK: ", "");
                String selectedTime = ((Button) rootView.findViewById(R.id.time_pick)).getText().toString().replace("TIME PICK: ", "");

                // Check if date and time are selected
                if (selectedDate.equals("SELECT DATE") || selectedTime.equals("SELECT TIME")) {
                    // Show a message to select both date and time
                    // You can implement this part based on your UI/UX requirement
                    return;
                }

                // Check availability in Firestore database
                checkAvailability(selectedDate, selectedTime);
            }
        });


        return rootView;
    }

    private int roundToNearest15Minutes(int minute) {
        int remainder = minute % 15;
        if (remainder < 8) {
            return minute - remainder;
        } else {
            return minute + (15 - remainder);
        }
    }

    // Initialize Firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Reference to the "patients" collection
    private CollectionReference patientsRef = db.collection("patients");

    // Method to fetch the list of patients
    private void getPatientList() {
        patientsRef.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        // Get patient name from DocumentSnapshot
                        // Get patient id from DocumentSnapshot
                        Patient patient = documentSnapshot.toObject(Patient.class);

                        assert patient != null;
                        patient.fromDocumentSnapshot(documentSnapshot);
                        // Add patient name to the list
                        patientNames.add(patient);
                    }

                    // Pass the list of patient names to populate the spinner
                    populateSpinner(patientNames);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching documents: " + e.getMessage());
                });
    }

    // Method to populate the spinner with patient names
    private void populateSpinner(List<Patient> patientNames) {
        // Create an ArrayAdapter using the patientNames list and a default spinner layout
        ArrayAdapter<Patient> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, patientNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        patientSpinner.setAdapter(adapter);
    }

    private void checkAvailability(String selectedDate, String selectedTime) {
        // Get the user's document ID
        String userDocumentId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();// Replace this with the actual user's document ID

        // Get the current time
        Calendar currentTime = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String currentTimeString = dateFormat.format(currentTime.getTime());

        // Compare selected time with current time
        try {
            Date selectedDateTime = dateFormat.parse(selectedTime);
            Date currentDateTime = dateFormat.parse(currentTimeString);

            if (selectedDateTime != null && selectedDateTime.after(currentDateTime)) {
                // Selected time is in the future, proceed to check availability
                // Assuming you have a collection named "appointments" in your Firestore database
                db.collection("appointments")
                        .whereEqualTo("date", selectedDate)
                        .whereEqualTo("time", selectedTime)
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            if (queryDocumentSnapshots.isEmpty()) {
                                // No existing appointment found for the selected date and time
                                // Proceed to add the appointment to Firestore
                                addAppointment(selectedDate, selectedTime, userDocumentId);
                            } else {
                                // Appointment already exists for the selected date and time
                                // Show a message to the user indicating the unavailability
                                // You can implement this part based on your UI/UX requirement
                                Log.d(TAG, "Appointment already exists for the selected date and time");
                                Toast.makeText(requireContext(), "Appointment already exists for the selected date and time", Toast.LENGTH_SHORT).show();

                            }
                        })
                        .addOnFailureListener(e -> {
                            // Failed to fetch appointments from Firestore
                            Log.e(TAG, "Error checking availability: " + e.getMessage());
                        });
            } else {
                // Selected time is not in the future, show an error message to the user
                // You can implement this part based on your UI/UX requirement
                Log.d(TAG, "Selected time is not in the future");
                Toast.makeText(requireContext(), "Selected time is not in the future", Toast.LENGTH_SHORT).show();
            }
        } catch (ParseException e) {
            // Handle parsing exception
            Log.e(TAG, "Error parsing time: " + e.getMessage());

        }
    }



    private void addAppointment(String selectedDate, String selectedTime, String userDocumentId) {
        // Assuming you have a collection named "appointments" in your Firestore database
        // You can replace "yourField" with appropriate field names in your Firestore document

        Appointment appointment = new Appointment(((Patient) patientSpinner.getSelectedItem()).getDocumentId(), userDocumentId, mPurpose.getSelectedItem().toString(), selectedTime, selectedDate);

        db.collection("appointments")
                .add(appointment)
                .addOnSuccessListener(documentReference -> {
                    // Appointment added successfully
                    // You can show a success message to the user
                    Log.d(TAG, "Appointment added successfully");
                    // Go back to the previous fragment
                    requireActivity().getSupportFragmentManager().popBackStack();
                })
                .addOnFailureListener(e -> {
                    // Failed to add appointment
                    // You can show an error message to the user
                    Log.e(TAG, "Error adding appointment: " + e.getMessage());
                    // Show a toast message for error logging
                    Toast.makeText(requireContext(), "Failed to add appointment. Please try again later.", Toast.LENGTH_SHORT).show();
                });
    }

}


