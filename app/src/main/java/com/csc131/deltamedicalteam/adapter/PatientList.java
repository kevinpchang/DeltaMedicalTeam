package com.csc131.deltamedicalteam.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.csc131.deltamedicalteam.R;
import com.csc131.deltamedicalteam.model.Patient;
import com.csc131.deltamedicalteam.model.Phone;


import com.csc131.deltamedicalteam.utils.Tools;

import java.util.ArrayList;
import java.util.List;

public class PatientList extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Patient> items = new ArrayList<>();

    private Context ctx;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, Patient obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public PatientList(Context context, List<Patient> items) {
        this.items = items;
        ctx = context;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView name;
        public View lyt_parent;
        public TextView email;
        public TextView phone;
        public TextView address;

        public OriginalViewHolder(View v) {
            super(v);
            image = (ImageView) v.findViewById(R.id.image);
            name = (TextView) v.findViewById(R.id.appointment_list_item_name);
            phone = (TextView) v.findViewById(R.id.appointment_list_item_phone);
            address = (TextView) v.findViewById(R.id.appointment_list_item_address);
            lyt_parent = (View) v.findViewById(R.id.lyt_parent);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_patient_list, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof OriginalViewHolder) {
            OriginalViewHolder viewHolder = (OriginalViewHolder) holder;

            Patient patient = items.get(position);
            viewHolder.name.setText(patient.getfName() + " " + patient.getlName());
            viewHolder.phone.setText(String.valueOf(patient.getPhone().getMobile()));

            viewHolder.address.setText(patient.getAddress()); // Assuming you have added an address TextView

            // Check if image is available
            if (patient.getImage() != 0) {
                Tools.displayImageRound(ctx, viewHolder.image, patient.getImage());
            } else {
                // Load default image
                viewHolder.image.setImageResource(R.drawable.no_avatar_patient);
            }

            viewHolder.lyt_parent.setOnClickListener(view -> {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION && mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(view, items.get(adapterPosition), adapterPosition);
                }
            });
        }
    }



    @Override
    public int getItemCount() {
        return items.size();
    }

}