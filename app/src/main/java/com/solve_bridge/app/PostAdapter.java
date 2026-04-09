package com.solve_bridge.app;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private ArrayList<Post> list;
    private Context context;

    public PostAdapter(Context context, ArrayList<Post> list) {
        this.context = context;
        this.list = list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView user, title, desc;

        public ViewHolder(View itemView) {
            super(itemView);

            user = itemView.findViewById(R.id.tvUser);
            title = itemView.findViewById(R.id.tvTitle);
            desc = itemView.findViewById(R.id.tvDesc);
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

        holder.user.setText(post.getUser());
        holder.title.setText(post.getTitle());
        holder.desc.setText(post.getDesc());

        // 🔥 ITEM CLICK LISTENER
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProblemDetailActivity.class);

            intent.putExtra("problemId", post.getId());
            intent.putExtra("title", post.getTitle());
            intent.putExtra("description", post.getDesc());
            intent.putExtra("category", post.getCategory());

            context.startActivity(intent);
        });
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
