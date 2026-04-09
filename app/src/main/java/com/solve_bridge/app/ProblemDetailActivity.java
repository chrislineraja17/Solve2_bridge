package com.solve_bridge.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class ProblemDetailActivity extends AppCompatActivity {

    TextView tvTitle, tvDescription, tvCategory;
    Button btnAddSolution;

    RecyclerView recyclerSolutions;

    FirebaseFirestore db;

    ArrayList<SolutionModel> solutionList;
    SolutionAdapter adapter;

    String problemId, problemTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problem_detail);

        tvTitle = findViewById(R.id.tvTitle);
        tvDescription = findViewById(R.id.tvDescription);
        tvCategory = findViewById(R.id.tvCategory);
        btnAddSolution = findViewById(R.id.btnAddSolution);

        recyclerSolutions = findViewById(R.id.recyclerSolutions);

        db = FirebaseFirestore.getInstance();

        solutionList = new ArrayList<>();
        adapter = new SolutionAdapter(solutionList);

        recyclerSolutions.setLayoutManager(new LinearLayoutManager(this));
        recyclerSolutions.setAdapter(adapter);

        // Receive problem details from previous activity
        problemId = getIntent().getStringExtra("problemId");
        problemTitle = getIntent().getStringExtra("title");
        String description = getIntent().getStringExtra("description");
        String category = getIntent().getStringExtra("category");

        tvTitle.setText(problemTitle);
        tvDescription.setText(description);
        tvCategory.setText(category);

        loadSolutions();

        // Open Add Solution Page
        btnAddSolution.setOnClickListener(v -> {
            Intent intent = new Intent(ProblemDetailActivity.this, AddSolutionActivity.class);
            intent.putExtra("problemId", problemId);
            intent.putExtra("problemTitle", problemTitle); // Pass title to AddSolutionActivity
            startActivity(intent);
        });

        // Toolbar setup
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
        }

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadSolutions() {
        db.collection("solutions")
                .whereEqualTo("problemId", problemId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    solutionList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String solutionText = doc.getString("solutionText");
                        String userName = doc.getString("userName");
                        if (userName == null) {
                            userName = "Anonymous";
                        }
                        solutionList.add(new SolutionModel(userName, solutionText));
                    }
                    adapter.notifyDataSetChanged();
                });
    }
}
