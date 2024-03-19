package com.csc131.deltamedicalteam.splash;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.csc131.deltamedicalteam.Login;
import com.csc131.deltamedicalteam.MainActivity;
import com.csc131.deltamedicalteam.R;
import com.google.firebase.auth.FirebaseAuth;

public class SplashMoving extends AppCompatActivity {


    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_moving);

        fAuth = FirebaseAuth.getInstance();

        // Start animations immediately
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.push_down_in);
        findViewById(R.id.logo).startAnimation(animation);

        Animation animation2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.push_up_in);
        findViewById(R.id.logo_big).startAnimation(animation2);

        // Start next activity after animations finish
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // Animation started
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Animation ended, start next activity
                if (fAuth.getCurrentUser() != null) {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                } else {
                    startActivity(new Intent(getApplicationContext(), Login.class));
                }
                finish(); // Close the current splash activity
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // Animation repeated
            }
        });
    }


}