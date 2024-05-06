package com.csc131.deltamedicalteam.ui.profile;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.csc131.deltamedicalteam.R;
import com.csc131.deltamedicalteam.model.Medication;
import com.csc131.deltamedicalteam.model.User;

public class ProfileCurrentMedicationFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        com.csc131.deltamedicalteam.databinding.FragmentProfileCurrentMedicationBinding binding = com.csc131.deltamedicalteam.databinding.FragmentProfileCurrentMedicationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Retrieve the passed user information
        ProfileCurrentMedicationFragmentArgs args = ProfileCurrentMedicationFragmentArgs.fromBundle(getArguments());
        String med = args.getMedication();

        // Display the user information
        TextView nameTextView = view.findViewById(R.id.profile_name);
        TextView emailTextView = view.findViewById(R.id.profile_email);
        TextView permissionTextView = view.findViewById(R.id.profile_permission);
        TextView phoneTextView = view.findViewById(R.id.profile_phone);

//        // Set user information in TextViews
//        nameTextView.setText(med.getName());
//        emailTextView.setText(med.getEmail());
//        permissionTextView.setText(med.getPermission());
//        phoneTextView.setText(med.getPhone());

        // Set up edit buttons click listeners
        ImageButton editNameButton = view.findViewById(R.id.edit_name_btn);
        ImageButton editEmailButton = view.findViewById(R.id.edit_email_btn);
        ImageButton editPhoneButton = view.findViewById(R.id.edit_phone_btn);


//
//        // Set click listeners to allow editing directly in the fields
//        editNameButton.setOnClickListener(v -> showEditDialog("Name", med.getName(), nameTextView));
//        editEmailButton.setOnClickListener(v -> showEditDialog("Email", med.getEmail(), emailTextView));
//        editPhoneButton.setOnClickListener(v -> showEditDialog("Phone", med.getPhone(), phoneTextView));
//
//
//        // Set up call button
//        ImageButton callButton = view.findViewById(R.id.call_btn);
//        if (med.getPhone() == null || med.getPhone().isEmpty()) {
//            callButton.setVisibility(View.GONE); // Hide call button if phone number is empty
//        } else {
//            callButton.setVisibility(View.VISIBLE); // Show call button if phone number is valid
//            callButton.setOnClickListener(v -> makeCall(med.getPhone()));
//        }
//
//        // Set up message button
//        ImageButton messageButton = view.findViewById(R.id.msg_btn);
//        if (med.getPhone() == null || med.getPhone().isEmpty()) {
//            messageButton.setVisibility(View.GONE); // Hide message button if phone number is empty
//        } else {
//            messageButton.setVisibility(View.VISIBLE); // Show message button if phone number is valid
//            messageButton.setOnClickListener(v -> sendMessage(user.getPhone()));
//        }
    }


    // Method to show a dialog for editing user information
    private void showEditDialog(String field, String currentValue, TextView textView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Edit " + field);

        // Create an EditText for editing the value
        final EditText editText = new EditText(requireContext());
        editText.setText(currentValue);
        builder.setView(editText);

        // Set up the buttons
        builder.setPositiveButton("Save", (dialog, which) -> {
            String newValue = editText.getText().toString().trim();
            textView.setText(newValue);
            // You can perform further actions here, like updating the user object
            // with the new value and saving it to a database, etc.
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
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