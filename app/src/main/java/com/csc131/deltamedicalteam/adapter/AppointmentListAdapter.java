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
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

public class AppointmentListAdapter extends RecyclerView.Adapter<AppointmentListAdapter.ViewHolder> {

    private List<Appointment> appointments;
    private Context context;
    private OnItemClickListener listener;

    public AppointmentListAdapter(Context context, List<Appointment> appointments) {
        this.context = context;
        this.appointments = appointments;
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

        public ViewHolder(View itemView) {
            super(itemView);
            patientIdTextView = itemView.findViewById(R.id.appointment_patient_id);
            doctorIdTextView = itemView.findViewById(R.id.appointment_user_id);
            purposeTextView = itemView.findViewById(R.id.appointment_purpose);
            timeTextView = itemView.findViewById(R.id.appointment_time);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_appointment_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Appointment appointment = appointments.get(position);

        // Fetch user ID and patient ID
        String userID = appointment.getUserDocumentId();
        String patientID = appointment.getPatientDocumentId();

        // Fetch user name and patient name asynchronously
        User.getUserFromId(userID, user -> {
            // Display user name
            if (user != null) {
                holder.doctorIdTextView.setText(user.getfName());
            } else {
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
        holder.purposeTextView.setText(appointment.getPurpose());
        holder.timeTextView.setText(appointment.getTime());

        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(appointment, position);
            }
        });
    }





    @Override
    public int getItemCount() {
        return appointments.size();
    }
}
