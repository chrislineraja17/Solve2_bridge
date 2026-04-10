package com.solve_bridge.app;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private ArrayList<Post> list;
    private Context context;
    private FirebaseFirestore db;

    public PostAdapter(Context context, ArrayList<Post> list) {
        this.context = context;
        this.list = list;
        this.db = FirebaseFirestore.getInstance();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView user, title, desc, likes, dislikes, categoryTag, userRole;
        ImageView btnLike, btnDislike;

        public ViewHolder(View itemView) {
            super(itemView);
            user = itemView.findViewById(R.id.tvUser);
            title = itemView.findViewById(R.id.tvTitle);
            desc = itemView.findViewById(R.id.tvDesc);
            likes = itemView.findViewById(R.id.tvLikes);
            dislikes = itemView.findViewById(R.id.tvDislikes);
            btnLike = itemView.findViewById(R.id.btnLike);
            btnDislike = itemView.findViewById(R.id.btnDislike);
            categoryTag = itemView.findViewById(R.id.tvCategoryTag);
            userRole = itemView.findViewById(R.id.tvUserRole);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Post post = list.get(position);
        String currentUserId = FirebaseAuth.getInstance().getUid();

        holder.user.setText(post.getUser());
        holder.title.setText(post.getTitle());
        holder.desc.setText(post.getDesc());
        holder.likes.setText(String.valueOf(post.getLikesCount()));
        holder.dislikes.setText(String.valueOf(post.getDislikesCount()));
        holder.categoryTag.setText(post.getCategory());

        // Dynamic role loading: Try the saved role first, then fetch from Users collection
        if (post.getUserRole() != null && !post.getUserRole().isEmpty()) {
            holder.userRole.setText(post.getUserRole());
            holder.userRole.setVisibility(View.VISIBLE);
        } else if (post.getUserId() != null) {
            // Fetch role if missing in post but userId exists
            db.collection("Users").document(post.getUserId()).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            List<String> roles = (List<String>) documentSnapshot.get("roles");
                            if (roles != null && !roles.isEmpty()) {
                                holder.userRole.setText(roles.get(0));
                                holder.userRole.setVisibility(View.VISIBLE);
                            } else {
                                holder.userRole.setText("User");
                                holder.userRole.setVisibility(View.VISIBLE);
                            }
                        }
                    });
        } else {
            holder.userRole.setText("User");
            holder.userRole.setVisibility(View.VISIBLE);
        }

        // Update UI based on user's interaction
        if (currentUserId != null) {
            if (post.getLikedBy().contains(currentUserId)) {
                holder.btnLike.setColorFilter(context.getColor(R.color.colorPrimary));
            } else {
                holder.btnLike.setColorFilter(context.getColor(R.color.textSecondary));
            }

            if (post.getDislikedBy().contains(currentUserId)) {
                holder.btnDislike.setColorFilter(context.getColor(R.color.colorPrimary));
            } else {
                holder.btnDislike.setColorFilter(context.getColor(R.color.textSecondary));
            }
        } else {
            holder.btnLike.setColorFilter(context.getColor(R.color.textSecondary));
            holder.btnDislike.setColorFilter(context.getColor(R.color.textSecondary));
        }

        holder.btnLike.setOnClickListener(v -> handleLike(post));
        holder.btnDislike.setOnClickListener(v -> handleDislike(post));

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProblemDetailActivity.class);
            intent.putExtra("problemId", post.getId());
            intent.putExtra("title", post.getTitle());
            intent.putExtra("description", post.getDesc());
            intent.putExtra("category", post.getCategory());
            context.startActivity(intent);
        });
    }

    private void handleLike(Post post) {
        String currentUserId = FirebaseAuth.getInstance().getUid();
        if (currentUserId == null) {
            Toast.makeText(context, "Please login to like", Toast.LENGTH_SHORT).show();
            return;
        }

        if (post.getLikedBy().contains(currentUserId)) {
            // Unlike
            db.collection("posts").document(post.getId())
                    .update("likesCount", FieldValue.increment(-1),
                            "likedBy", FieldValue.arrayRemove(currentUserId));
        } else {
            // Like
            db.collection("posts").document(post.getId())
                    .update("likesCount", FieldValue.increment(1),
                            "likedBy", FieldValue.arrayUnion(currentUserId));
            
            // Remove dislike if exists
            if (post.getDislikedBy().contains(currentUserId)) {
                db.collection("posts").document(post.getId())
                        .update("dislikesCount", FieldValue.increment(-1),
                                "dislikedBy", FieldValue.arrayRemove(currentUserId));
            }
        }
    }

    private void handleDislike(Post post) {
        String currentUserId = FirebaseAuth.getInstance().getUid();
        if (currentUserId == null) {
            Toast.makeText(context, "Please login to dislike", Toast.LENGTH_SHORT).show();
            return;
        }

        if (post.getDislikedBy().contains(currentUserId)) {
            // Undislike
            db.collection("posts").document(post.getId())
                    .update("dislikesCount", FieldValue.increment(-1),
                            "dislikedBy", FieldValue.arrayRemove(currentUserId));
        } else {
            // Dislike
            db.collection("posts").document(post.getId())
                    .update("dislikesCount", FieldValue.increment(1),
                            "dislikedBy", FieldValue.arrayUnion(currentUserId));

            // Remove like if exists
            if (post.getLikedBy().contains(currentUserId)) {
                db.collection("posts").document(post.getId())
                        .update("likesCount", FieldValue.increment(-1),
                                "likedBy", FieldValue.arrayRemove(currentUserId));
            }
        }
    }

    public void updateList(ArrayList<Post> newList) {
        list.clear();
        list.addAll(newList);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
