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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.csc131.deltamedicalteam.R;
import com.csc131.deltamedicalteam.model.Medication;
import com.csc131.deltamedicalteam.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class ProfileCurrentMedicationFragment extends Fragment {
    FirebaseFirestore fStore;
    String medDescription, medDosage, medFrequency;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        com.csc131.deltamedicalteam.databinding.FragmentProfileCurrentMedicationBinding binding = com.csc131.deltamedicalteam.databinding.FragmentProfileCurrentMedicationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Retrieve the passed user information
        ProfileCurrentMedicationFragmentArgs args = ProfileCurrentMedicationFragmentArgs.fromBundle(getArguments());
        String med = args.getMedication();

        fStore = FirebaseFirestore.getInstance();

        // Display the user information
        TextView nameTextView = view.findViewById(R.id.profile_medicationName);
        TextView descriptionTextView = view.findViewById(R.id.textview_description);
        TextView dosageTextView = view.findViewById(R.id.textview_medicationDosage);
        TextView frequencyTextView = view.findViewById(R.id.textview_medicationFrequency);

        ImageButton mPrint = view.findViewById(R.id.imageButton_medicationPrint);


        DocumentReference MedRef = fStore.collection("medications").document(med);
        MedRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    // Access individual fields
                    medDescription = documentSnapshot.getString("description");
                    medDosage = documentSnapshot.getString("dosage");
                    medFrequency = documentSnapshot.getString("frequency");

                    // Set user information in TextViews
                    nameTextView.setText(med);
                    descriptionTextView.setText(medDescription);
                    dosageTextView.setText(medDosage);
                    frequencyTextView.setText(medFrequency);

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