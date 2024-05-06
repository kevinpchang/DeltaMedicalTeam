package com.csc131.deltamedicalteam.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.csc131.deltamedicalteam.R;
import com.csc131.deltamedicalteam.model.Appointment;
import com.csc131.deltamedicalteam.model.Patient;
import com.csc131.deltamedicalteam.model.User;

import java.util.ArrayList;
import java.util.List;

public class AppointmentListAdapter extends RecyclerView.Adapter<AppointmentListAdapter.ViewHolder> {

    private List<Appointment> appointments;
    private final List<Appointment> originalAppointments = new ArrayList<>();
    private OnItemClickListener listener;
    private String currentPatientFilter = ""; // Default filter value for patient name
    private String currentUserFilter = ""; // Default filter value for user name
    private String currentPurposeFilter = ""; // Default filter value for purpose

    private List<Appointment> filteredAppointments; // To hold filtered appointments

    public AppointmentListAdapter(Context context, List<Appointment> appointments) {
        this.appointments = appointments;
        this.originalAppointments.addAll(appointments); // Save the original list
        this.filteredAppointments = new ArrayList<>(appointments); // Initialize filteredAppointments with all appointments
    }



    public interface OnItemClickListener {
        void onItemClick(Appointment appointment, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView patientIdTextView;
        TextView doctorIdTextView;
        TextView purposeTextView;
        TextView timeTextView;
        TextView dateTextView;


        public ViewHolder(View itemView) {
            super(itemView);
            patientIdTextView = itemView.findViewById(R.id.appointment_patient_id);
            doctorIdTextView = itemView.findViewById(R.id.appointment_user_id);
            purposeTextView = itemView.findViewById(R.id.appointment_purpose);
            timeTextView = itemView.findViewById(R.id.appointment_time);
            dateTextView = itemView.findViewById(R.id.appointment_date);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_appointment_list, parent, false);
        return new ViewHolder(view);
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }

    // Method to filter appointments based on patient name
    // Method to filter appointments based on patient name
    public void filterByPatientID(String ID) {
        currentPatientFilter = ID.trim(); // Update current filter value
        applyFilters();
    }

    // Method to filter appointments based on user name
    public void filterByUserID(String ID) {
        currentUserFilter = ID.trim(); // Update current filter value
        applyFilters();
    }

    // Method to filter appointments based on purpose
    public void filterByPurpose(String purpose) {
        currentPurposeFilter = purpose.trim(); // Update current filter value
        applyFilters();
    }

    public void resetFilterByPatientID() {
        currentPatientFilter = ""; // Reset the filter value
        applyFilters(); // Apply filters again
    }

    public void resetFilterByUserID() {
        currentUserFilter = ""; // Reset the filter value
        applyFilters(); // Apply filters again
    }

    public void resetFilterByPurpose() {
        currentPurposeFilter = ""; // Reset the filter value
        applyFilters(); // Apply filters again
    }

    // Method to set the appointments and notify the adapter
    // Method to set the appointments and notify the adapter
    public void updateAppointments(List<Appointment> updatedAppointments) {
        appointments.clear();
        appointments.addAll(updatedAppointments);
        applyFilters(); // Update filtered appointments based on new appointments
        notifyDataSetChanged();
    }


    private void applyFilters() {
        filteredAppointments.clear(); // Clear previous filtered results
        for (Appointment appointment : appointments) {
            boolean matchesPatient = currentPatientFilter.isEmpty() ||
                    appointment.getPatientDocumentId().equalsIgnoreCase(currentPatientFilter);
            boolean matchesUser = currentUserFilter.isEmpty() ||
                    appointment.getUserDocumentId().equalsIgnoreCase(currentUserFilter);
            boolean matchesPurpose = currentPurposeFilter.isEmpty() ||
                    appointment.getPurpose().equalsIgnoreCase(currentPurposeFilter);
            // Check if the appointment matches all filters
            if (matchesPatient && matchesUser && matchesPurpose) {
                filteredAppointments.add(appointment); // Add appointments that match all filters
            }
        }


        notifyDataSetChanged(); // Notify the adapter that data set has changed
    }

    public void setDefaultList() {
        appointments.clear(); // Clear any filtered appointments
        appointments.addAll(originalAppointments); // Restore the original list
        notifyDataSetChanged(); // Notify the adapter that the data has changed
    }



    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Appointment appointment = filteredAppointments.get(position);
        // Fetch user ID and patient ID
        String userID = appointment.getUserDocumentId();
        String patientID = appointment.getPatientDocumentId();

        // Fetch user name and patient name asynchronously
        User.getUserFromId(userID, user -> {
            // Display user name
            if (user != null) holder.doctorIdTextView.setText(user.getName());
            else {
                holder.doctorIdTextView.setText("No User");
            }
        });

        Patient.getPatientFromId(patientID, patient -> {
            // Display patient name
            if (patient != null) {
                holder.patientIdTextView.setText(patient.getName());
            } else {
                holder.patientIdTextView.setText("No Patient");
            }
        });

        // Set other appointment details
        if (appointment.getPurpose() != null) {
            holder.purposeTextView.setText(appointment.getPurpose());
        } else {
            holder.purposeTextView.setText("Unknown Purpose");
        }

        holder.timeTextView.setText(appointment.getTime());
        holder.dateTextView.setText(appointment.getDate());

        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(appointment, position);
            }
        });
    }






    @Override
    public int getItemCount() {
        return filteredAppointments.size(); // Return the size of the filtered list
    }
}
