package com.csc131.deltamedicalteam.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.csc131.deltamedicalteam.R;

import java.util.ArrayList;
import java.util.List;

public class CurrentAllergiesList extends RecyclerView.Adapter<CurrentAllergiesList.OriginalViewHolder> {

    private final List<String> allergies;

    private OnItemClickListener mOnItemClickListener;
    private final List<String> items_swiped = new ArrayList<>(); // Keep track of swiped items



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
    @SuppressLint("NotifyDataSetChanged")
    public void updateAllergies(List<String> updatedAllergies) {
        allergies.clear();
        allergies.addAll(updatedAllergies);
        notifyDataSetChanged();
    }

    public static class OriginalViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public ImageButton bt_move;
        public Button bt_undo;
        public boolean swiped; // Added field to track swiped state

        public View lyt_parent;

        public OriginalViewHolder(View v) {
            super(v);
            name = v.findViewById(R.id.list_item_name);
            lyt_parent = v.findViewById(R.id.lyt_parent);
            bt_move = v.findViewById(R.id.bt_move);
            bt_undo = v.findViewById(R.id.bt_undo);
            swiped = false; // Initialize swiped state to false
        }
    }



    @NonNull
    @Override
    public OriginalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_health_allergies_list, parent, false);
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

//    @Override
//    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//                for (String item : items_swiped) {
//                    int index_removed = allergies.indexOf(item);
//                    if (index_removed != -1) {
//                        allergies.remove(index_removed);
//                        notifyItemRemoved(index_removed);
//                    }
//                }
//                items_swiped.clear();
//                super.onScrollStateChanged(recyclerView, newState);
//            }
//        });
//        super.onAttachedToRecyclerView(recyclerView);
//    }


    @Override
    public int getItemCount() {
        return allergies.size();
    }





}
