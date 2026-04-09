package com.solve_bridge.app;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MySolutionsActivity extends AppCompatActivity {

    private static final String TAG = "MySolutionsActivity";
    RecyclerView recyclerSolutions;
    SolutionAdapter adapter;
    List<SolutionModel> solutionList;
    FirebaseFirestore db;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_solutions);

        recyclerSolutions = findViewById(R.id.recyclerSolutions);
        ImageButton btnBack = findViewById(R.id.btnBack);

        solutionList = new ArrayList<>();
        adapter = new SolutionAdapter(solutionList);

        recyclerSolutions.setLayoutManager(new LinearLayoutManager(this));
        recyclerSolutions.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            loadSolutions();
        } else {
            Toast.makeText(this, "Please login to see your solutions", Toast.LENGTH_SHORT).show();
        }

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
    }

    private void loadSolutions() {
        String userId = mAuth.getCurrentUser().getUid();

        db.collection("solutions")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    solutionList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String solutionText = doc.getString("solutionText");
                        String problemTitle = doc.getString("problemTitle");
                        String problemId = doc.getString("problemId");
                        
                        // Try alternative key if problemTitle is null
                        if (problemTitle == null) {
                            problemTitle = doc.getString("title");
                        }

                        final SolutionModel solutionModel = new SolutionModel(
                                problemTitle != null ? problemTitle : "Loading problem details...",
                                solutionText != null ? solutionText : ""
                        );
                        solutionList.add(solutionModel);

                        // Fetch from 'posts' collection to get the most up-to-date title and category
                        if (problemId != null) {
                            db.collection("posts").document(problemId).get()
                                    .addOnSuccessListener(postDoc -> {
                                        if (postDoc.exists()) {
                                            String fetchedTitle = postDoc.getString("title");
                                            String category = postDoc.getString("category");
                                            
                                            String displayHeader = fetchedTitle != null ? fetchedTitle : "Untitled Problem";
                                            if (category != null && !category.isEmpty()) {
                                                displayHeader += " (" + category + ")";
                                            }
                                            
                                            solutionModel.setHeader(displayHeader);
                                        } else {
                                            if (solutionModel.getHeader().equals("Loading problem details...")) {
                                                solutionModel.setHeader("Unknown Problem (Deleted)");
                                            }
                                        }
                                        adapter.notifyDataSetChanged();
                                    })
                                    .addOnFailureListener(e -> {
                                        if (solutionModel.getHeader().equals("Loading problem details...")) {
                                            solutionModel.setHeader("Unknown Problem");
                                            adapter.notifyDataSetChanged();
                                        }
                                    });
                        } else if (problemTitle == null) {
                            solutionModel.setHeader("Unknown Problem");
                        }
                    }

                    if (solutionList.isEmpty()) {
                        Toast.makeText(MySolutionsActivity.this, "No solutions posted yet.", Toast.LENGTH_SHORT).show();
                    }
                    
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to load solutions", e);
                    Toast.makeText(MySolutionsActivity.this, "Error loading solutions", Toast.LENGTH_SHORT).show();
                });
    }
}
