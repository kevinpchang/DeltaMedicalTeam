package com.csc131.deltamedicalteam;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.balysv.materialripple.MaterialRippleLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    EditText mFullname, mEmail, mPassword, mRetypePassword, mPhone, mCode;
    MaterialRippleLayout mRegisterBtn;
    TextView mLoginBtn;
    FirebaseAuth fAuth;
    ProgressBar progressBar;
    FirebaseFirestore fstore;
    String userID,mPermission;

    Boolean isCodeValid = false;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFullname = findViewById(R.id.fullName);
        mCode = findViewById(R.id.registerCode);
        mPhone = findViewById(R.id.phone);

        mEmail = findViewById(R.id.fieldEmail);
        mPassword = findViewById(R.id.password);
        mPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        mRetypePassword = findViewById(R.id.password2);
        mRetypePassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        mRegisterBtn = findViewById(R.id.bt_create_account);
        mLoginBtn = findViewById(R.id.sign_in);

        fAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();

        progressBar = findViewById(R.id.progressBar);





        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                String password2 = mRetypePassword.getText().toString().trim();

                String fullName = mFullname.getText().toString();
                String phone = mPhone.getText().toString();
                String code = mCode.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Email is Required.");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    mPassword.setError("Password is Required.");
                    return;
                } else {
                    if (TextUtils.isEmpty(password2)) {
                        mRetypePassword.setError("Retype Password is Required.");
                        return;
                    } else {
                        if (!password2.equals(password)) {
                            mRetypePassword.setError("Please ensure you've retyped the same password.");
                            return;
                        }
                    }
                }

                // Get database RegisterCode
                DocumentReference documentReference = fstore.collection("RegisterCode").document(code);
                documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // Document exists, handle it

                                isCodeValid = true;
                                mPermission = documentSnapshot.getString("permission");
                                registerUser(email, password, fullName, phone, mPermission, code);
                                // Update isUsed field to true


                        } else {
                            // Document does not exist, handle it
                            mCode.setError("Register Code is not Valid");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle the failure to fetch document
                        Log.e("Firestore Error", "Error fetching document: " + e.getMessage());
                    }
                });
            }
        });




        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });
    }

    // Define method for registering the user
    private void registerUser(String email, String password, String fullName, String phone, String permission, String code) {
        progressBar.setVisibility(View.VISIBLE);

        // Register the user in Firebase
        fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(Register.this, "User Created.", Toast.LENGTH_SHORT).show();

                    //send verify email

                    FirebaseUser fuser = fAuth.getCurrentUser();
                    fuser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(Register.this, "Verification Email has been Sent.", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: Email not Sent " +e.getMessage());
                        }
                    });


                    //get UserUID
                    userID = fAuth.getCurrentUser().getUid();
                    //create user db base on USERUID
                    DocumentReference documentReference = fstore.collection("users").document(userID);
                    Map<String, Object> user = new HashMap<>();
                    user.put("fName", fullName);
                    user.put("email", email);
                    user.put("phone", phone);
                    user.put("permission", permission);
                    user.put("registercode", code);
                    documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d(TAG, "onSuccessL user Profile is created for " + userID);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: " + e.toString());
                        }
                    });

                    //update RegisterCode isUsed = True
                    DocumentReference documentReference2 = fstore.collection("RegisterCode").document(code);
                    documentReference2.delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("Firestore", "Document deleted successfully");
                                    // Proceed with registration process after code validation
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e("Firestore Error", "Error deleting document: " + e.getMessage());
                                }
                            });

                    // Go to main page
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                } else {
                    Toast.makeText(Register.this, "Error !" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}


