package com.csc131.deltamedicalteam.ui.labreport;

import static android.content.ContentValues.TAG;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.csc131.deltamedicalteam.R;
import com.csc131.deltamedicalteam.adapter.StringList;
import com.csc131.deltamedicalteam.model.Patient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

public class LabReportFragment extends Fragment {

    private com.csc131.deltamedicalteam.databinding.FragmentLabReportBinding binding;

    // Initialize Firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();

    // Reference to the "patients" collection
    private CollectionReference patientsRef = db.collection("patients");

    private StorageReference storageRef = storage.getReference();
    private ActivityResultLauncher<String> filePicker;

    private Button mUploadButton;
    private Spinner patientSpinner;
    RecyclerView recyclerViewReport, recyclerViewPhoto, recyclerViewVideo;
    TabLayout tabLayout;

    private StringList reportListAdapter;
    private StringList photoListAdapter;
    private StringList radListAdapter;
    private int tabLayoutPosition;
    private String tabName;
    private Patient mCurrentPatient;
    List<Patient> patientNames = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_lab_report, container, false);

        patientSpinner = rootView.findViewById(R.id.lab_report_patient_list_spinner);
        getPatientList();
        registerFilePicker();

        // Find RecyclerViews
        recyclerViewReport = rootView.findViewById(R.id.RecyclerView_report);
        recyclerViewPhoto = rootView.findViewById(R.id.RecyclerView_photo);
        recyclerViewVideo = rootView.findViewById(R.id.RecyclerView_current_video);

        recyclerViewReport.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewPhoto.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewVideo.setLayoutManager(new LinearLayoutManager(getActivity()));

        //button
        mUploadButton = rootView.findViewById(R.id.current_lab_report_edit);

        // Find TabLayout
        tabLayout = rootView.findViewById(R.id.labReportTabs);
        tabName = "lab_reports";

        //detects when spinner item is selected
        patientSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mCurrentPatient = (Patient) parent.getItemAtPosition(position);
                updateTabs();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        mUploadButton.setOnClickListener(v -> {
            openFilePicker();
        });
                // Set up OnTabSelectedListener
                tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        // Show corresponding RecyclerView and hide others
                        switch (tab.getPosition()) {
                            case 0:
                                updateTabs();
                                recyclerViewReport.setVisibility(View.VISIBLE);
                                recyclerViewPhoto.setVisibility(View.GONE);
                                recyclerViewVideo.setVisibility(View.GONE);
                                tabLayoutPosition = tab.getPosition();
                                tabName = "lab_reports";
                                break;
                            case 1:
                                updateTabs();
                                recyclerViewReport.setVisibility(View.GONE);
                                recyclerViewPhoto.setVisibility(View.VISIBLE);
                                recyclerViewVideo.setVisibility(View.GONE);
                                tabLayoutPosition = tab.getPosition();
                                tabName = "photos";
                                break;
                            case 2:
                                updateTabs();
                                recyclerViewReport.setVisibility(View.GONE);
                                recyclerViewPhoto.setVisibility(View.GONE);
                                recyclerViewVideo.setVisibility(View.VISIBLE);
                                tabLayoutPosition = tab.getPosition();
                                tabName = "radiology";
                                break;
                        }
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {
                        // No action needed
                        updateTabs();
                        recyclerViewReport.setVisibility(View.VISIBLE);
                        recyclerViewPhoto.setVisibility(View.GONE);
                        recyclerViewVideo.setVisibility(View.GONE);
                        tabLayoutPosition = tab.getPosition();
                        tabName = "lab_reports";

                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {
                        // No action needed
                    }
                });
        return rootView;
    }

    // Method to fetch the list of patients, spinner is populated with patient objects
    private void getPatientList() {
        patientsRef.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        // Get patient name from DocumentSnapshot
                        // Get patient id from DocumentSnapshot
                        Patient patient = documentSnapshot.toObject(Patient.class);

                        assert patient != null;
                        patient.fromDocumentSnapshot(documentSnapshot);
//                        String documentId = documentSnapshot.getId();
//                        patient.setDocumentId(documentId);

                        // Add patient name to the list
                        patientNames.add(patient);
                    }

                    // Pass the list of patient names to populate the spinner
                    populateSpinner(patientNames);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching documents: " + e.getMessage());
                });
    }

    // Method to populate the spinner with patient names
    private void populateSpinner(List<Patient> patientNames) {
        // Create an ArrayAdapter using the patientNames list and a default spinner layout
        ArrayAdapter<Patient> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, patientNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        patientSpinner.setAdapter(adapter);
    }

    private void registerFilePicker() {
        filePicker = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> onPickFile(uri));
    }

    private void openFilePicker() {
        switch (tabLayoutPosition){
            case 0:
                filePicker.launch("application/pdf");
                break;
            case 1:
                filePicker.launch("image/*");
                break;
            case 2:
                filePicker.launch("video/*");
                break;
        }
    }

    private void onPickFile(Uri uri) {
        if(uri != null){
            StorageReference tempRef = storageRef.child("patients/"+mCurrentPatient.getDocumentId()+"/"+tabName+"/"+uri.getLastPathSegment());
            UploadTask uploadTask = tempRef.putFile(uri);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getActivity(), "File Uploaded Successfully", Toast.LENGTH_SHORT).show();
                    updateTabs();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getActivity(), "An Error Occurred While Uploading", Toast.LENGTH_SHORT).show();
                    updateTabs();
                }
            });
        }
    }

    private void updateTabs() {
        StorageReference reportRef = storageRef.child("patients/"+mCurrentPatient.getDocumentId()+"/lab_reports");
        List<String> reportListFiles = new ArrayList<>();
        reportRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                List<StorageReference> results = listResult.getItems();
                for(int i = 0; i < results.size(); i++) {
                    String resultsURL = results.get(i).toString();
                    Log.d("MYTAG", resultsURL);
                    int index = resultsURL.indexOf("lab_reports");
                    String tempFileName = resultsURL.substring(index);
                    String unconvertedFileName = tempFileName.substring("lab_reports/".length());
                    String fileName = URLDecoder.decode(unconvertedFileName);
                    reportListFiles.add(fileName);
                }
                reportListAdapter = new StringList(getActivity(), reportListFiles);
                recyclerViewReport.setAdapter(reportListAdapter);
            }
        });

        StorageReference photoRef = storageRef.child("patients/"+mCurrentPatient.getDocumentId()+"/photos");
        List<String> photoListFiles = new ArrayList<>();
        photoRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                List<StorageReference> results = listResult.getItems();
                for(int i = 0; i < results.size(); i++) {
                    String resultsURL = results.get(i).toString();
                    Log.d("MYTAG", resultsURL);
                    int index = resultsURL.indexOf("photos");
                    String tempFileName = resultsURL.substring(index);
                    String unconvertedFileName = tempFileName.substring("photos/".length());
                    String fileName = URLDecoder.decode(unconvertedFileName);
                    photoListFiles.add(fileName);
                }
                photoListAdapter = new StringList(getActivity(), photoListFiles);
                recyclerViewPhoto.setAdapter(photoListAdapter);
            }
        });

        StorageReference radRef = storageRef.child("patients/"+mCurrentPatient.getDocumentId()+"/radiology");
        List<String> radListFiles = new ArrayList<>();
        radRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                List<StorageReference> results = listResult.getItems();
                Log.d("MYTAG", "SUCCESS");
                for(int i = 0; i < results.size(); i++) {
                    String resultsURL = results.get(i).toString();
                    Log.d("MYTAG", resultsURL);
                    int index = resultsURL.indexOf("radiology");
                    String tempFileName = resultsURL.substring(index);
                    String unconvertedFileName = tempFileName.substring("radiology/".length());
                    String fileName = URLDecoder.decode(unconvertedFileName);
                    radListFiles.add(fileName);
                }
                radListAdapter = new StringList(getActivity(), radListFiles);
                recyclerViewVideo.setAdapter(radListAdapter);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}