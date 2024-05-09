package com.csc131.deltamedicalteam.ui.profile;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.csc131.deltamedicalteam.R;
import com.csc131.deltamedicalteam.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileIllnessFragment extends Fragment {


    FirebaseFirestore fStore;
    String illDescription, illPrevention, illSeverity, illSymptom, illTreatment;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        com.csc131.deltamedicalteam.databinding.FragmentProfileIllnessBinding binding = com.csc131.deltamedicalteam.databinding.FragmentProfileIllnessBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Retrieve the passed user information
        ProfileIllnessFragmentArgs args = ProfileIllnessFragmentArgs.fromBundle(getArguments());
        String health = args.getIllness();
        fStore = FirebaseFirestore.getInstance();

        // Display the user information
        TextView nameTextView = view.findViewById(R.id.profile_illnessName);
        TextView descriptionTextView = view.findViewById(R.id.textview_description);
        TextView preventionTextView = view.findViewById(R.id.textview_prevention);
        TextView severityTextView = view.findViewById(R.id.textview_severity);
        TextView symptomTextView = view.findViewById(R.id.textview_symptom);
        TextView treatmentTextView = view.findViewById(R.id.textview_treatment);

        ImageButton mPrint = view.findViewById(R.id.imageButton_illnessPrint);


        EditText descriptionEditText = view.findViewById(R.id.editTextTextMultiLine_illnessDescription);
        EditText preventionEditText = view.findViewById(R.id.editTextTextMultiLine_illnessPrevention);
        EditText severityEditText = view.findViewById(R.id.editTextTextMultiLine_illnessSeverity);
        EditText symptomEditText = view.findViewById(R.id.editTextTextMultiLine_illnessSymptom);
        EditText treatmentEditText = view.findViewById(R.id.editTextTextMultiLine_illnessTreatment);


        Button mEditButton = view.findViewById(R.id.illnessProfile_edit_btn);
        Button mSaveButton = view.findViewById(R.id.illnessProfile_save_btn);

        DocumentReference MedRef = fStore.collection("illnesses").document(health);
        MedRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    // Access individual fields
                    illDescription = documentSnapshot.getString("description");
                    illPrevention = documentSnapshot.getString("prevention");
                    illSeverity = documentSnapshot.getString("severity");
                    illSymptom = documentSnapshot.getString("symptom");
                    illTreatment = documentSnapshot.getString("treatment");

                    // Set user information in TextViews
                    nameTextView.setText(health);
                    descriptionTextView.setText(illDescription);
                    preventionTextView.setText(illPrevention);
                    severityTextView.setText(illSeverity);
                    symptomTextView.setText(illSymptom);
                    treatmentTextView.setText(illTreatment);

                    descriptionEditText.setText(illDescription);
                    preventionEditText.setText(illPrevention);
                    severityEditText.setText(illSeverity);
                    symptomEditText.setText(illSymptom);
                    treatmentEditText.setText(illTreatment);

                    // Now you can use the retrieved data as needed
                    // For example, you can display it in your UI or perform further processing
                } else {
                    // Document does not exist
                    Log.d(TAG, "No such document");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle errors
                Log.e(TAG, "Error fetching document: " + e.getMessage());
            }
        });

        mEditButton.setOnClickListener(v -> {
            // Show EditText fields and Save button, hide TextViews and Edit button

//            memberIDTextView.setVisibility(View.GONE);
            descriptionTextView.setVisibility(View.GONE);
            preventionTextView.setVisibility(View.GONE);
            severityTextView.setVisibility(View.GONE);
            symptomTextView.setVisibility(View.GONE);
            treatmentTextView.setVisibility(View.GONE);


            descriptionEditText.setVisibility(View.VISIBLE);
            preventionEditText.setVisibility(View.VISIBLE);
            severityEditText.setVisibility(View.VISIBLE);
            symptomEditText.setVisibility(View.VISIBLE);
            treatmentEditText.setVisibility(View.VISIBLE);

            mEditButton.setVisibility(View.GONE);
            mSaveButton.setVisibility(View.VISIBLE);

        });

        mSaveButton.setOnClickListener(v -> {
            // Update Firestore with new values

            String newDescription= descriptionEditText.getText().toString();
            String newPrevention = preventionEditText.getText().toString();
            String newSeverity = severityEditText.getText().toString();
            String newSymptom = symptomEditText.getText().toString();
            String newTreatment = treatmentEditText.getText().toString();

            DocumentReference illnessRef = fStore.collection("illnesses").document(health);
            illnessRef.update(
                            "description",newDescription,
                            "prevention", newPrevention,
                            "severity", newSeverity,
                            "symptom", newSymptom,
                            "treatment", newTreatment)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Illness updated successfully", Toast.LENGTH_SHORT).show();
                        // Hide EditText fields and Save button, show TextViews and Edit button

                        descriptionTextView.setText(newDescription);
                        preventionTextView.setText(newPrevention);
                        severityTextView.setText(newSeverity);
                        symptomTextView.setText(newSymptom);
                        treatmentTextView.setText(newTreatment);


                        descriptionTextView.setVisibility(View.VISIBLE);
                        preventionTextView.setVisibility(View.VISIBLE);
                        severityTextView.setVisibility(View.VISIBLE);
                        symptomTextView.setVisibility(View.VISIBLE);
                        treatmentTextView.setVisibility(View.VISIBLE);


                        descriptionEditText.setVisibility(View.GONE);
                        preventionEditText.setVisibility(View.GONE);
                        severityEditText.setVisibility(View.GONE);
                        symptomEditText.setVisibility(View.GONE);
                        treatmentEditText.setVisibility(View.GONE);

                        mEditButton.setVisibility(View.VISIBLE);
                        mSaveButton.setVisibility(View.GONE);

                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firestore Error", "Error updating document: " + e.getMessage());
                        Toast.makeText(getContext(), "Failed to update profile", Toast.LENGTH_SHORT).show();
                    });
        });

        // Adding OnClickListener to mPrint button
        mPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a PrintManager instance
//                PrintManager printManager = (PrintManager) requireContext().getSystemService(Context.PRINT_SERVICE);
//
//                // Set the print job name
//                String jobName = getString(R.string.app_name) + " Document";
//
//                // Start a print job
//                printManager.print(jobName, new PrintDocumentAdapter() {
//                    @Override
//                    public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle extras) {
//                        // Check if cancellation signal is set, if yes, cancel the printing
//                        if (cancellationSignal.isCanceled()) {
//                            callback.onLayoutCancelled();
//                            return;
//                        }
//
//                        // Create a print document info
//                        PrintDocumentInfo.Builder builder = new PrintDocumentInfo.Builder(jobName);
//                        builder.setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
//                                .setPageCount(1); // Set the page count to 1, as we're printing a single page
//
//                        PrintDocumentInfo info = builder.build();
//
//                        // Inform the system about the layout
//                        callback.onLayoutFinished(info, true);
//                    }
//
//                    @Override
//                    public void onWrite(PageRange[] pages, ParcelFileDescriptor destination, CancellationSignal cancellationSignal, WriteResultCallback callback) {
//                        // Check if cancellation signal is set, if yes, cancel the printing
//                        if (cancellationSignal.isCanceled()) {
//                            callback.onWriteCancelled();
//                            return;
//                        }
//
//                        // Start writing the document content to the output file
//                        // Create a new file output stream
//                        FileOutputStream outputStream = new FileOutputStream(destination.getFileDescriptor());
//                        PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream));
//
//                        // Write medication information to the output stream
//                        writer.println("Medication Name: " + med);
//                        writer.println("Description: " + medDescription);
//                        writer.println("Dosage: " + medDosage);
//                        writer.println("Frequency: " + medFrequency);
//
//                        // Close the writer
//                        writer.close();
//
//                        // Notify the system that writing is completed
//                        callback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});
//
//                    }
//                }, null);
                Toast.makeText(requireContext(), "Printing medication information", Toast.LENGTH_SHORT).show();
            }
        });






    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}