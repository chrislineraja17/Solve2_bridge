package com.solve_bridge.app;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class PostProblemActivity extends AppCompatActivity {

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
            String desc = etDescription.getText().toString().trim();
            String category = etCategory.getText().toString().trim();

            if (title.isEmpty() || desc.isEmpty() || category.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            postProblem(title, desc, category);
        });

        btnBack.setOnClickListener(v -> finish());
    }

    private void postProblem(String title, String desc, String category) {
        String userId = FirebaseAuth.getInstance().getUid();
        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        Map<String, Object> problem = new HashMap<>();
        problem.put("title", title);
        problem.put("desc", desc);
        problem.put("category", category);
        problem.put("user", userEmail);
        problem.put("userId", userId);
        problem.put("likesCount", 0);
        problem.put("dislikesCount", 0);
        problem.put("likedBy", new java.util.ArrayList<String>());
        problem.put("dislikedBy", new java.util.ArrayList<String>());

        // Changed collection to 'posts' to match your database structure
        db.collection("posts")
                .add(problem)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Problem Posted!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
