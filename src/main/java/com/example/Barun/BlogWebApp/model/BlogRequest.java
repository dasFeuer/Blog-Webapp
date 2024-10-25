package com.example.Barun.BlogWebApp.model;

public class BlogRequest {

    private int userId;
    private Blog blog;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Blog getBlog() {
        return blog;
    }

    public void setBlog(Blog blog) {
        this.blog = blog;
    }
}
