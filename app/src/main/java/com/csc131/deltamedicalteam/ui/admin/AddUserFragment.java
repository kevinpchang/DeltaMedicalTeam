package com.csc131.deltamedicalteam.ui.admin;
import com.balysv.materialripple.MaterialRippleLayout;
import com.csc131.deltamedicalteam.R;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AddUserFragment extends Fragment {

    private EditText mFname, mLname, mEmail, mPhone;
    private Spinner mPermission;
    private MaterialRippleLayout mRegisterBtn;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fstore;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_adduser, container, false);

        // Prepare the data source (list of suggestions)
        String[] items  = {"Doctor", "Nurse"};

        // Create the adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mPermission = rootView.findViewById(R.id.spinner_permission);
        mPermission.setAdapter(adapter);


        mFname = rootView.findViewById(R.id.add_user_fName);
        mLname = rootView.findViewById(R.id.add_user_lName);
        mPhone = rootView.findViewById(R.id.phone);
        mEmail = rootView.findViewById(R.id.add_user_address);
        mRegisterBtn = rootView.findViewById(R.id.bt_create_account);
        progressBar = rootView.findViewById(R.id.progressBar);

        // Initialize Firebase
        fAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle registration logic
                String email = mEmail.getText().toString().trim();

                String fName = mFname.getText().toString();
                String lName = mLname.getText().toString();
                String phone = mPhone.getText().toString();
                String permission = (String) mPermission.getSelectedItem();

                if (!isValidEmail(email)) {
                    mEmail.setError("Invalid email format");
                    return;
                }
                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Email is Required.");
                    return;
                }

                if (TextUtils.isEmpty(fName)) {
                    mFname.setError("First name is Required.");
                    return;
                } else if (fName.length() < 3) {
                    mFname.setError("First name must be at least 3 chars.");
                    return;
                }

                if (TextUtils.isEmpty(lName)) {
                    mLname.setError("Last name is Required.");
                    return;
                } else if (lName.length() < 3) {
                    mLname.setError("Last name must be at least 3 chars.");
                    return;
                }

                if (TextUtils.isEmpty(phone)) {
                    mPhone.setError("Phone is Required.");
                    return;
                } else if (phone.length() != 10) {
                    mPhone.setError("Phone number must be 10 digits.");
                    return;
                }



                progressBar.setVisibility(View.VISIBLE);
                String password = setPassword(fName, lName, phone);
                // Register the user in Firebase
                fAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // User registration successful
                                    Toast.makeText(requireContext(), "User Created.", Toast.LENGTH_SHORT).show();
                                    sendEmailVerification();
                                    saveUserData(fName, lName, email, phone, permission);
                                } else {
                                    // User registration failed
                                    Toast.makeText(requireContext(), "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                                progressBar.setVisibility(View.GONE);
                            }
                        });
            }
        });


        return rootView;
    }




    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    public static String setPassword(String firstName,String lastName, String phoneNumber) {
        // Extract first name and last name

            // Extract last 4 digits of phone number
            String lastFourDigits = phoneNumber.substring(phoneNumber.length() - 4);

            // Generate password

        return firstName.substring(0, 2) + lastName.substring(0, 2) + lastFourDigits;

    }

    private void sendEmailVerification() {
        FirebaseUser fUser = fAuth.getCurrentUser();
        if (fUser != null) {
            fUser.sendEmailVerification()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(requireContext(), "Verification Email has been Sent.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(requireContext(), "Email not Sent: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void saveUserData(String fName, String lName, String email, String phone, String permission) {
        FirebaseUser user = fAuth.getCurrentUser();
        if (user != null) {
            String userID = user.getUid();
            DocumentReference documentReference = fstore.collection("users").document(userID);
            Map<String, Object> userData = new HashMap<>();
            userData.put("fName", fName);
            userData.put("lName", lName);
            userData.put("email", email);
            userData.put("phone", phone);
            userData.put("permission", permission);
            userData.put("address", "N/A");
            userData.put("location", "N/A");
            documentReference.set(userData)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            // User data saved successfully
                            Toast.makeText(requireContext(), "User Profile created for " + userID, Toast.LENGTH_SHORT).show();
                            fAuth.signInWithEmailAndPassword("admin@csc131.delta", "123456");
                            Navigation.findNavController(requireView()).navigate(R.id.userManagerFragment);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Failed to save user data
                            Toast.makeText(requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}


