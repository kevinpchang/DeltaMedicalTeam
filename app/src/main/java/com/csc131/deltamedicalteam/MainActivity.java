package com.csc131.deltamedicalteam;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.content.Intent;
import android.widget.TextView;

import com.csc131.deltamedicalteam.ui.appointment.AddAppointmentFragment;
import com.csc131.deltamedicalteam.utils.Tools;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.csc131.deltamedicalteam.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

     TextView fullNameTextView;
     TextView permissionTextView;
     TextView emailTextView, verifyMsg;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        userID = fAuth.getCurrentUser().getUid();



        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .setAnchorView(R.id.fab).show();
            }
        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        // Determine the menu items to display based on user's permissions
        Menu navMenu = navigationView.getMenu();
        MenuItem userManagerItem = navMenu.findItem(R.id.userManagerFragment);

        Tools.checkAdminPermission(new PermissionCallback() {
            @Override
            public void onPermissionCheck(boolean isAdmin) {
                    Log.e(TAG, "isAdmin: " + isAdmin );
                    if (!isAdmin) {
                        // Remove the userManagerFragment item from the menu
                        userManagerItem.setVisible(false);
                        Log.e(TAG, "userManagerFragment is hidden in menu");

                    }
            }
        });




        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.patientManagerFragment, R.id.nav_appointment, R.id.userManagerFragment, R.id.nav_lab_report, R.id.nav_medication, R.id.nav_health_condition, R.id.nav_logout)
                .setOpenableLayout(drawer)
                .build();


        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_logout) {
                    // Other menu items handling

                    // Handle logout item click
                    // For example, you can start a logout activity or clear user session
                    // Once the logout process is complete, navigate to the login screen or home screen
                    // Example:
                    // startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    fAuth.signOut();
                    startActivity(new Intent(getApplicationContext(), Login.class));
                    return true; // Return true to indicate that the item is handled

                } else if(item.getItemId() == R.id.nav_home) {
                    //Take you to the next screen
                    NavController navControllerHome = Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment_content_main);
                    navControllerHome.navigate(R.id.nav_home);
                    drawer.closeDrawer(GravityCompat.START);
                    return true;


                } else if(item.getItemId() == R.id.nav_appointment) {
                    // Navigate to the "nav_appointment" destination
                    NavController navControllerAppointment = Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment_content_main);
                    navControllerAppointment.navigate(R.id.nav_appointment);
                    drawer.closeDrawer(GravityCompat.START);
                    return true;
                } else if(item.getItemId() == R.id.nav_health_condition) {
                    // Navigate to the "nav_appointment" destination
                    NavController navControllerAppointment = Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment_content_main);
                    navControllerAppointment.navigate(R.id.nav_health_condition);
                    drawer.closeDrawer(GravityCompat.START);
                    return true;
                } else if(item.getItemId() == R.id.nav_lab_report) {
                    // Navigate to the "nav_appointment" destination
                    NavController navControllerAppointment = Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment_content_main);
                    navControllerAppointment.navigate(R.id.nav_lab_report);
                    drawer.closeDrawer(GravityCompat.START);
                    return true;
                } else if(item.getItemId() == R.id.nav_medication) {
                    // Navigate to the "nav_appointment" destination
                    NavController navControllerAppointment = Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment_content_main);
                    navControllerAppointment.navigate(R.id.nav_medication);
                    drawer.closeDrawer(GravityCompat.START);
                    return true;

                } else if(item.getItemId() == R.id.userManagerFragment) {
                    // Navigate to the "nav_appointment" destination
                    NavController navControllerAppointment = Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment_content_main);
                    navControllerAppointment.navigate(R.id.userManagerFragment);
                    drawer.closeDrawer(GravityCompat.START);
                    return true;

                } else if(item.getItemId() == R.id.patientManagerFragment) {
                    // Navigate to the "nav_appointment" destination
                    NavController navControllerAppointment = Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment_content_main);
                    navControllerAppointment.navigate(R.id.patientManagerFragment);
                    drawer.closeDrawer(GravityCompat.START);
                    return true;

                }
                return false; // Return false to indicate that the item is not handled here

            }
        });

        View headerView = navigationView.getHeaderView(0);
        fullNameTextView = headerView.findViewById(R.id.add_patient_fName);
        permissionTextView = headerView.findViewById(R.id.permission);
        emailTextView = headerView.findViewById(R.id.profileEmail);
        verifyMsg = headerView.findViewById(R.id.notverifymessage);

        FirebaseUser fuser = fAuth.getCurrentUser();

        if(!fuser.isEmailVerified()) {
            verifyMsg.setVisibility(View.VISIBLE);
        }

// Call method to retrieve user's information from database
        DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    // Handle any errors that occur during the retrieval of the document
                    Log.e("Firestore Error", "Error fetching user document: " + error.getMessage());
                    return;
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    // Document exists, retrieve and set the text of TextViews with user's information
                    String fullName = documentSnapshot.getString("fName");
                    String email = documentSnapshot.getString("email");
                    String permission = documentSnapshot.getString("permission");

                    if (fullName != null) {
                        fullNameTextView.setText(fullName);
                    } else {
                        Log.e("Firestore Error", "Full name is null");
                        fullNameTextView.setText("Click to setup Profile");
                    }

                    // Similarly, handle other fields like email, permission, etc.
                    // Make sure to handle null cases for other fields as well

                    if (email != null) {
                        emailTextView.setText(email);
                    } else {
                        Log.e("Firestore Error", "Email is null");
                        emailTextView.setText("Click to setup Email");
                    }

                    if (permission != null) {
                        permissionTextView.setText(permission);
                    } else {
                        Log.e("Firestore Error", "Email is null");
                        permissionTextView.setText("Contact admin for permission");
                    }

                } else {
                    // Document does not exist or snapshot is null
                    Log.e("Firestore Error", "User document does not exist or snapshot is null");
                }
            }
        });



        // Set an OnClickListener on the view
        headerView.findViewById(R.id.header_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle clicks on the profileTextView
                // Navigate to the profile fragment, assuming its ID is nav_profile

                NavController navControllerProfile = Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment_content_main);
                navControllerProfile.navigate(R.id.nav_profile);
               // startActivity(new Intent(getApplicationContext(), ProfilePurple.class));
                drawer.closeDrawer(GravityCompat.START);
            }
        });


    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    // Method to check if the user has admin permission
//    public void checkAdminPermission(final PermissionCallback callback) {
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        if (user != null) {
//            FirebaseFirestore db = FirebaseFirestore.getInstance();
//            DocumentReference userRef = db.collection("users").document(user.getUid());
//            userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                    if (task.isSuccessful()) {
//                        DocumentSnapshot document = task.getResult();
//                        if (document.exists()) {
//                            String permission = document.getString("permission");
//                            Log.e(TAG, "db check permission = " + permission);
//                            if ("admin".equalsIgnoreCase(permission)) {
//                                callback.onPermissionCheck(true);
//                            } else {
//                                callback.onPermissionCheck(false);
//                            }
//                        } else {
//                            // User data does not exist
//                            callback.onPermissionCheck(false);
//                            Log.e(TAG, "db not exist");
//                        }
//                    } else {
//                        // Error getting document
//                        callback.onPermissionCheck(false);
//                        Log.e(TAG, "Error getting document: ", task.getException());
//                    }
//                }
//            });
//        } else {
//            // User is not authenticated
//            callback.onPermissionCheck(false);
//            Log.e(TAG, "user not auth");
//        }
//    }

    public interface PermissionCallback {
        void onPermissionCheck(boolean isAdmin);
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


}