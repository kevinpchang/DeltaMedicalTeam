package com.csc131.deltamedicalteam.ui.patient;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

public class PatientFragment extends Fragment {

    private com.csc131.deltamedicalteam.databinding.FragmentPatientBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        PatientViewModel galleryViewModel =
                new ViewModelProvider(this).get(PatientViewModel.class);

        binding = com.csc131.deltamedicalteam.databinding.FragmentPatientBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textPatient;

        galleryViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}