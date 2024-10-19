package com.example.Barun.BlogWebApp.controller;

import com.example.Barun.BlogWebApp.model.Comment;
import com.example.Barun.BlogWebApp.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/blogs/{blogId}/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @GetMapping
    public ResponseEntity<List<Comment>> getAllComments(@PathVariable int blogId) {
        List<Comment> comments = commentService.getAllCommentsByBlogId(blogId);
        return ResponseEntity.ok(comments);
    }

    @PostMapping
    public ResponseEntity<Comment> createComment(@PathVariable int blogId, @RequestBody Comment comment,
                                                 @RequestParam int userId) {
        Comment createdComment = commentService.createComment(blogId, userId, comment);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdComment);
    }

    @DeleteMapping("/{commentId}")
        public ResponseEntity<Void> deleteComment(@PathVariable int commentId){
            commentService.deleteComment(commentId);
            return ResponseEntity.noContent().build();
        }

}
