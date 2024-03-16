package com.csc131.deltamedicalteam.ui.user;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView; // Add import for TextView

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.csc131.deltamedicalteam.R;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    @SuppressLint("RestrictedApi")
    private List<UserManagerFragment.mUser> userList;

    // Constructor with List<User>
    public UserAdapter(List<UserManagerFragment.mUser> userList) {
        this.userList = userList;
    }

    // Constructor with no argument
    public UserAdapter() {
        this.userList = new ArrayList<>();
    }





    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_user, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserManagerFragment.mUser user = userList.get(position);
        holder.userNameTextView.setText(user.getUserName());
        // Bind other data to views as needed
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView userNameTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameTextView = itemView.findViewById(R.id.user_name_text_view);
        }
    }
}
