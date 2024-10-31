package com.example.Barun.BlogWebApp.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class CommentRequest {

    @NotBlank(message = "Content cannot be empty")
    private String content;

    @Min(value = 1, message = "User ID must be greater than 0")
    private int userId;

    public String getContent() {
        return content;
    }

    public void setContent(@NotBlank(message = "Content cannot be empty") String content) {
        this.content = content;
    }

    @Min(value = 1, message = "User ID must be greater than 0")
    public int getUserId() {
        return userId;
    }

    public void setUserId(@Min(value = 1, message = "User ID must be greater than 0") int userId) {
        this.userId = userId;
    }

}