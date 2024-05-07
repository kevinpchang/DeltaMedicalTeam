package com.csc131.deltamedicalteam.ui.profile;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.csc131.deltamedicalteam.R;
import com.csc131.deltamedicalteam.model.User;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileUserFragment extends Fragment {

    FirebaseFirestore fStore;

    ImageView profileImage;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        com.csc131.deltamedicalteam.databinding.FragmentProfileUserBinding binding = com.csc131.deltamedicalteam.databinding.FragmentProfileUserBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Retrieve the passed user information
        ProfileUserFragmentArgs args = ProfileUserFragmentArgs.fromBundle(getArguments());
        User user = args.getUser();

        fStore = FirebaseFirestore.getInstance();
        DocumentReference userRef = fStore.collection("users").document(user.getDocumentId());


        profileImage = view.findViewById(R.id.image);

        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String profilePictureUrl = documentSnapshot.getString("profilePictureUrl");
                if (profilePictureUrl != null && !profilePictureUrl.isEmpty()) {
                    Glide.with(this).load(profilePictureUrl).into(profileImage);
                } else {
                    // If profile picture URL is not available, you can set a default image or hide the ImageView
                    profileImage.setImageResource(R.drawable.photo_male_1);
                    // or
                    // profileImage.setVisibility(View.GONE);
                }
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Error loading profile picture URL from Firestore: " + e.getMessage());
        });

        // Display the user information
        TextView nameTextView = view.findViewById(R.id.profile_name);
        TextView memberIDTextView = view.findViewById(R.id.profile_memberID);
        TextView emailTextView = view.findViewById(R.id.profile_email);
        TextView permissionTextView = view.findViewById(R.id.profile_permission);
        TextView phoneTextView = view.findViewById(R.id.profile_phone);
        TextView addressTextView = view.findViewById(R.id.profile_address);
        TextView locationTextView = view.findViewById(R.id.profile_location);

        EditText nameEditText = view.findViewById(R.id.profile_name_edit);
        EditText memberIDEditText = view.findViewById(R.id.profile_memberID_edit);
        EditText emailEditText = view.findViewById(R.id.profile_email_edit);
        EditText permissionEditText = view.findViewById(R.id.profile_permission_edit);
        EditText phoneEditText = view.findViewById(R.id.profile_phone_edit);
        EditText addressEditText = view.findViewById(R.id.profile_address_edit);
        EditText locationEditText = view.findViewById(R.id.profile_location_edit);

        Button mEditButton = view.findViewById(R.id.profile_edit_btn);
        Button mSaveButton = view.findViewById(R.id.profile_save_btn);

        // Set user information in TextViews
        nameTextView.setText(user.getName());
        memberIDTextView.setText(user.getMemberID());
        emailTextView.setText(user.getEmail());
        permissionTextView.setText(user.getPermission());
        phoneTextView.setText(user.getPhone());
        addressTextView.setText(user.getAddress());
        locationTextView.setText(user.getLocation());

        nameEditText.setText(user.getName());
        memberIDEditText.setText(user.getMemberID());
        emailEditText.setText(user.getEmail());
        permissionEditText.setText(user.getPermission());
        phoneEditText.setText(user.getPhone());
        addressEditText.setText(user.getAddress());
        locationEditText.setText(user.getLocation());


        mEditButton.setOnClickListener(v -> {
            // Show EditText fields and Save button, hide TextViews and Edit button

//            memberIDTextView.setVisibility(View.GONE);
            emailTextView.setVisibility(View.GONE);
            permissionTextView.setVisibility(View.GONE);
            phoneTextView.setVisibility(View.GONE);
            addressTextView.setVisibility(View.GONE);
            locationTextView.setVisibility(View.GONE);


            permissionEditText.setVisibility(View.VISIBLE);
            emailEditText.setVisibility(View.VISIBLE);
            phoneEditText.setVisibility(View.VISIBLE);
            addressEditText.setVisibility(View.VISIBLE);
            locationEditText.setVisibility(View.VISIBLE);

            mEditButton.setVisibility(View.GONE);
            mSaveButton.setVisibility(View.VISIBLE);

        });

        mSaveButton.setOnClickListener(v -> {
            // Update Firestore with new values

            String newPermission = permissionEditText.getText().toString();
            String newPhone = phoneEditText.getText().toString();
            String newAddress = addressEditText.getText().toString();
            String newLocation = locationEditText.getText().toString();
            String newEmail = emailEditText.getText().toString();

//            DocumentReference userRef = fStore.collection("users").document(user.getDocumentId());
            userRef.update(
                            "permission",newPermission,
                            "phone", newPhone,
                            "address", newAddress,
                            "location", newLocation,
                            "email", newEmail)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                        // Hide EditText fields and Save button, show TextViews and Edit button

                        permissionTextView.setText(newPermission);
                        phoneTextView.setText(newPhone);
                        addressTextView.setText(newAddress);
                        locationTextView.setText(newLocation);
                        emailTextView.setText(newEmail);


                        emailTextView.setVisibility(View.VISIBLE);
                        permissionTextView.setVisibility(View.VISIBLE);
                        phoneTextView.setVisibility(View.VISIBLE);
                        addressTextView.setVisibility(View.VISIBLE);
                        locationTextView.setVisibility(View.VISIBLE);


                        permissionEditText.setVisibility(View.GONE);
                        emailEditText.setVisibility(View.GONE);
                        phoneEditText.setVisibility(View.GONE);
                        addressEditText.setVisibility(View.GONE);
                        locationEditText.setVisibility(View.GONE);

                        mEditButton.setVisibility(View.VISIBLE);
                        mSaveButton.setVisibility(View.GONE);

                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firestore Error", "Error updating document: " + e.getMessage());
                        Toast.makeText(getContext(), "Failed to update profile", Toast.LENGTH_SHORT).show();
                    });
        });







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