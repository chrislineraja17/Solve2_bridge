package com.solve_bridge.app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Removed SplashScreen.installSplashScreen(this); to avoid system icon splash
        
        super.onCreate(savedInstanceState);
        // We set the background in the theme for a seamless transition,
        // but we can still keep the layout if you want to add animations later.
        setContentView(R.layout.splash_activity);

        // Delay for 2 seconds to show the splash screen
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                // User is signed in, go to HomeActivity
                startActivity(new Intent(SplashActivity.this, HomeActivity.class));
            } else {
                // No user is signed in, go to MainActivity (Login)
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            }
            finish();
        }, 2000);
    }
}
