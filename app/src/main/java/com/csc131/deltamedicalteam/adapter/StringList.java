package com.csc131.deltamedicalteam.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.csc131.deltamedicalteam.R;
import com.csc131.deltamedicalteam.model.User;

import java.util.ArrayList;
import java.util.List;

public class StringList extends RecyclerView.Adapter<StringList.OriginalViewHolder> {

    private List<String> items;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public List<String> getItem() {
        return items;
    }

    public void updateItems(List<String> updatedItems){
        items.clear();
        items.addAll(updatedItems);
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, String item, int position);
    }

    public StringList(Context context, List<String> items) {
        this.context = context;
        this.items = items;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public OriginalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_medication_current_list, parent, false);
        return new OriginalViewHolder(v);
    }

    public void setFilteredList(List<String> filteredList ) {
        this.items = filteredList;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull OriginalViewHolder holder, int position) {
        String medication = items.get(position);
        holder.bind(medication);
    }



    @Override
    public int getItemCount() {
        return items.size();
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name;

        public OriginalViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.list_item_current_medication);
            itemView.setOnClickListener(this);
        }

        public void bind(String medication) {
            name.setText(medication);
        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    onItemClickListener.onItemClick(v, items.get(position), position);
                }
            }
        }
    }
}
