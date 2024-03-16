package com.csc131.deltamedicalteam.ui.medication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

public class MedicationFragment extends Fragment {

    private com.csc131.deltamedicalteam.databinding.FragmentMedicationBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MedicationViewModel homeViewModel =
                new ViewModelProvider(this).get(MedicationViewModel.class);

        binding = com.csc131.deltamedicalteam.databinding.FragmentMedicationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textMedication;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}