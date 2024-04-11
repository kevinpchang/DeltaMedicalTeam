package com.csc131.deltamedicalteam.ui.appointment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.csc131.deltamedicalteam.R;
import com.csc131.deltamedicalteam.adapter.AppointmentList;
import com.csc131.deltamedicalteam.model.Appointment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AppointmentManagerFragment extends Fragment {
    private static final String TAG = "AppointmentManagerFragment";
    private RecyclerView recyclerView;
    private AppointmentList mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_appointment_manager, container, false);

        recyclerView = view.findViewById(R.id.appointment_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        initComponent();

        // Find the Button and set its click listener
        Button btnAddAppointment = view.findViewById(R.id.add_appointment_redirect_button);
        btnAddAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the destination fragment
             //   Navigation.findNavController(v).navigate(R.id.add_appointment_redirect_button);
            }
        });


        return view;
    }


    private void initComponent() {
        // Initialize Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Reference to the "appointments" collection
        CollectionReference appointmentsRef = db.collection("appointments");

        // Query to get all documents from the "appointments" collection
        appointmentsRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    List<Appointment> items = new ArrayList<>();
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        // Retrieve appointment information from Firestore document
                        String firstname = documentSnapshot.getString("fName");
                        String lastname = documentSnapshot.getString("lName");
                        String name = firstname + " " + lastname;
                        String address = documentSnapshot.getString("address");
                        String phoneNumber = "There no phone number";
                        String patient=null, date=null, time=null, purpose=null; //REMOVE THIS

                        // Retrieve the phone field as an Object
                        Object phoneObj = documentSnapshot.getData().get("phone");

                        // Check if the phoneObj is not null and is of type Map
                        if (phoneObj instanceof Map) {
                            // Cast the phoneObj to Map<String, Object>
                            Map<String, Object> phoneMap = (Map<String, Object>) phoneObj;

                            // Check if the phoneMap contains the "mobile" key
                            if (phoneMap.containsKey("mobile")) {
                                // Retrieve the value associated with the "mobile" key
                                Object mobileObj = phoneMap.get("mobile");

                                // Check if the value is not null and is of type Long
                                if (mobileObj instanceof Long) {
                                    // Convert the Long value to a String
                                    phoneNumber = String.valueOf((Long) mobileObj);

                                    // Now you have the mobile phone number as a String
                                    System.out.println("Mobile com.csc131.deltamedicalteam.model.Patient.Phone Number: " + phoneNumber);
                                } else {
                                    // Handle the case where the value is not a Long
                                    System.out.println("Mobile phone number is not of type Long.");
                                }
                            } else {
                                // Handle the case where the "phone" map doesn't contain the "mobile" key
                                System.out.println("Mobile phone number not found.");
                            }
                        } else {
                            // Handle the case where the "phone" field is not a map
                            System.out.println("com.csc131.deltamedicalteam.model.Patient.Phone field is not of type Map<String, Object>.");
                        }



                        // Create People object with appointment information
                        Appointment appointment = new Appointment(patient, date, time, purpose);
                        items.add(appointment);
                    }

                    // Set data and list adapter
                    mAdapter = new AppointmentList(getActivity(), items);
                    recyclerView.setAdapter(mAdapter);

                    // On item list clicked
                    mAdapter.setOnItemClickListener(new AppointmentList.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, Appointment obj, int position) {
                            Snackbar.make(view, "Item " + obj.patientId + " clicked", Snackbar.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Log.d(TAG, "No documents found.");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Error fetching documents: " + e.getMessage());
            }
        });
    }


}
