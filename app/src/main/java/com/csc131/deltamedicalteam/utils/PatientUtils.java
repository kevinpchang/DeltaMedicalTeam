package com.csc131.deltamedicalteam.utils;
import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.csc131.deltamedicalteam.model.Patient;

import java.util.ArrayList;
import java.util.List;

public class PatientUtils {

    // Method to retrieve a list of patient names from a list of Patient objects
    public static List<String> getPatientNames(List<Patient> patients) {
        List<String> names = new ArrayList<>();
        for (Patient patient : patients) {
            // Concatenate first name and last name to form the full name
            String fullName = patient.getName();
            names.add(fullName);
        }
        return names;
    }

    // Method to populate a Spinner with a list of items
    public static void populateSpinner(Context context, Spinner spinner, List<String> items) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }
}
