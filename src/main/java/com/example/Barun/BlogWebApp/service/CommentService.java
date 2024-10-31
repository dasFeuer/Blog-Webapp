package com.example.Barun.BlogWebApp.service;

import com.example.Barun.BlogWebApp.model.Blog;
import com.example.Barun.BlogWebApp.model.Comment;
import com.example.Barun.BlogWebApp.model.User;
import com.example.Barun.BlogWebApp.repo.BlogRepository;
import com.example.Barun.BlogWebApp.repo.CommentRepository;
import com.example.Barun.BlogWebApp.repo.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public Comment createComment(int blogId, int userId, String content) {
        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new RuntimeException("Blog not found with ID: " + blogId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User  not found with ID: " + userId));

        // Validate comment content
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Comment content must not be empty.");
        }

        Comment comment = new Comment();
        comment.setContent(content);
        comment.setBlog(blog);
        comment.setUser (user);

        return commentRepository.save(comment);
    }

    public List<Comment> getAllCommentsByBlogId(int blogId) {
        return commentRepository.findByBlogId(blogId);
    }

    public void deleteComment(int commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new RuntimeException("Comment not found with ID: " + commentId);
        }
        commentRepository.deleteById(commentId);
    }

    @Transactional
    public Comment updateComment(int blogId, int commentId, String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Comment content must not be empty.");
        }

        // Find the blog to ensure the blogId exists
        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new RuntimeException("Blog not found with ID: " + blogId));

        // Find the comment associated with the blog
        Comment comment = commentRepository.findById(commentId)
                .filter(c -> c.getBlog().getId() == blogId) // Ensure the comment belongs to the blog
                .orElseThrow(() -> new RuntimeException("Comment not found with ID: " + commentId + " for the specified blog"));

        // Update the comment content
        comment.setContent(content);
        return commentRepository.save(comment);
    }
}