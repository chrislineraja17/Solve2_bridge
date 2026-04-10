package com.solve_bridge.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "HomeActivity";
    RecyclerView recyclerView;
    ArrayList<Post> list;
    ArrayList<Post> filteredList;
    PostAdapter adapter;

    SearchView searchView;
    ImageButton btnSearch, btnMenu;
    ChipGroup chipGroup;

    FirebaseFirestore db;
    FirebaseAuth mAuth;

    DrawerLayout drawerLayout;
    NavigationView navigationView;

    String currentCategory = "All";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Drawer Setup
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);

        updateNavHeader();

        // Views
        recyclerView = findViewById(R.id.recyclerView);
        searchView = findViewById(R.id.searchView);
        btnSearch = findViewById(R.id.btnSearch);
        btnMenu = findViewById(R.id.btnMenu);
        chipGroup = findViewById(R.id.chipGroup);

        FloatingActionButton fabPost = findViewById(R.id.fabPost);
        fabPost.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, PostProblemActivity.class));
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        filteredList = new ArrayList<>();

        adapter = new PostAdapter(this, filteredList);
        recyclerView.setAdapter(adapter);

        loadPosts();

        // CATEGORY CHIP LOGIC
        chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                currentCategory = "All";
            } else {
                Chip chip = findViewById(checkedIds.get(0));
                currentCategory = chip.getText().toString();
            }
            Log.d(TAG, "Category changed to: " + currentCategory);
            applyFilters(searchView.getQuery().toString());
        });

        // SEARCH LOGIC
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { return false; }

            @Override
            public boolean onQueryTextChange(String newText) {
                applyFilters(newText);
                return true;
            }
        });

        // SEARCH ICON
        btnSearch.setOnClickListener(v ->
                searchView.setVisibility(
                        searchView.getVisibility() == View.VISIBLE
                                ? View.GONE : View.VISIBLE
                ));

        // MENU BUTTON → OPEN DRAWER
        btnMenu.setOnClickListener(v ->
                drawerLayout.openDrawer(GravityCompat.START));
    }

    private void updateNavHeader() {
        View headerView = navigationView.getHeaderView(0);
        TextView tvUserEmail = headerView.findViewById(R.id.tvUserEmail);
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            tvUserEmail.setText(user.getEmail());
        }
    }

    private void applyFilters(String searchText) {
        filteredList.clear();
        String searchLower = searchText.toLowerCase().trim();
        String catLower = currentCategory.toLowerCase().trim();

        for (Post p : list) {
            // Search filter: check title and description
            boolean matchesSearch = searchLower.isEmpty() || 
                    (p.getTitle() != null && p.getTitle().toLowerCase().contains(searchLower)) ||
                    (p.getDescription() != null && p.getDescription().toLowerCase().contains(searchLower));

            // Category filter: support partial matches like "IoT" in "IoT/AI"
            boolean matchesCategory = catLower.equals("all") || 
                    (p.getCategory() != null && p.getCategory().toLowerCase().contains(catLower));

            if (matchesSearch && matchesCategory) {
                filteredList.add(p);
            }
        }
        
        Log.d(TAG, "Filter applied. Found " + filteredList.size() + " items.");
        adapter.notifyDataSetChanged();
    }

    private void loadPosts() {
        db.collection("posts")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e(TAG, "Load posts failed", error);
                        return;
                    }
                    if (value != null) {
                        list.clear();
                        for (QueryDocumentSnapshot doc : value) {
                            Post p = doc.toObject(Post.class);
                            p.setId(doc.getId());
                            list.add(p);
                        }
                        Log.d(TAG, "Loaded " + list.size() + " posts.");
                        applyFilters(searchView.getQuery().toString());
                    }
                });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_post_problem) {
            startActivity(new Intent(this, PostProblemActivity.class));
        } else if (id == R.id.nav_my_profile) {
            startActivity(new Intent(this, ProfileActivity.class));
        } else if (id == R.id.nav_my_problems) {
            startActivity(new Intent(this, MyProblemsActivity.class));
        } else if (id == R.id.nav_posted_solutions) {
            startActivity(new Intent(this, MySolutionsActivity.class));
        } else if (id == R.id.nav_logout) {
            logoutUser();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logoutUser() {
        mAuth.signOut();
        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
