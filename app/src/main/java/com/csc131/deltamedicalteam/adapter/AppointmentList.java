package com.csc131.deltamedicalteam.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.csc131.deltamedicalteam.R;
import com.csc131.deltamedicalteam.model.Appointment;
import com.csc131.deltamedicalteam.model.Appointment;
import com.csc131.deltamedicalteam.utils.Tools;

import java.util.ArrayList;
import java.util.List;

public class AppointmentList extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Appointment> items = new ArrayList<>();

    private Context ctx;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, Appointment obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public AppointmentList(Context context, List<Appointment> items) {
        this.items = items;
        ctx = context;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public TextView patientId;
        public TextView date;
        public TextView time;
        public TextView purpose;
        public AdapterView<Adapter> OriginalViewHolder;

        public OriginalViewHolder(View v) {
            super(v);
       //     patientId = (TextView) v.findViewById(R.id`.TextViewPatientIdHolder);
       //    date = (TextView) v.findViewById(R.id.TextViewDateHolder);
       //     time = (TextView) v.findViewById(R.id.TextViewTimeHolder);
       //     purpose = (TextView) v.findViewById(R.id.TextViewPurposeHolder);
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_appointment_list, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof OriginalViewHolder) {
            OriginalViewHolder viewHolder = (OriginalViewHolder) holder;

            Appointment p = items.get(position);
           // viewHolder.TextViewPatientIdHolder.setText(p.patientId );

            viewHolder.OriginalViewHolder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int adapterPosition = holder.getAdapterPosition();
                    if (adapterPosition != RecyclerView.NO_POSITION && mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(view, items.get(adapterPosition), adapterPosition);
                    }
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

}