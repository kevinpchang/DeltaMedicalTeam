package com.csc131.deltamedicalteam.ui.profile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.csc131.deltamedicalteam.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class ProfileFragment extends Fragment {

    TextView emailProfile, mName, mPermission;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    String userID;
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

//                mNameEditText.setText(fullName);
//                mPermissionEditText.setText(permission);
                mEmailEditText.setText(email);
                mPhoneEditText.setText(phone);
                mAddressEditText.setText(address);
                mLocationEditText.setText(location);
            }
        });

        mEditButton.setOnClickListener(view -> {
            // Show EditText fields and Save button, hide TextViews and Edit button
            mNameTextView.setVisibility(View.GONE);
            mMemberIDTextView.setVisibility(View.GONE);
            mEmailTextView.setVisibility(View.GONE);
            mPermissionTextView.setVisibility(View.GONE);
            mPhoneTextView.setVisibility(View.GONE);
            mAddressTextView.setVisibility(View.GONE);
            mLocationTextView.setVisibility(View.GONE);

//            mNameEditText.setVisibility(View.VISIBLE);
//            mPermissionEditText.setVisibility(View.VISIBLE);
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
//            String newName = mNameEditText.getText().toString();
//            String newPermission = mPermissionEditText.getText().toString();
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
//                        mNameTextView.setText(newName);
//                        mPermissionTextView.setText(newPermission);
                        mPhoneTextView.setText(newPhone);
                        mEmailTextView.setText(newPhone);
                        mAddressTextView.setText(newAddress);
                        mLocationTextView.setText(newLocation);


                        mMemberIDTextView.setVisibility(View.VISIBLE);
                        mNameTextView.setVisibility(View.VISIBLE);
                        mEmailTextView.setVisibility(View.VISIBLE);
                        mPermissionTextView.setVisibility(View.VISIBLE);
                        mPhoneTextView.setVisibility(View.VISIBLE);
                        mAddressTextView.setVisibility(View.VISIBLE);
                        mLocationTextView.setVisibility(View.VISIBLE);

//                        mNameEditText.setVisibility(View.GONE);
//                        mPermissionEditText.setVisibility(View.GONE);
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

        return root;


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}