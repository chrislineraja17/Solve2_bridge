package com.solve_bridge.app;

import com.google.firebase.firestore.DocumentId;
import java.util.ArrayList;
import java.util.List;

public class Post {
    @DocumentId
    private String id;
    private String user, title, desc, category;
    private long likesCount = 0;
    private long dislikesCount = 0;
    private List<String> likedBy = new ArrayList<>();
    private List<String> dislikedBy = new ArrayList<>();

    public Post() {}

    public Post(String user, String title, String desc, String category) {
        this.user = user;
        this.title = title;
        this.desc = desc;
        this.category = category;
        this.likesCount = 0;
        this.dislikesCount = 0;
        this.likedBy = new ArrayList<>();
        this.dislikedBy = new ArrayList<>();
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

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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
}
