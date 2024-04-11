package com.csc131.deltamedicalteam.adapter;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.csc131.deltamedicalteam.R;

import java.util.List;

public class CurrentAllergiesList extends RecyclerView.Adapter<CurrentAllergiesList.OriginalViewHolder> {

    private final List<String> allergies;

    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, String allergy, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public CurrentAllergiesList(List<String> allergies) {
        this.allergies = allergies;
    }

    // Method to update the list of allergies
    public void updateAllergies(List<String> updatedAllergies) {
        allergies.clear();
        allergies.addAll(updatedAllergies);
        notifyDataSetChanged();
    }

    public static class OriginalViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public View lyt_parent;

        public OriginalViewHolder(View v) {
            super(v);
            name = v.findViewById(R.id.list_item_name);
            lyt_parent = v.findViewById(R.id.lyt_parent);
        }
    }

    @NonNull
    @Override
    public OriginalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_health_current_list, parent, false);
        return new OriginalViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull OriginalViewHolder holder, final int position) {
        String allergy = allergies.get(position);
        if (allergy != null && !allergy.isEmpty()) {
            holder.name.setText(allergy);
        } else {
            holder.name.setText("No current allergies");
        }

        holder.lyt_parent.setOnClickListener(view -> {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(view, allergy, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return allergies.size();
    }
}
