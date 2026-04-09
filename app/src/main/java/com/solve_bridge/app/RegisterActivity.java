package com.solve_bridge.app;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    EditText etName, etEmail, etPassword;
    CheckBox cbPoster, cbDeveloper, cbResearcher;
    Button btnRegister;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ImageView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        cbPoster = findViewById(R.id.cbPoster);
        cbDeveloper = findViewById(R.id.cbDeveloper);
        cbResearcher = findViewById(R.id.cbResearcher);
        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(view -> {
            String name = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            List<String> roles = new ArrayList<>();
            if (cbPoster.isChecked()) roles.add("Problem Poster");
            if (cbDeveloper.isChecked()) roles.add("Developer");
            if (cbResearcher.isChecked()) roles.add("Researcher");

            if (TextUtils.isEmpty(name)) {
                etName.setError("Name required");
                return;
            }
            if (TextUtils.isEmpty(email)) {
                etEmail.setError("Email required");
                return;
            }
            if (TextUtils.isEmpty(password)) {
                etPassword.setError("Password required");
                return;
            }
            if (roles.isEmpty()) {
                Toast.makeText(this, "Select at least one role", Toast.LENGTH_SHORT).show();
                return;
            }

            Log.d(TAG, "Attempting to register: " + email);
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                            Map<String, Object> user = new HashMap<>();
                            user.put("name", name);
                            user.put("email", email);
                            user.put("roles", roles);

                            db.collection("Users").document(userId)
                                    .set(user)
                                    .addOnSuccessListener(unused -> {
                                        Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e(TAG, "Firestore Error: " + e.getMessage());
                                        Toast.makeText(RegisterActivity.this, "Database Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                    });
                        } else {
                            String errorMessage = "Registration Failed";
                            if (task.getException() instanceof FirebaseAuthException) {
                                String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                                Log.e(TAG, "Firebase Auth Error Code: " + errorCode);
                                switch (errorCode) {
                                    case "ERROR_EMAIL_ALREADY_IN_USE":
                                        errorMessage = "This email is already registered.";
                                        break;
                                    case "ERROR_WEAK_PASSWORD":
                                        errorMessage = "Password is too weak.";
                                        break;
                                    case "ERROR_INVALID_EMAIL":
                                        errorMessage = "Invalid email format.";
                                        break;
                                    default:
                                        errorMessage = task.getException().getMessage();
                                }
                            } else if (task.getException() != null) {
                                errorMessage = task.getException().getMessage();
                            }
                            Log.e(TAG, "Registration error: " + errorMessage);
                            Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        }
                    });
        });
    }
}
