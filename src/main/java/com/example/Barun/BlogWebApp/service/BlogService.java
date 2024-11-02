package com.example.Barun.BlogWebApp.service;

import com.example.Barun.BlogWebApp.exception.BlogNotFoundException;
import com.example.Barun.BlogWebApp.exception.UserNotFoundException;
import com.example.Barun.BlogWebApp.model.Blog;
import com.example.Barun.BlogWebApp.model.User;
import com.example.Barun.BlogWebApp.repo.BlogRepository;
import com.example.Barun.BlogWebApp.repo.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class BlogService {

    private static final Logger logger = LoggerFactory.getLogger(BlogService.class);

    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Retrieve the user ID by username.
     * @param username the username of the user
     * @return the user ID if the user exists
     * @throws UserNotFoundException if no user is found with the given username
     */
    public int getUserIdByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User with username " + username + " not found"));
        return user.getId();
    }

    @Transactional
    public Blog createBlog(int userId, Blog blog) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + userId + " not found"));

        blog.setUser(user);
        Blog savedBlog = blogRepository.save(blog);
        logger.info("Blog created: {}", savedBlog);
        return savedBlog;
    }

    public List<Blog> getAllBlogs() {
        return blogRepository.findAll();
    }

    public Optional<Blog> getBlogById(int id) {
        return blogRepository.findById(id);
    }

    public Page<Blog> getAllBlogsSorted(Pageable pageable) {
        return blogRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    public Page<Blog> searchBlogsByTitle(String title, Pageable pageable) {
        return blogRepository.findByTitleContainingIgnoreCase(title, pageable);
    }

    @Transactional
    public Blog updateBlog(int id, Blog updatedBlog) {
        Blog existingBlog = blogRepository.findById(id)
                .orElseThrow(() -> new BlogNotFoundException("Blog with ID " + id + " not found"));

        existingBlog.setTitle(updatedBlog.getTitle());
        existingBlog.setContent(updatedBlog.getContent());
        Blog savedBlog = blogRepository.save(existingBlog);
        logger.info("Blog updated: {}", savedBlog);
        return savedBlog;
    }

    @Transactional
    public void deleteBlog(int id) {
        if (!blogRepository.existsById(id)) {
            logger.warn("Attempted to delete non-existing blog with ID: {}", id);
            throw new BlogNotFoundException("Blog with ID " + id + " not found");
        }
        blogRepository.deleteById(id);
        logger.info("Blog with ID {} deleted", id);
    }

    public List<Blog> getBlogsByUsername(String username) {
        return blogRepository.findByUserUsername(username);
    }

    public List<Blog> getBlogsByUserId(int id) {
        return blogRepository.findByUserId(id);
    }
}
