package com.csc131.deltamedicalteam.ui.healthcondition;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

public class HealthConditionFragment extends Fragment {

    private com.csc131.deltamedicalteam.databinding.FragmentHealthConditionBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HealthConditionViewModel homeViewModel =
                new ViewModelProvider(this).get(HealthConditionViewModel.class);

        binding = com.csc131.deltamedicalteam.databinding.FragmentHealthConditionBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHealthCondition;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}