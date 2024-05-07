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
import androidx.recyclerview.widget.RecyclerView;

import com.csc131.deltamedicalteam.R;
import com.csc131.deltamedicalteam.model.Patient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

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
                String docID = mCurrentPatient.getDocumentId();

                //StorageReference patientStorageRef = storageRef.child("patients/"+"test"+"/");
                //StorageReference folderRef = patientStorageRef.child("profile.jpg");
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
                                recyclerViewReport.setVisibility(View.VISIBLE);
                                recyclerViewPhoto.setVisibility(View.GONE);
                                recyclerViewVideo.setVisibility(View.GONE);
                                tabLayoutPosition = tab.getPosition();
                                tabName = "lab_reports";
                                break;
                            case 1:
                                recyclerViewReport.setVisibility(View.GONE);
                                recyclerViewPhoto.setVisibility(View.VISIBLE);
                                recyclerViewVideo.setVisibility(View.GONE);
                                tabLayoutPosition = tab.getPosition();
                                tabName = "photos";
                                break;
                            case 2:
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

        //health_condition_edit button onClick
//        mAddEditButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                EditText resetMail = new EditText(v.getContext());
//                AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
//                passwordResetDialog.setTitle("Add more field");
//                passwordResetDialog.setMessage("Enter email address to receive reset link");
//                passwordResetDialog.setView(resetMail);
//
//                passwordResetDialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        //extract email and send reset link
//                        String mail = resetMail.getText().toString();
//                        fAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void aVoid) {
//                                Toast.makeText(Login.this, "Reset Link Sent to Your Email.", Toast.LENGTH_SHORT).show();
//                            }
//                        }).addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                Toast.makeText(Login.this, "Error ! Reset Link is Not Sent" + e.getMessage(), Toast.LENGTH_SHORT).show();
//                            }
//                        });
//
//                    }
//                });
//
//
//                passwordResetDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        //Close
//
//                    }
//                });
//                passwordResetDialog.create().show();
//            }
//        });

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

    private void registerFilePicker(){
        filePicker = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> onPickFile(uri));
    }

    private void openFilePicker(){
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

    private void onPickFile(Uri uri){
        if(uri != null){
            StorageReference tempRef = storageRef.child("patients/"+mCurrentPatient.getDocumentId()+"/"+tabName+"/"+uri.getLastPathSegment());
            UploadTask uploadTask = tempRef.putFile(uri);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getActivity(), "File Uploaded Successfully", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getActivity(), "An Error Occurred While Uploading", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}