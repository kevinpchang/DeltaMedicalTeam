package com.csc131.deltamedicalteam.ui.appointment;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

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

        String[][] purposeItemsWithDuration = {
                //{"Name", "Minute"},
                {"Analysis", "60"},
                {"X-Rays", "30"},
                {"Vaccination", "30"},
                {"Doctor Visit", "120"}
        };


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
                // Create a MaterialDatePicker builder
                MaterialDatePicker.Builder<Long> materialDateBuilder = MaterialDatePicker.Builder.datePicker();
                // Set the title for the date picker
                materialDateBuilder.setTitleText("Select date");
                // Build the MaterialDatePicker
                MaterialDatePicker<Long> materialDatePicker = materialDateBuilder.build();
                // Show the MaterialDatePicker
                materialDatePicker.show(getParentFragmentManager(), "DATE_PICKER");

                // Add a listener for the positive button click (selection)
                materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                    @Override
                    public void onPositiveButtonClick(Long selection) {
                        // Convert the selected date to Calendar object
                        Calendar selectedCalendar = Calendar.getInstance();
                        selectedCalendar.setTimeInMillis(selection);

                        // Add one day to the selected date
                        selectedCalendar.add(Calendar.DAY_OF_MONTH, 1);

                        // Format the selected date
                        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
                        String selectedDate = dateFormat.format(selectedCalendar.getTime());

                        // Set the text of the datePickButton to the selected date
                        datePickButton.setText("DATE PICK: " + selectedDate);
                    }
                });
            }
        });


        Button timePickButton = rootView.findViewById(R.id.time_pick);
        timePickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the selected purpose from the spinner
                String selectedPurpose = mPurpose.getSelectedItem().toString();

                // Find the corresponding duration for the selected purpose
                String duration = "";
                for (String[] item : purposeItemsWithDuration) {
                    if (item[0].equals(selectedPurpose)) {
                        duration = item[1];
                        break;
                    }
                }

                // If duration is found, generate available times and display them in a dialog
                if (!duration.isEmpty()) {
                    int durationInMinutes = Integer.parseInt(duration);

                    // Calculate the start and end times for the day
                    Calendar startTime = Calendar.getInstance();
                    startTime.set(Calendar.HOUR_OF_DAY, 8); // Assuming start time is 8:00 AM
                    startTime.set(Calendar.MINUTE, 0);
                    startTime.set(Calendar.SECOND, 0);
                    Calendar endTime = Calendar.getInstance();
                    endTime.set(Calendar.HOUR_OF_DAY, 17); // Assuming end time is 5:00 PM
                    endTime.set(Calendar.MINUTE, 0);
                    endTime.set(Calendar.SECOND, 0);

                    // Generate available times based on duration
                    List<String> availableTimes = new ArrayList<>();
                    while (startTime.before(endTime)) {
                        availableTimes.add(String.format(Locale.getDefault(), "%02d:%02d", startTime.get(Calendar.HOUR_OF_DAY), startTime.get(Calendar.MINUTE)));
                        startTime.add(Calendar.MINUTE, durationInMinutes);
                    }

                    // Display available times in a dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                    builder.setTitle("Select Time");
                    builder.setItems(availableTimes.toArray(new String[0]), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Handle the selected time
                            String selectedTime = availableTimes.get(which);
                            timePickButton.setText("TIME PICK: " + selectedTime);
                        }
                    });
                    builder.show();
                } else {
                    // Show a message indicating that duration for the selected purpose is not available
                    Toast.makeText(requireContext(), "Duration for the selected purpose is not available", Toast.LENGTH_SHORT).show();
                }
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
        CollectionReference documentReference = db.collection("appointments");
        Map<String, Object> appointmentData = new HashMap<>();
        appointmentData.put("date", selectedDate);
        appointmentData.put("time", selectedTime);
        appointmentData.put("userDocumentId", userDocumentId);
        appointmentData.put("patientDocumentId",((Patient) patientSpinner.getSelectedItem()).getDocumentId());
        appointmentData.put("purpose",mPurpose.getSelectedItem().toString());
        appointmentData.put("note", "");
        documentReference.add(appointmentData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(requireContext(), "Appointment Profile created", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(requireView()).navigate(R.id.nav_appointment);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(requireContext(), "Failed to create Patient Profile ", Toast.LENGTH_SHORT).show();
            }
        });
    }

}


