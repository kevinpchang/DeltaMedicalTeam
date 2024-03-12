package com.csc131.deltamedicalteam.ui.appointment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.csc131.deltamedicalteam.databinding.FragmentAppointmentBinding;

public class AppointmentFragment extends Fragment {

    private FragmentAppointmentBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AppointmentViewModel slideshowViewModel =
                new ViewModelProvider(this).get(AppointmentViewModel.class);

        binding = FragmentAppointmentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textAppointment;
        slideshowViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}