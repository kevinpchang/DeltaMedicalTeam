package com.csc131.deltamedicalteam.ui.profile;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.csc131.deltamedicalteam.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class ProfileCurrentIllnessFragment extends Fragment {

    FirebaseFirestore fStore;
    String illDescription, illPrevention, illSeverity, illSymptom, illTreatment;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        com.csc131.deltamedicalteam.databinding.FragmentProfileCurrentIllnessBinding binding = com.csc131.deltamedicalteam.databinding.FragmentProfileCurrentIllnessBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Retrieve the passed user information
        ProfileCurrentIllnessFragmentArgs args = ProfileCurrentIllnessFragmentArgs.fromBundle(getArguments());
        String health = args.getHealthConditions();
        fStore = FirebaseFirestore.getInstance();

        // Display the user information
        TextView nameTextView = view.findViewById(R.id.profile_illnessName);
        TextView descriptionTextView = view.findViewById(R.id.textview_description);
        TextView preventionTextView = view.findViewById(R.id.textview_prevention);
        TextView severityTextView = view.findViewById(R.id.textview_severity);
        TextView symptomTextView = view.findViewById(R.id.textview_symptom);
        TextView treatmentTextView = view.findViewById(R.id.textview_treatment);

        ImageButton mPrint = view.findViewById(R.id.imageButton_illnessPrint);


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

        // Adding OnClickListener to mPrint button
//        mPrint.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                 Create a PrintManager instance
//                PrintManager printManager = (PrintManager) requireContext().getSystemService(Context.PRINT_SERVICE);
//
//                // Set the print job name
//                String jobName = "Illness Document";
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
//                        writer.println("Illness Name: " + health);
//                        writer.println("Description: " + illDescription);
//                        writer.println("Prevention: " + illPrevention);
//                        writer.println("Severity: " + illSeverity);
//                        writer.println("Symptom: " + illSymptom);
//                        writer.println("Treatment: " + illTreatment);
//
//                        // Close the writer
//                        writer.close();
//
//                        // Notify the system that writing is completed
//                        callback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});
//
//                    }
//                }, null);
//                Toast.makeText(requireContext(), "Printing medication information", Toast.LENGTH_SHORT).show();
//            }
//        });






    }





    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}