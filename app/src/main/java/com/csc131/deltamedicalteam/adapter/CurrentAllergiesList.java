package com.csc131.deltamedicalteam.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.csc131.deltamedicalteam.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

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

    public List<String> getAllergies() {
        return allergies;
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



    public void onItemDismiss(int position) {
        // Remove the item from the list
        String removedAllergy = allergies.remove(position);

        // Notify the adapter about the removal
        notifyItemRemoved(position);

        // Remove the item from the database

    }

}
