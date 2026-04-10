package com.solve_bridge.app;

import com.google.firebase.firestore.DocumentId;
import java.util.ArrayList;
import java.util.List;

public class SolutionModel {
    @DocumentId
    private String id;
    private String header;
    private String content;
    private int likesCount = 0;
    private int dislikesCount = 0;
    private List<String> likedBy = new ArrayList<>();
    private List<String> dislikedBy = new ArrayList<>();

    public SolutionModel() {}

    public SolutionModel(String header, String content) {
        this.header = header;
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public int getDislikesCount() {
        return dislikesCount;
    }

    public void setDislikesCount(int dislikesCount) {
        this.dislikesCount = dislikesCount;
    }

    public List<String> getLikedBy() {
        if (likedBy == null) likedBy = new ArrayList<>();
        return likedBy;
    }

    public void setLikedBy(List<String> likedBy) {
        this.likedBy = likedBy;
    }

    public List<String> getDislikedBy() {
        if (dislikedBy == null) dislikedBy = new ArrayList<>();
        return dislikedBy;
    }

    public void setDislikedBy(List<String> dislikedBy) {
        this.dislikedBy = dislikedBy;
    }
}
