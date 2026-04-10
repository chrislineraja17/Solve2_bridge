package com.solve_bridge.app;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddSolutionActivity extends AppCompatActivity {

    EditText etSolution;
    Button btnSubmit;
    TextView tvProblemTitle;
    String problemId, problemTitle;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_solution);

        etSolution = findViewById(R.id.etSolution);
        btnSubmit = findViewById(R.id.btnSubmit);
        tvProblemTitle = findViewById(R.id.tvProblemTitle);

        db = FirebaseFirestore.getInstance();

        problemId = getIntent().getStringExtra("problemId");
        problemTitle = getIntent().getStringExtra("problemTitle");

        tvProblemTitle.setText(problemTitle);

        btnSubmit.setOnClickListener(v -> {
            String solutionText = etSolution.getText().toString().trim();

            if (solutionText.isEmpty()) {
                etSolution.setError("Solution cannot be empty");
                return;
            }

            saveSolution(solutionText);
        });

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }

    private void saveSolution(String solutionText) {
        String userId = FirebaseAuth.getInstance().getUid();
        String userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        if (userName == null || userName.isEmpty()) {
            userName = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        }

        Map<String, Object> solution = new HashMap<>();
        solution.put("problemId", problemId);
        solution.put("header", userName); // Saving as header for SolutionModel
        solution.put("content", solutionText); // Saving as content for SolutionModel
        solution.put("userId", userId);
        solution.put("likesCount", 0);
        solution.put("dislikesCount", 0);
        solution.put("likedBy", new java.util.ArrayList<String>());
        solution.put("dislikedBy", new java.util.ArrayList<String>());

        db.collection("solutions")
                .add(solution)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(AddSolutionActivity.this, "Solution Added!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(AddSolutionActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
