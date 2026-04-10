package com.solve_bridge.app;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.PropertyName;
import java.util.ArrayList;
import java.util.List;

public class SolutionModel {
    @DocumentId
    private String id;
    private String header;
    private String content;
    private String solutionText; // Added to match Firestore field
    private String solutionLink;
    private String problemId;
    private String userId;
    private boolean accepted = false;
    private int likesCount = 0;
    private int dislikesCount = 0;
    private List<String> likedBy = new ArrayList<>();
    private List<String> dislikedBy = new ArrayList<>();
    private Object timestamp; // Added to match Firestore field

    public SolutionModel() {}

    public SolutionModel(String header, String content) {
        this.header = header;
        this.content = content;
        this.solutionText = content;
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
        return solutionText != null ? solutionText : content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSolutionText() {
        return solutionText;
    }

    public void setSolutionText(String solutionText) {
        this.solutionText = solutionText;
    }

    public String getSolutionLink() {
        return solutionLink;
    }

    public void setSolutionLink(String solutionLink) {
        this.solutionLink = solutionLink;
    }

    public String getProblemId() {
        return problemId;
    }

    public void setProblemId(String problemId) {
        this.problemId = problemId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
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

    public Object getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Object timestamp) {
        this.timestamp = timestamp;
    }
}
