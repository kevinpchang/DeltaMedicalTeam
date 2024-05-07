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

public class CurrentIllnessList extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<HealthConditions> hCons;

    private Context ctx;
    private OnItemClickListener mOnItemClickListener;




    public interface OnItemClickListener {
        void onItemClick(View view, HealthConditions obj, int position);
    }

//    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
//        this.mOnItemClickListener = mItemClickListener;
//    }

    public void setOnItemClickListener(CurrentIllnessList.OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public CurrentIllnessList(Context context, List<HealthConditions> hCons) {
        this.hCons = hCons;
        ctx = context;
    }

    public List<HealthConditions> getHealthConditions() { return hCons; }

    public void updateCurrentIllnesses(List<HealthConditions> updatedItems){
        hCons.clear();
        hCons.addAll(updatedItems);
        notifyDataSetChanged();
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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_health_current_list, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof OriginalViewHolder) {
            OriginalViewHolder viewHolder = (OriginalViewHolder) holder;

            HealthConditions p = hCons.get(position);
            //used to check if array is empty or not
            if (p.getCurrentIllnesses() != null) {
                viewHolder.name.setText(p.getCurrentIllnesses());
            } else {
                viewHolder.name.setText("No current illnesses");
            }

            viewHolder.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int adapterPosition = holder.getAdapterPosition();
                    if (adapterPosition != RecyclerView.NO_POSITION && mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(view, hCons.get(adapterPosition), adapterPosition);
                    }
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        return hCons.size();
    }

}