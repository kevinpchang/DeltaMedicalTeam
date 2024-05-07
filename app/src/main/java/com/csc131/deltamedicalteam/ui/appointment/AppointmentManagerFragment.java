package com.csc131.deltamedicalteam.ui.appointment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.csc131.deltamedicalteam.R;
import com.csc131.deltamedicalteam.adapter.AppointmentListAdapter;
import com.csc131.deltamedicalteam.helper.SwipeItemTouchHelper;
import com.csc131.deltamedicalteam.model.Appointment;
import com.csc131.deltamedicalteam.model.Patient;
import com.csc131.deltamedicalteam.model.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AppointmentManagerFragment extends Fragment {
    private static final String TAG = "AppointmentManagerFragment";
    private RecyclerView recyclerView;

    private Spinner patientSpinner, userSpinner, purposeSpinner;
    private AppointmentListAdapter mAdapter;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final CollectionReference patientsRef = db.collection("patients");
    private final CollectionReference userRef = db.collection("users");
    private Patient mCurrentPatient;

    List<Patient> patientNames = new ArrayList<>();
    List<User> userNames = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_appointment_manager, container, false);

        // Find the filter button
        ImageButton filterButton = view.findViewById(R.id.appointment_filter_btn);
        // Find the CardView for filter options
        CardView filterOptionsCardView = view.findViewById(R.id.appointment_filter_option);

        // Assuming you've initialized the switches and spinners
        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch filterByPatientSwitch = view.findViewById(R.id.filter_by_patient_switch);
        patientSpinner = view.findViewById(R.id.spinner_patient);
        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch filterByUserSwitch = view.findViewById(R.id.filter_by_user_switch);
        userSpinner = view.findViewById(R.id.spinner_user);
        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch filterByPurposeSwitch = view.findViewById(R.id.filter_by_purpose_switch);
        purposeSpinner = view.findViewById(R.id.spinner_appointment_purpose);

// Set OnCheckedChangeListener for the Patient filter switch
        filterByPatientSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                patientSpinner.setVisibility(View.VISIBLE);
                getPatientList(); // Fetch the updated list when switch is checked
            } else {
                patientSpinner.setVisibility(View.GONE);
                mAdapter.resetFilterByPatientID(); // Reset the filter
                patientNames.clear();
            }
        });

// Set OnCheckedChangeListener for the User filter switch
        filterByUserSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                userSpinner.setVisibility(View.VISIBLE);
                getUserList(); // Fetch the updated list when switch is checked
            } else {
                userSpinner.setVisibility(View.GONE);
                mAdapter.resetFilterByUserID(); // Reset the filter
                userNames.clear();
            }
        });

// Set OnCheckedChangeListener for the Purpose filter switch
        filterByPurposeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                purposeSpinner.setVisibility(View.VISIBLE);
                // Additional logic to handle when Purpose switch is checked
            } else {
                purposeSpinner.setVisibility(View.GONE);
                mAdapter.resetFilterByPurpose(); // Reset the filter
            }
        });




        // Set OnClickListener for filter button
        filterButton.setOnClickListener(v -> {
            // Toggle visibility of filter options CardView
            if (filterOptionsCardView.getVisibility() == View.VISIBLE) {
                filterOptionsCardView.setVisibility(View.GONE);
            } else {
                filterOptionsCardView.setVisibility(View.VISIBLE);
            }
        });

        recyclerView = view.findViewById(R.id.recycler_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        initComponent();


        // Prepare the data source (list of suggestions)
        String[] purposeItems  = {"", "Analysis", "X-Rays",  "Vaccination", "Doctor Visit"};


        // Create the adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, purposeItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        purposeSpinner.setAdapter(adapter);

// Additionally, you can set up click listeners for the filter options themselves to perform filtering actions










// Assuming you have a reference to your AppointmentListAdapter called mAdapter
        patientSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mCurrentPatient = (Patient) parent.getItemAtPosition(position);
                String ID = mCurrentPatient.getDocumentId();

                    // Get selected patient name from spinner
                    mAdapter.filterByPatientID(ID);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        userSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> user, View view, int position, long id) {
                User muser = (User) user.getItemAtPosition(position);
                String ID = muser.getDocumentId();

                // Get selected patient name from spinner
                mAdapter.filterByUserID(ID);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        purposeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String purpose = (String) parent.getItemAtPosition(position);
                mAdapter.filterByPurpose(purpose);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });






        // Find the Button and set its click listener
        Button btnAddAppointment = view.findViewById(R.id.add_button);
        btnAddAppointment.setOnClickListener(v -> {
            // Navigate to the destination fragment
            Navigation.findNavController(v).navigate(R.id.action_appointmentManagerFragment_to_nav_add_appointment);
        });

        return view;
    }


    private void initComponent() {
        // Reference to the "appointments" collection
        CollectionReference appointmentsRef = db.collection("appointments");

// Query to get all documents from the "appointments" collection
        appointmentsRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<Appointment> appointments = new ArrayList<>();
            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                // Convert DocumentSnapshot to Appointment object
                Appointment appointment = documentSnapshot.toObject(Appointment.class);

                // Call fromDocumentSnapshot() method to populate additional fields
                assert appointment != null;
                appointment.fromDocumentSnapshot(documentSnapshot);

                // Add the Appointment to the list
                appointments.add(appointment);
            }

            // Sort appointments based on date and time
            appointments.sort((a1, a2) -> {
                // First compare by date
                int dateComparison = a1.getDate().compareToIgnoreCase(a2.getDate());
                if (dateComparison != 0) {
                    return dateComparison;
                }
                // If dates are equal, compare by time
                return a1.getTime().compareToIgnoreCase(a2.getTime());
            });

            // Now you have a list of populated Appointment objects sorted by date and time
            // You can use this list as needed





        // Set data and list adapter
            mAdapter = new AppointmentListAdapter(getActivity(), appointments);
            recyclerView.setAdapter(mAdapter);

            SwipeItemTouchHelper swipeItemTouchHelper = new SwipeItemTouchHelper(mAdapter);
            // Create an instance of ItemTouchHelper and attach SwipeItemTouchHelper to it
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeItemTouchHelper);
            itemTouchHelper.attachToRecyclerView(recyclerView);
            swipeItemTouchHelper.setSwipeListener(new SwipeItemTouchHelper.SwipeListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onItemDismiss(int position) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Confirm Deletion");
                    builder.setMessage("Are you sure you want to remove this appointment?");
                    builder.setPositiveButton("Yes", (dialog, which) -> {

                        // Remove the item from the list
                        Appointment removedAppointment = mAdapter.getAppointments().get(position); // Get the appointment at the specified position
                        Log.d(TAG, "ID got: " + removedAppointment.getAppointmentId());
                        mAdapter.getAppointments().remove(position); // Remove the appointment from the list
                        mAdapter.notifyItemRemoved(position); // Notify the adapter about the removal


                        // Additional logic to handle the removal from the database (already implemented in your code)
                        FirebaseFirestore db = FirebaseFirestore.getInstance();

                        DocumentReference appointmentRef = db.collection("appointments").document(removedAppointment.getAppointmentId());
                        appointmentRef.delete()
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(getContext(), "Appointment Removed: " + removedAppointment.getAppointmentId(), Toast.LENGTH_SHORT).show();
                                    dialog.dismiss(); // Dismiss the dialog after successful deletion

                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "Failed to remove Appointment: " + e.getMessage());
                                    // If removal from database fails, add the item back to the list and notify the adapter
                                    mAdapter.getAppointments().add(position, removedAppointment);
                                    mAdapter.notifyItemInserted(position);
                                    Toast.makeText(getContext(), "Failed to remove appointment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                        refreshAppointments();
                    });

                    builder.setNegativeButton("Cancel", (dialog, which) -> {
                        dialog.dismiss();
                        mAdapter.notifyDataSetChanged(); // Refresh the list after canceling
                    });
                    builder.setOnDismissListener(dialog -> mAdapter.notifyDataSetChanged());
                    builder.show();

                }






            });

            // On item list clicked
            mAdapter.setOnItemClickListener((appointment, position) -> {
                // Inside the click listener where you navigate to ProfileAppointmentFragment
                Toast.makeText(requireContext(), "Item CLICKED", Toast.LENGTH_SHORT).show();
            });
        }).addOnFailureListener(e -> Log.e(TAG, "Error fetching documents: " + e.getMessage()));
    }

    private void refreshAppointments() {
        // Retrieve the updated list of appointments from the database
        FirebaseFirestore.getInstance().collection("appointments")
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Appointment> updatedAppointments = new ArrayList<>();
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        // Convert DocumentSnapshot to Appointment object
                        Appointment appointment = documentSnapshot.toObject(Appointment.class);
                        // Call fromDocumentSnapshot() method to populate additional fields
                        appointment.fromDocumentSnapshot(documentSnapshot);
                        // Add the appointment to the list
                        updatedAppointments.add(appointment);
                    }
                    // Notify the adapter with the updated list of appointments
                    if (mAdapter != null) {
                        mAdapter.updateAppointments(updatedAppointments);
                    } else {
                        Log.e(TAG, "Adapter is null");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error getting appointments: " + e.getMessage());
                });
    }

    // Method to fetch the list of patients, spinner is populated with patient objects
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
                .addOnFailureListener(e -> Log.e(TAG, "Error fetching documents: " + e.getMessage()));
    }

    private void getUserList() {
        userRef.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        // Get user name from DocumentSnapshot
                        // Get user id from DocumentSnapshot
                        User user = documentSnapshot.toObject(User.class);
                        String documentId = documentSnapshot.getId();
                        user.setDocumentId(documentId);

                        // Add user to the list
                        userNames.add(user);
                    }

                    // Pass the list of users to populate the spinner
                    populateUserSpinner(userNames);
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error fetching documents: " + e.getMessage()));
    }

    // Method to populate the spinner with patient names
    private void populateSpinner(List<Patient> patientNames) {
        // Create an ArrayAdapter using the patientNames list and a default spinner layout
        ArrayAdapter<Patient> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, patientNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        patientSpinner.setAdapter(adapter);
    }

    private void populateUserSpinner(List<User> userNames) {
        // Create an ArrayAdapter using the userNames list and a default spinner layout
        ArrayAdapter<User> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, userNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        userSpinner.setAdapter(adapter);
    }





}
