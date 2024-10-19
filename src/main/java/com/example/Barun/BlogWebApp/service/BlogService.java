package com.example.Barun.BlogWebApp.service;

import com.example.Barun.BlogWebApp.model.Blog;
import com.example.Barun.BlogWebApp.model.User;
import com.example.Barun.BlogWebApp.repo.BlogRepository;
import com.example.Barun.BlogWebApp.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BlogService {

    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private UserRepository userRepository;

    public Blog createBlog(int userId, Blog blog){
        Optional<User> user = userRepository.findById(userId);
        if(user.isPresent()){
            blog.setUser(user.get());
            return blogRepository.save(blog);
        } else {
            throw new RuntimeException("User not found");
        }
    }

    public List<Blog>getAllBlogs(){
        return blogRepository.findAll();
    }

    public Optional<Blog> getBlogById(int id) {
        return blogRepository.findById(id);
    }

    public Blog updateBlog(int id, Blog updatedBlog) {
        Optional<Blog> existingBlog = blogRepository.findById(id);
        if (existingBlog.isPresent()) {
            Blog blog = existingBlog.get();
            blog.setTitle(updatedBlog.getTitle());
            blog.setContent(updatedBlog.getContent());
            return blogRepository.save(blog);
        } else {
            throw new RuntimeException("Blog not found");
        }
    }

    public void deleteBlog(int id) {
        blogRepository.deleteById(id);
    }

    public List<Blog> getBlogsByUsername(String username){
        return blogRepository.findByUserUsername(username);
    }
}
