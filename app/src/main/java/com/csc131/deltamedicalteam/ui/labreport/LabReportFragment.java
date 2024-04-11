package com.csc131.deltamedicalteam.ui.labreport;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.csc131.deltamedicalteam.databinding.FragmentHomeBinding;
import com.csc131.deltamedicalteam.databinding.FragmentLabReportBinding;

public class LabReportFragment extends Fragment {

    private FragmentLabReportBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        LabReportViewModel homeViewModel =
                new ViewModelProvider(this).get(LabReportViewModel.class);

        binding = FragmentLabReportBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textLabReport;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}