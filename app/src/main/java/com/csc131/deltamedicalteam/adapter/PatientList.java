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

    public List<Patient> getPatients() {
        return items;
    }
    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public TextView id;
        public TextView name;
        public TextView sex;
        public TextView phone;
        public TextView address;
        public View lyt_parent;

        public OriginalViewHolder(View v) {
            super(v);
            id = (TextView) v.findViewById(R.id.list_item_id);
            name = (TextView) v.findViewById(R.id.list_item_name);
            sex = (TextView) v.findViewById(R.id.list_item_sex);
            phone = (TextView) v.findViewById(R.id.list_item_phone);
            address = (TextView) v.findViewById(R.id.list_item_address);
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
            String idSetText = items.get(position).getDocumentId();
            String idSubString = idSetText.substring(Math.max(idSetText.length()-10,0));

            Patient patient = items.get(position);
            viewHolder.id.setText(idSubString);
            viewHolder.name.setText(patient.toString());
            viewHolder.sex.setText(patient.getSex());
            viewHolder.phone.setText(patient.getCellPhone());
            viewHolder.address.setText(patient.getAddress()); // Assuming you have added an address TextView

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