package com.solve_bridge.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    ImageButton btnBack;
    Button btnLogout;
    TextView tvName, tvRole;
    RecyclerView recyclerProblems, recyclerSolutions;
    ProfileListAdapter problemsAdapter, solutionsAdapter;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnBack = findViewById(R.id.btnBack);
        btnLogout = findViewById(R.id.btnLogout);
        tvName = findViewById(R.id.tvName);
        tvRole = findViewById(R.id.tvRole);
        recyclerProblems = findViewById(R.id.recyclerProblems);
        recyclerSolutions = findViewById(R.id.recyclerSolutions);

        btnBack.setOnClickListener(v -> finish());
        btnLogout.setOnClickListener(v -> logoutUser());

        setupRecyclerViews();
        loadUserProfile();
    }

    private void setupRecyclerViews() {
        problemsAdapter = new ProfileListAdapter(new ArrayList<>());
        recyclerProblems.setLayoutManager(new LinearLayoutManager(this));
        recyclerProblems.setAdapter(problemsAdapter);

        solutionsAdapter = new ProfileListAdapter(new ArrayList<>());
        recyclerSolutions.setLayoutManager(new LinearLayoutManager(this));
        recyclerSolutions.setAdapter(solutionsAdapter);
    }

    private void loadUserProfile() {
        if (mAuth.getCurrentUser() == null) return;

        String userId = mAuth.getCurrentUser().getUid();

        db.collection("Users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("name");
                        List<String> roles = (List<String>) documentSnapshot.get("roles");
                        List<String> myProblems = (List<String>) documentSnapshot.get("myProblems");
                        List<String> mySolutions = (List<String>) documentSnapshot.get("mySolutions");

                        tvName.setText(name != null ? name : "User");
                        
                        if (roles != null && !roles.isEmpty()) {
                            tvRole.setText("Roles: " + String.join(", ", roles));
                        } else {
                            tvRole.setText("No roles assigned");
                        }

                        if (myProblems != null) {
                            problemsAdapter.updateItems(myProblems);
                        }
                        
                        if (mySolutions != null) {
                            solutionsAdapter.updateItems(mySolutions);
                        }
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(ProfileActivity.this, "Error loading profile", Toast.LENGTH_SHORT).show());
    }

    private void logoutUser() {
        mAuth.signOut();
        Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}