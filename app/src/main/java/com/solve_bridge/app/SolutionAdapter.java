package com.solve_bridge.app;

import android.content.Context;
import android.graphics.Color;
import android.text.util.Linkify;
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

import java.util.List;

public class SolutionAdapter extends RecyclerView.Adapter<SolutionAdapter.ViewHolder> {

    private List<SolutionModel> solutionList;
    private FirebaseFirestore db;
    private String currentUserId;
    private Context context;
    private String problemOwnerId;

    public SolutionAdapter(List<SolutionModel> solutionList, String problemOwnerId) {
        this.solutionList = solutionList;
        this.db = FirebaseFirestore.getInstance();
        this.currentUserId = FirebaseAuth.getInstance().getUid();
        this.problemOwnerId = problemOwnerId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context)
                .inflate(R.layout.solution_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SolutionModel solution = solutionList.get(position);
        holder.tvHeader.setText(solution.getHeader());
        holder.tvContent.setText(solution.getContent());
        holder.likes.setText(String.valueOf(solution.getLikesCount()));
        holder.dislikes.setText(String.valueOf(solution.getDislikesCount()));

        // GitHub/Project Link
        if (solution.getSolutionLink() != null && !solution.getSolutionLink().isEmpty()) {
            holder.tvSolutionLink.setText("View Project: " + solution.getSolutionLink());
            holder.tvSolutionLink.setVisibility(View.VISIBLE);
            Linkify.addLinks(holder.tvSolutionLink, Linkify.WEB_URLS);
        } else {
            holder.tvSolutionLink.setVisibility(View.GONE);
        }

        // Accepted Status UI
        if (solution.isAccepted()) {
            holder.tvAcceptedBadge.setVisibility(View.VISIBLE);
            holder.cardSolution.setStrokeColor(Color.parseColor("#4CAF50"));
            holder.cardSolution.setStrokeWidth(4);
            holder.btnAccept.setVisibility(View.GONE);
        } else {
            holder.tvAcceptedBadge.setVisibility(View.GONE);
            holder.cardSolution.setStrokeWidth(0);
            
            // Show "Mark as Accepted" button ONLY to the problem owner
            if (currentUserId != null && currentUserId.equals(problemOwnerId)) {
                holder.btnAccept.setVisibility(View.VISIBLE);
            } else {
                holder.btnAccept.setVisibility(View.GONE);
            }
        }

        holder.btnAccept.setOnClickListener(v -> handleAccept(solution));

        // Like/Dislike UI
        if (currentUserId != null) {
            if (solution.getLikedBy().contains(currentUserId)) {
                holder.btnLike.setColorFilter(context.getColor(R.color.colorPrimary));
            } else {
                holder.btnLike.setColorFilter(context.getColor(R.color.textSecondary));
            }

            if (solution.getDislikedBy().contains(currentUserId)) {
                holder.btnDislike.setColorFilter(context.getColor(R.color.colorPrimary));
            } else {
                holder.btnDislike.setColorFilter(context.getColor(R.color.textSecondary));
            }
        }

        holder.btnLike.setOnClickListener(v -> handleLike(solution));
        holder.btnDislike.setOnClickListener(v -> handleDislike(solution));
        holder.btnReport.setOnClickListener(v -> Toast.makeText(context, "Reported successfully", Toast.LENGTH_SHORT).show());
    }

    private void handleAccept(SolutionModel solution) {
        // First, unaccept any other solution for this problem
        db.collection("solutions")
                .whereEqualTo("problemId", solution.getProblemId())
                .whereEqualTo("accepted", true)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (var doc : queryDocumentSnapshots) {
                        doc.getReference().update("accepted", false);
                    }
                    // Then accept the new one
                    db.collection("solutions").document(solution.getId())
                            .update("accepted", true)
                            .addOnSuccessListener(unused -> Toast.makeText(context, "Solution Accepted!", Toast.LENGTH_SHORT).show());
                });
    }

    private void handleLike(SolutionModel solution) {
        if (currentUserId == null) return;
        if (solution.getLikedBy().contains(currentUserId)) {
            db.collection("solutions").document(solution.getId())
                    .update("likesCount", FieldValue.increment(-1),
                            "likedBy", FieldValue.arrayRemove(currentUserId));
        } else {
            db.collection("solutions").document(solution.getId())
                    .update("likesCount", FieldValue.increment(1),
                            "likedBy", FieldValue.arrayUnion(currentUserId));
            if (solution.getDislikedBy().contains(currentUserId)) {
                db.collection("solutions").document(solution.getId())
                        .update("dislikesCount", FieldValue.increment(-1),
                                "dislikedBy", FieldValue.arrayRemove(currentUserId));
            }
        }
    }

    private void handleDislike(SolutionModel solution) {
        if (currentUserId == null) return;
        if (solution.getDislikedBy().contains(currentUserId)) {
            db.collection("solutions").document(solution.getId())
                    .update("dislikesCount", FieldValue.increment(-1),
                            "dislikedBy", FieldValue.arrayRemove(currentUserId));
        } else {
            db.collection("solutions").document(solution.getId())
                    .update("dislikesCount", FieldValue.increment(1),
                            "dislikedBy", FieldValue.arrayUnion(currentUserId));
            if (solution.getLikedBy().contains(currentUserId)) {
                db.collection("solutions").document(solution.getId())
                        .update("likesCount", FieldValue.increment(-1),
                                "likedBy", FieldValue.arrayRemove(currentUserId));
            }
        }
    }

    @Override
    public int getItemCount() {
        return solutionList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvHeader, tvContent, tvSolutionLink, tvAcceptedBadge, btnAccept, likes, dislikes, btnReport;
        ImageView btnLike, btnDislike;
        com.google.android.material.card.MaterialCardView cardSolution;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardSolution = (com.google.android.material.card.MaterialCardView) itemView.findViewById(R.id.cardSolution);
            tvHeader = itemView.findViewById(R.id.tvHeader);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvSolutionLink = itemView.findViewById(R.id.tvSolutionLink);
            tvAcceptedBadge = itemView.findViewById(R.id.tvAcceptedBadge);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            likes = itemView.findViewById(R.id.tvLikes);
            dislikes = itemView.findViewById(R.id.tvDislikes);
            btnLike = itemView.findViewById(R.id.btnLike);
            btnDislike = itemView.findViewById(R.id.btnDislike);
            btnReport = itemView.findViewById(R.id.btnReport);
        }
    }
}
