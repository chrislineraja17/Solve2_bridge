package com.solve_bridge.app;

import android.content.Context;
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

    public SolutionAdapter(List<SolutionModel> solutionList) {
        this.solutionList = solutionList;
        this.db = FirebaseFirestore.getInstance();
        this.currentUserId = FirebaseAuth.getInstance().getUid();
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
        TextView tvHeader, tvContent, likes, dislikes, btnReport;
        ImageView btnLike, btnDislike;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHeader = itemView.findViewById(R.id.tvHeader);
            tvContent = itemView.findViewById(R.id.tvContent);
            likes = itemView.findViewById(R.id.tvLikes);
            dislikes = itemView.findViewById(R.id.tvDislikes);
            btnLike = itemView.findViewById(R.id.btnLike);
            btnDislike = itemView.findViewById(R.id.btnDislike);
            btnReport = itemView.findViewById(R.id.btnReport);
        }
    }
}
