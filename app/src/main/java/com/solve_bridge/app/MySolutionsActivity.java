package com.solve_bridge.app;

import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class MySolutionsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<SolutionModel> list;
    SolutionAdapter adapter;
    ImageButton btnBack;

    FirebaseFirestore db;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_solutions);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.recyclerSolutions);
        btnBack = findViewById(R.id.btnBack);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();
        // In MySolutionsActivity, we don't need to show the 'Accept' button,
        // so we pass null for problemOwnerId.
        adapter = new SolutionAdapter(list, null);
        recyclerView.setAdapter(adapter);

        btnBack.setOnClickListener(v -> finish());

        loadMySolutions();
    }

    private void loadMySolutions() {
        if (mAuth.getCurrentUser() == null) return;

        String currentUserId = mAuth.getCurrentUser().getUid();

        db.collection("solutions")
                .whereEqualTo("userId", currentUserId)
                .addSnapshotListener((value, error) -> {
                    if (error != null) return;
                    if (value != null) {
                        list.clear();
                        for (QueryDocumentSnapshot doc : value) {
                            SolutionModel solution = doc.toObject(SolutionModel.class);
                            solution.setId(doc.getId());
                            list.add(solution);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }
}
