package com.csc131.deltamedicalteam.ui.profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.csc131.deltamedicalteam.R;
import com.csc131.deltamedicalteam.model.User;

public class ProfileUserFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        com.csc131.deltamedicalteam.databinding.FragmentProfileBinding binding = com.csc131.deltamedicalteam.databinding.FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Retrieve the passed user information
        ProfileUserFragmentArgs args = ProfileUserFragmentArgs.fromBundle(getArguments());
        User user = args.getUser();

        // Display the user information
        TextView nameTextView = view.findViewById(R.id.profile_name);
        TextView emailTextView = view.findViewById(R.id.profile_email);
        TextView permissionTextView = view.findViewById(R.id.profile_permission);
        TextView phoneTextView = view.findViewById(R.id.profile_phone);

        // Set user information in TextViews
        nameTextView.setText(user.getName());
        emailTextView.setText(user.getEmail());
        permissionTextView.setText(user.getPermission());
        phoneTextView.setText(user.getPhone());

        // Set up call button
        ImageButton callButton = view.findViewById(R.id.call_btn);
        if (user.getPhone() == null || user.getPhone().isEmpty()) {
            callButton.setVisibility(View.GONE); // Hide call button if phone number is empty
        } else {
            callButton.setVisibility(View.VISIBLE); // Show call button if phone number is valid
            callButton.setOnClickListener(v -> makeCall(user.getPhone()));
        }

        // Set up message button
        ImageButton messageButton = view.findViewById(R.id.msg_btn);
        if (user.getPhone() == null || user.getPhone().isEmpty()) {
            messageButton.setVisibility(View.GONE); // Hide message button if phone number is empty
        } else {
            messageButton.setVisibility(View.VISIBLE); // Show message button if phone number is valid
            messageButton.setOnClickListener(v -> sendMessage(user.getPhone()));
        }
    }

    private void makeCall(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(intent);
    }

    private void sendMessage(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("smsto:" + phoneNumber));
        startActivity(intent);
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}