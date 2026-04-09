package com.solve_bridge.app;

import com.google.firebase.firestore.DocumentId;

public class Post {
    @DocumentId
    private String id;
    private String user, title, desc, category;

    public Post() {}

    public Post(String user, String title, String desc, String category) {
        this.user = user;
        this.title = title;
        this.desc = desc;
        this.category = category;
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
}
