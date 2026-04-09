package com.solve_bridge.app;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class PostProblemActivity extends AppCompatActivity {

    EditText etTitle, etDescription, etCategory;
    Button btnSubmit;

    FirebaseFirestore db;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_problem);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setTitle("");   // remove default title
        }

        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        etCategory = findViewById(R.id.etCategory);
        btnSubmit = findViewById(R.id.btnSubmit);
        ImageButton btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String title = etTitle.getText().toString().trim();
                String desc = etDescription.getText().toString().trim();
                String category = etCategory.getText().toString().trim();

                if(title.isEmpty() || desc.isEmpty() || category.isEmpty()){
                    Toast.makeText(PostProblemActivity.this, "All fields required", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (mAuth.getCurrentUser() == null) {
                    Toast.makeText(PostProblemActivity.this, "Please login to post", Toast.LENGTH_SHORT).show();
                    return;
                }

                String userId = mAuth.getCurrentUser().getUid();
                String userName = mAuth.getCurrentUser().getDisplayName();
                if (userName == null || userName.isEmpty()) userName = "User";

                Map<String, Object> post = new HashMap<>();
                post.put("userId", userId);
                post.put("user", userName);
                post.put("title", title);
                post.put("desc", desc);
                post.put("category", category);
                post.put("timestamp", System.currentTimeMillis());

                btnSubmit.setEnabled(false);

                db.collection("posts")
                        .add(post)
                        .addOnSuccessListener(documentReference -> {
                            // Update user's problem list in their profile
                            db.collection("Users").document(userId)
                                    .update("myProblems", FieldValue.arrayUnion(title))
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(PostProblemActivity.this, "Problem Posted Successfully", Toast.LENGTH_SHORT).show();
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(PostProblemActivity.this, "Problem posted but profile update failed", Toast.LENGTH_SHORT).show();
                                        finish();
                                    });
                        })
                        .addOnFailureListener(e -> {
                            btnSubmit.setEnabled(true);
                            Toast.makeText(PostProblemActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
