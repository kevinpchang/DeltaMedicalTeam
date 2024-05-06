package com.csc131.deltamedicalteam.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.csc131.deltamedicalteam.R;
import com.csc131.deltamedicalteam.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserList extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<User> items = new ArrayList<>();


    private Context ctx;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, User obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public UserList(Context context, List<User> items) {
        this.items = items;
        ctx = context;
    }



    public void updateUsers(List<User> updatedUsers) {
        items.clear();
        items.addAll(updatedUsers);
        notifyDataSetChanged();
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView name;
        public View lyt_parent;
        public TextView email;
        public TextView phone;
        public TextView permission;

        public OriginalViewHolder(View v) {
            super(v);
            image = (ImageView) v.findViewById(R.id.image);
            name = (TextView) v.findViewById(R.id.list_item_name);
            email = (TextView) v.findViewById(R.id.list_item_address);
            phone = (TextView) v.findViewById(R.id.list_item_phone);
            permission = (TextView) v.findViewById(R.id.patient_list_item_permission);
            lyt_parent = (View) v.findViewById(R.id.lyt_parent);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_list, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    public List<User> getUsers() {
        return items;
    }

    // Replace the contents of a view (invoked by the layout manager)
public void setFilteredList(List<User> filteredList ) {
        this.items = filteredList;
        notifyDataSetChanged();
}


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof OriginalViewHolder) {
            OriginalViewHolder viewHolder = (OriginalViewHolder) holder;

            User user = items.get(position); // Use dataListFiltered here
            viewHolder.name.setText(user.getName());
            viewHolder.email.setText(user.getEmail());
            viewHolder.phone.setText(user.getPhone());
            viewHolder.permission.setText(user.getPermission());

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