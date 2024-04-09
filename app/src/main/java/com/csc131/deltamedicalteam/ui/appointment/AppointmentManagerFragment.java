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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.csc131.deltamedicalteam.R;
import com.csc131.deltamedicalteam.adapter.AppointmentListAdapter;
import com.csc131.deltamedicalteam.model.Appointment;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AppointmentManagerFragment extends Fragment {
    private static final String TAG = "AppointmentManagerFragment";
    private RecyclerView recyclerView;
    private AppointmentListAdapter mAdapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_appointment_manager, container, false);

        recyclerView = view.findViewById(R.id.recycler_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        initComponent();

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

                // Add the Appointment to the list
                appointments.add(appointment);
            }

            // Set data and list adapter
            mAdapter = new AppointmentListAdapter(getActivity(), appointments);
            recyclerView.setAdapter(mAdapter);

            // On item list clicked
            mAdapter.setOnItemClickListener((appointment, position) -> {
                // Inside the click listener where you navigate to ProfileAppointmentFragment
                AppointmentManagerFragmentDirections.ActionAppointmentManagerFragmentToNavProfileAppointment action =
                        AppointmentManagerFragmentDirections.actionAppointmentManagerFragmentToNavProfileAppointment(appointment);
                Navigation.findNavController(requireView()).navigate(action);
            });
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Error fetching documents: " + e.getMessage());
        });
    }
}
