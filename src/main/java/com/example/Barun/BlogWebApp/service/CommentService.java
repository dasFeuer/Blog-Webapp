package com.example.Barun.BlogWebApp.service;

import com.example.Barun.BlogWebApp.model.Blog;
import com.example.Barun.BlogWebApp.model.Comment;
import com.example.Barun.BlogWebApp.model.User;
import com.example.Barun.BlogWebApp.repo.BlogRepository;
import com.example.Barun.BlogWebApp.repo.CommentRepository;
import com.example.Barun.BlogWebApp.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private UserRepository userRepository;

    public Comment createComment(int blogId, int userId, Comment comment){
        Optional<Blog> blog = blogRepository.findById(blogId);
        Optional<User> user = userRepository.findById(userId);

        if(blog.isPresent() && user.isPresent()){
            comment.setBlog(blog.get());
            comment.setUser(user.get());
            return commentRepository.save(comment);
        } else {
            throw new RuntimeException("Blog or User not found");
        }
    }

    public List<Comment> getAllCommentsByBlogId(int blogId){
        return commentRepository.findByBlogId(blogId);
    }


    public void deleteComment(int commentId) {
        commentRepository.deleteById(commentId);
    }

}
