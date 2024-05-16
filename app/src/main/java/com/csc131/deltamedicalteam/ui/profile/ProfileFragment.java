package com.csc131.deltamedicalteam.ui.profile;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.csc131.deltamedicalteam.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfileFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    String userID;
    ImageView mProfileImage;
    private com.csc131.deltamedicalteam.databinding.FragmentProfileBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
//        ProfileViewModel ProfileViewModel =
//                new ViewModelProvider(this).get(ProfileViewModel.class);

        binding = com.csc131.deltamedicalteam.databinding.FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

//       EditText mNameEditText = root.findViewById(R.id.profile_name_edit);
//       EditText mPermissionEditText = root.findViewById(R.id.profile_permission_edit);
        EditText mEmailEditText = root.findViewById(R.id.profile_email_edit);
        EditText  mPhoneEditText = root.findViewById(R.id.profile_phone_edit);
        EditText  mAddressEditText = root.findViewById(R.id.profile_address_edit);
        EditText mLocationEditText = root.findViewById(R.id.profile_location_edit);

        TextView mMemberIDTextView = root.findViewById(R.id.profile_memberID);
        TextView mNameTextView = root.findViewById(R.id.profile_name);
        TextView mEmailTextView = root.findViewById(R.id.profile_email);
        TextView mPermissionTextView = root.findViewById(R.id.profile_permission);
        TextView mPhoneTextView = root.findViewById(R.id.profile_phone);
        TextView mAddressTextView = root.findViewById(R.id.profile_address);
        TextView mLocationTextView = root.findViewById(R.id.profile_location);

        Button mEditButton = root.findViewById(R.id.profile_edit_button);
        Button  mSaveButton = root.findViewById(R.id.profile_save_button);
        Button mResetPwdBtn = root.findViewById(R.id.reset_pwd_btn);

        mProfileImage = root.findViewById(R.id.image);
        // Set click listener for profile image
        mProfileImage.setOnClickListener(v -> selectProfilePicture());




        //init Database
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = fAuth.getCurrentUser().getUid();

        DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {

                String fName = documentSnapshot.getString("fName");
                String lName = documentSnapshot.getString("lName");
                String permission = documentSnapshot.getString("permission");
                String phone = documentSnapshot.getString("phone");
                String address = documentSnapshot.getString("address");
                String location = documentSnapshot.getString("location");
                String email = documentSnapshot.getString("email");
                String fullName = fName + " " + lName;

                // Extract first three characters of permission and last five characters of userID
                String memberID = (permission.substring(0, Math.min(permission.length(), 3)) +
                        userID.substring(userID.length() - 5)).toUpperCase();

                mNameTextView.setText(fullName);
                mMemberIDTextView.setText(memberID);
                mEmailTextView.setText(email);
                mPermissionTextView.setText(permission);
                mPhoneTextView.setText(phone);
                mAddressTextView.setText(address);
                mLocationTextView.setText(location);

                mEmailEditText.setText(email);
                mPhoneEditText.setText(phone);
                mAddressEditText.setText(address);
                mLocationEditText.setText(location);
            }
        });

        mEditButton.setOnClickListener(view -> {
            // Show EditText fields and Save button, hide TextViews and Edit button

            mEmailTextView.setVisibility(View.GONE);
            mPhoneTextView.setVisibility(View.GONE);
            mAddressTextView.setVisibility(View.GONE);
            mLocationTextView.setVisibility(View.GONE);

            mEmailEditText.setVisibility(View.VISIBLE);
            mPhoneEditText.setVisibility(View.VISIBLE);
            mAddressEditText.setVisibility(View.VISIBLE);
            mLocationEditText.setVisibility(View.VISIBLE);

            mEditButton.setVisibility(View.GONE);
            mSaveButton.setVisibility(View.VISIBLE);
            mResetPwdBtn.setVisibility(View.GONE);
        });

        mSaveButton.setOnClickListener(view -> {
            // Update Firestore with new values
            String newPhone = mPhoneEditText.getText().toString();
            String newAddress = mAddressEditText.getText().toString();
            String newLocation = mLocationEditText.getText().toString();
            String newEmail = mEmailEditText.getText().toString();

            DocumentReference userRef = fStore.collection("users").document(userID);
            userRef.update("phone", newPhone,
                            "address", newAddress,
                            "location", newLocation,
                            "email", newEmail)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                        // Hide EditText fields and Save button, show TextViews and Edit button

                        mPhoneTextView.setText(newPhone);
                        mEmailTextView.setText(newEmail);
                        mAddressTextView.setText(newAddress);
                        mLocationTextView.setText(newLocation);


                        mEmailTextView.setVisibility(View.VISIBLE);
                        mPhoneTextView.setVisibility(View.VISIBLE);
                        mAddressTextView.setVisibility(View.VISIBLE);
                        mLocationTextView.setVisibility(View.VISIBLE);

                        mEmailEditText.setVisibility(View.GONE);
                        mPhoneEditText.setVisibility(View.GONE);
                        mAddressEditText.setVisibility(View.GONE);
                        mLocationEditText.setVisibility(View.GONE);

                        mEditButton.setVisibility(View.VISIBLE);
                        mSaveButton.setVisibility(View.GONE);
                        mResetPwdBtn.setVisibility(View.VISIBLE);
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firestore Error", "Error updating document: " + e.getMessage());
                        Toast.makeText(getContext(), "Failed to update profile", Toast.LENGTH_SHORT).show();
                    });
        });

        mResetPwdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText resetMail = new EditText(v.getContext());
                AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
                passwordResetDialog.setTitle("Reset Password");
                passwordResetDialog.setMessage("Enter email address to receive reset link");
                passwordResetDialog.setView(resetMail);

                passwordResetDialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //extract email and send reset link
                        String mail = resetMail.getText().toString();
                        fAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getContext(), "Reset Link Sent to Your Email.", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "Error ! Reset Link is Not Sent" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });


                passwordResetDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Close

                    }
                });
                passwordResetDialog.create().show();
            }
        });







        return root;


    }

    @Override
    public void onStart() {
        super.onStart();
        // Load profile picture when the fragment starts
        loadProfilePicture();
    }

    private void loadProfilePicture() {
        DocumentReference userRef = fStore.collection("users").document(userID);
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String profilePictureUrl = documentSnapshot.getString("profilePictureUrl");
                if (profilePictureUrl != null && !profilePictureUrl.isEmpty()) {
                    Glide.with(requireContext()).load(profilePictureUrl).into(mProfileImage);
                }
//                else {
//                    // If profile picture URL is not available, you can set a default image or hide the ImageView
//                    // For example:
//                     mProfileImage.setImageResource(R.drawable.no_avatar);
//                    // or
//                    // mProfileImage.setVisibility(View.GONE);
//                }
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Error loading profile picture URL from Firestore: " + e.getMessage());
        });
    }
    // Method to handle image selection (e.g., from gallery or camera)
    private void selectProfilePicture() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            uploadProfilePicture(imageUri);
        }
    }

    private void uploadProfilePicture(Uri imageUri) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("profile_pictures/" + userID);

        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String profilePictureUrl = uri.toString();
                        updateProfilePictureInFirestore(profilePictureUrl);
                    });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error uploading profile picture: " + e.getMessage());
                    Toast.makeText(getContext(), "Failed to upload profile picture", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateProfilePictureInFirestore(String profilePictureUrl) {
        DocumentReference userRef = fStore.collection("users").document(userID);
        userRef.update("profilePictureUrl", profilePictureUrl)
                .addOnSuccessListener(aVoid -> {
                    // Update the profile image ImageView with the new profile picture URL
                    Glide.with(requireContext()).load(profilePictureUrl).into(mProfileImage);
                    Toast.makeText(getContext(), "Profile picture updated successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error updating profile picture URL in Firestore: " + e.getMessage());
                    Toast.makeText(getContext(), "Failed to update profile picture", Toast.LENGTH_SHORT).show();
                });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}