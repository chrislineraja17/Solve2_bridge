package com.solve_bridge.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    RecyclerView recyclerView;
    ArrayList<Post> list;
    ArrayList<Post> filteredList;
    PostAdapter adapter;

    SearchView searchView;
    ImageButton btnSearch, btnMenu, btnBack;

    FirebaseFirestore db;
    FirebaseAuth mAuth;

    DrawerLayout drawerLayout;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();

        // Drawer Setup
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);

        // Views
        recyclerView = findViewById(R.id.recyclerView);
        searchView = findViewById(R.id.searchView);
        btnSearch = findViewById(R.id.btnSearch);
        btnMenu = findViewById(R.id.btnMenu);
        btnBack = findViewById(R.id.btnBack);

        FloatingActionButton fabPost;

        fabPost = findViewById(R.id.fabPost);

        fabPost.setOnClickListener(v -> {

            Intent intent = new Intent(HomeActivity.this, PostProblemActivity.class);
            startActivity(intent);

        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        filteredList = new ArrayList<>();

        adapter = new PostAdapter(this, filteredList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        loadPosts();

        // SEARCH LOGIC
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { return false; }

            @Override
            public boolean onQueryTextChange(String newText) {

                filteredList.clear();

                if (newText.isEmpty()) {
                    filteredList.addAll(list);
                } else {
                    for (Post p : list) {
                        if (p.getTitle().toLowerCase()
                                .contains(newText.toLowerCase())) {
                            filteredList.add(p);
                        }
                    }
                }

                adapter.notifyDataSetChanged();
                return true;
            }
        });

        // SEARCH ICON
        btnSearch.setOnClickListener(v ->
                searchView.setVisibility(
                        searchView.getVisibility() == View.VISIBLE
                                ? View.GONE : View.VISIBLE
                ));

        // BACK BUTTON
        btnBack.setOnClickListener(v -> finish());

        // MENU BUTTON → OPEN DRAWER
        btnMenu.setOnClickListener(v ->
                drawerLayout.openDrawer(GravityCompat.START));
    }

    private void loadPosts() {
        db.collection("posts")
                .get()
                .addOnSuccessListener(snap -> {

                    list.clear();
                    filteredList.clear();

                    for (QueryDocumentSnapshot doc : snap) {
                        Post p = doc.toObject(Post.class);
                        list.add(p);
                    }

                    filteredList.addAll(list);
                    adapter.notifyDataSetChanged();
                });
    }

    // Navigation Drawer Click Handling
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_post_problem) {
            startActivity(new Intent(this, PostProblemActivity.class));
        }
        else if (id == R.id.nav_my_profile) {
            startActivity(new Intent(this, ProfileActivity.class));
        }
        else if (id == R.id.nav_my_problems) {
            startActivity(new Intent(this, MyProblemsActivity.class));
        }
        else if (id == R.id.nav_posted_solutions) {
            startActivity(new Intent(this, MySolutionsActivity.class));
        }
        else if (id == R.id.nav_logout) {
            logoutUser();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logoutUser() {
        // Sign out from Firebase
        mAuth.signOut();

        // Go to Login page
        Intent intent = new Intent(HomeActivity.this, MainActivity.class);

        // Clear activity stack
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
        finish();
    }

}
