package com.example.Barun.BlogWebApp.exception;

public class BlogNotFoundException extends  RuntimeException{
    public BlogNotFoundException (String message) {
        super(message);
    }
}
