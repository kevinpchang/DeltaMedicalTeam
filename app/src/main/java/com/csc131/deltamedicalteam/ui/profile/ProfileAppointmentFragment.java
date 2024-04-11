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
import com.csc131.deltamedicalteam.model.Patient;

public class ProfileAppointmentFragment extends Fragment {
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

        // Retrieve the passed patient information
        ProfilePatientFragmentArgs args = ProfilePatientFragmentArgs.fromBundle(getArguments());
        Patient patient = args.getPatient();

        // Display the patient information
        TextView nameTextView = view.findViewById(R.id.profile_name);
//        TextView addressTextView = view.findViewById(R.id.profile_address);
        TextView phoneTextView = view.findViewById(R.id.profile_phone);

        // Add more TextViews for other patient information

        if (nameTextView != null) {
            nameTextView.setText(patient.getName());
        } else {
            Log.e("TextView Error", "nameTextView is null");
        }

//        if (addressTextView != null) {
//            addressTextView.setText(patient.getAddress());
//        } else {
//            Log.e("TextView Error", "addressTextView is null");
//        }

        if (phoneTextView != null) {
            phoneTextView.setText(patient.getCellPhone());
        } else {
            Log.e("TextView Error", "phoneTextView is null");
        }


    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}