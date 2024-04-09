package com.csc131.deltamedicalteam.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.csc131.deltamedicalteam.R;
import com.csc131.deltamedicalteam.model.HealthConditions;

import java.util.ArrayList;
import java.util.List;

public class MedicalHistoryList extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<HealthConditions> items = new ArrayList<>();

    private Context ctx;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, HealthConditions obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public MedicalHistoryList(Context context, List<HealthConditions> items) {
        this.items = items;
        ctx = context;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public View lyt_parent;

        public OriginalViewHolder(View v) {
            super(v);
            name = (TextView) v.findViewById(R.id.list_item_name);
            lyt_parent = (View) v.findViewById(R.id.lyt_parent);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_health_history_list, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof OriginalViewHolder) {
            OriginalViewHolder viewHolder = (OriginalViewHolder) holder;

            HealthConditions p = items.get(position);
            //used to check if array is empty or not
            if (p.getPreviousIllnesses() != null) {
                viewHolder.name.setText(p.getPreviousIllnesses());
            } else {
                viewHolder.name.setText("No Medical History Availiable");
            }
            viewHolder.lyt_parent.setOnClickListener(new View.OnClickListener() {
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