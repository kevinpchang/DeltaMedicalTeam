package com.csc131.deltamedicalteam.ui.profile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.csc131.deltamedicalteam.R;
import com.csc131.deltamedicalteam.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class ProfileUserFragment extends Fragment {
    private com.csc131.deltamedicalteam.databinding.FragmentProfileBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = com.csc131.deltamedicalteam.databinding.FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
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
        // Add more TextViews for other user information

        nameTextView.setText(user.getName());
        emailTextView.setText(user.getEmail());
        permissionTextView.setText(user.getPermission());
        phoneTextView.setText(user.getPhone());
        // Set other TextViews with user information
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}