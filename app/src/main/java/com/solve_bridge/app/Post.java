package com.solve_bridge.app;

import com.google.firebase.firestore.DocumentId;
import java.util.ArrayList;
import java.util.List;

public class Post {
    @DocumentId
    private String id;
    private String user, title, description, category, userId, userRole;
    private long likesCount = 0;
    private long dislikesCount = 0;
    private List<String> likedBy = new ArrayList<>();
    private List<String> dislikedBy = new ArrayList<>();
    private Object timestamp; // Added to handle Firestore timestamp field
    private boolean solved = false;
    private String acceptedSolutionId;

    public Post() {}

    public Post(String user, String title, String description, String category) {
        this.user = user;
        this.title = title;
        this.description = description;
        this.category = category;
        this.likesCount = 0;
        this.dislikesCount = 0;
        this.likedBy = new ArrayList<>();
        this.dislikedBy = new ArrayList<>();
        this.solved = false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public long getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(long likesCount) {
        this.likesCount = likesCount;
    }

    public long getDislikesCount() {
        return dislikesCount;
    }

    public void setDislikesCount(long dislikesCount) {
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

    public Object getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Object timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isSolved() {
        return solved;
    }

    public void setSolved(boolean solved) {
        this.solved = solved;
    }

    public String getAcceptedSolutionId() {
        return acceptedSolutionId;
    }

    public void setAcceptedSolutionId(String acceptedSolutionId) {
        this.acceptedSolutionId = acceptedSolutionId;
    }
}
