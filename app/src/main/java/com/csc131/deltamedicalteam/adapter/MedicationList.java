package com.csc131.deltamedicalteam.adapter;

//public class MedicationList {
//}

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.csc131.deltamedicalteam.R;
import com.csc131.deltamedicalteam.model.Medication;

import java.util.List;

public class MedicationList extends RecyclerView.Adapter<MedicationList.MedicationViewHolder> {
    private List<Medication> medicationList;

    public MedicationList(FragmentActivity activity, List<Medication> medicationList) {
        this.medicationList = medicationList;
    }

    @NonNull
    @Override
    public MedicationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_medication, parent, false);
        return new MedicationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicationViewHolder holder, int position) {
        Medication medication = medicationList.get(position);
        holder.bind(medication);
       // holder.nameTextView.setText(medication);
    }

    @Override
    public int getItemCount() {
        return medicationList.size();
    }


    public void setOnItemClickListener(UserList.OnItemClickListener onItemClickListener) {
    }

    public static class MedicationViewHolder extends RecyclerView.ViewHolder {
        TextView medicationNameTextView;
        TextView medicationInfoTextView;

        public MedicationViewHolder(@NonNull View itemView) {
            super(itemView);
            //medicationNameTextView = itemView.findViewById(R.id.medNameTextView);
            //medicationInfoTextView = itemView.findViewById(R.id.medInfoTextView);

        }

        public void bind(Medication medication) {
            //medicationNameTextView.setText(medication.getMedicationName());
            //medicationInfoTextView.setText(medication.getMedicationInfo());
            // Set other TextViews with corresponding medication fields if necessary
        }
    }
}