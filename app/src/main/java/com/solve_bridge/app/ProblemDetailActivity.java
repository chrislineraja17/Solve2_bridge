package com.solve_bridge.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class ProblemDetailActivity extends AppCompatActivity {

    TextView tvTitle, tvDescription, tvCategory, tvLikes, tvDislikes;
    ImageView btnLike, btnDislike;
    Button btnAddSolution;

    RecyclerView recyclerSolutions;

    FirebaseFirestore db;
    String currentUserId;

    ArrayList<SolutionModel> solutionList;
    SolutionAdapter adapter;

    String problemId, problemTitle, problemOwnerId;
    Post currentPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problem_detail);

        db = FirebaseFirestore.getInstance();
        currentUserId = FirebaseAuth.getInstance().getUid();

        tvTitle = findViewById(R.id.tvTitle);
        tvDescription = findViewById(R.id.tvDescription);
        tvCategory = findViewById(R.id.tvCategory);
        tvLikes = findViewById(R.id.tvLikes);
        tvDislikes = findViewById(R.id.tvDislikes);
        btnLike = findViewById(R.id.btnLike);
        btnDislike = findViewById(R.id.btnDislike);
        btnAddSolution = findViewById(R.id.btnAddSolution);

        recyclerSolutions = findViewById(R.id.recyclerSolutions);

        // Receive problem details from previous activity
        problemId = getIntent().getStringExtra("problemId");
        problemTitle = getIntent().getStringExtra("title");
        String description = getIntent().getStringExtra("description");
        String category = getIntent().getStringExtra("category");

        tvTitle.setText(problemTitle);
        tvDescription.setText(description);
        tvCategory.setText(category);

        loadProblemData();

        btnLike.setOnClickListener(v -> handleLike());
        btnDislike.setOnClickListener(v -> handleDislike());

        // Open Add Solution Page
        btnAddSolution.setOnClickListener(v -> {
            Intent intent = new Intent(ProblemDetailActivity.this, AddSolutionActivity.class);
            intent.putExtra("problemId", problemId);
            intent.putExtra("problemTitle", problemTitle); 
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

    private void loadProblemData() {
        db.collection("posts").document(problemId)
                .addSnapshotListener((value, error) -> {
                    if (error != null || value == null) return;
                    currentPost = value.toObject(Post.class);
                    if (currentPost != null) {
                        currentPost.setId(value.getId());
                        problemOwnerId = currentPost.getUserId();
                        updateLikeUI();
                        
                        // Initialize adapter once we have the problemOwnerId
                        if (adapter == null) {
                            solutionList = new ArrayList<>();
                            adapter = new SolutionAdapter(solutionList, problemOwnerId);
                            recyclerSolutions.setLayoutManager(new LinearLayoutManager(this));
                            recyclerSolutions.setAdapter(adapter);
                            loadSolutions();
                        }
                    }
                });
    }

    private void updateLikeUI() {
        tvLikes.setText(String.valueOf(currentPost.getLikesCount()));
        tvDislikes.setText(String.valueOf(currentPost.getDislikesCount()));

        if (currentUserId != null) {
            if (currentPost.getLikedBy().contains(currentUserId)) {
                btnLike.setColorFilter(getColor(R.color.colorPrimary));
            } else {
                btnLike.setColorFilter(getColor(R.color.textSecondary));
            }

            if (currentPost.getDislikedBy().contains(currentUserId)) {
                btnDislike.setColorFilter(getColor(R.color.colorPrimary));
            } else {
                btnDislike.setColorFilter(getColor(R.color.textSecondary));
            }
        }
    }

    private void handleLike() {
        if (currentUserId == null) return;
        if (currentPost.getLikedBy().contains(currentUserId)) {
            db.collection("posts").document(problemId)
                    .update("likesCount", FieldValue.increment(-1),
                            "likedBy", FieldValue.arrayRemove(currentUserId));
        } else {
            db.collection("posts").document(problemId)
                    .update("likesCount", FieldValue.increment(1),
                            "likedBy", FieldValue.arrayUnion(currentUserId));
            if (currentPost.getDislikedBy().contains(currentUserId)) {
                db.collection("posts").document(problemId)
                        .update("dislikesCount", FieldValue.increment(-1),
                                "dislikedBy", FieldValue.arrayRemove(currentUserId));
            }
        }
    }

    private void handleDislike() {
        if (currentUserId == null) return;
        if (currentPost.getDislikedBy().contains(currentUserId)) {
            db.collection("posts").document(problemId)
                    .update("dislikesCount", FieldValue.increment(-1),
                            "dislikedBy", FieldValue.arrayRemove(currentUserId));
        } else {
            db.collection("posts").document(problemId)
                    .update("dislikesCount", FieldValue.increment(1),
                            "dislikedBy", FieldValue.arrayUnion(currentUserId));
            if (currentPost.getLikedBy().contains(currentUserId)) {
                db.collection("posts").document(problemId)
                        .update("likesCount", FieldValue.increment(-1),
                                "likedBy", FieldValue.arrayRemove(currentUserId));
            }
        }
    }

    private void loadSolutions() {
        db.collection("solutions")
                .whereEqualTo("problemId", problemId)
                .addSnapshotListener((value, error) -> {
                    if (error != null || value == null) return;
                    solutionList.clear();
                    for (QueryDocumentSnapshot doc : value) {
                        SolutionModel solution = doc.toObject(SolutionModel.class);
                        solution.setId(doc.getId());
                        solutionList.add(solution);
                    }
                    adapter.notifyDataSetChanged();
                });
    }
}
