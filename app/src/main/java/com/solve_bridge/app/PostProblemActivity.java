package com.solve_bridge.app;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostProblemActivity extends AppCompatActivity {

    private static final String TAG = "PostProblemActivity";
    EditText etTitle, etDescription, etCategory;
    Button btnSubmit;
    ImageButton btnBack;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_problem);

        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        etCategory = findViewById(R.id.etCategory);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnBack = findViewById(R.id.btnBack);

        db = FirebaseFirestore.getInstance();

        btnSubmit.setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String description = etDescription.getText().toString().trim();
            String category = etCategory.getText().toString().trim();

            if (title.isEmpty() || description.isEmpty() || category.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            fetchUserRoleAndPost(title, description, category);
        });

        btnBack.setOnClickListener(v -> finish());
    }

    private void fetchUserRoleAndPost(String title, String description, String category) {
        String userId = FirebaseAuth.getInstance().getUid();
        if (userId == null) return;

        db.collection("Users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    String role = "User";
                    if (documentSnapshot.exists()) {
                        List<String> roles = (List<String>) documentSnapshot.get("roles");
                        if (roles != null && !roles.isEmpty()) {
                            role = roles.get(0); // Take the primary role
                        }
                    }
                    postProblem(title, description, category, userId, role);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching user role", e);
                    postProblem(title, description, category, userId, "User");
                });
    }

    private void postProblem(String title, String description, String category, String userId, String role) {
        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        Map<String, Object> problem = new HashMap<>();
        problem.put("title", title);
        problem.put("description", description); // Changed from "desc" to "description"
        problem.put("category", category);
        problem.put("user", userEmail);
        problem.put("userId", userId);
        problem.put("userRole", role);
        problem.put("likesCount", 0);
        problem.put("dislikesCount", 0);
        problem.put("likedBy", new java.util.ArrayList<String>());
        problem.put("dislikedBy", new java.util.ArrayList<String>());
        problem.put("timestamp", com.google.firebase.firestore.FieldValue.serverTimestamp());

        db.collection("posts")
                .add(problem)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Problem Posted!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
