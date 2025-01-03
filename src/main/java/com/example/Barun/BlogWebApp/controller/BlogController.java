package com.example.Barun.BlogWebApp.controller;

import com.example.Barun.BlogWebApp.model.Blog;
import com.example.Barun.BlogWebApp.model.BlogRequest;
import com.example.Barun.BlogWebApp.service.BlogService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/blogs")
public class BlogController {

    private static final Logger logger = LoggerFactory.getLogger(BlogController.class);

    @Autowired
    private BlogService blogService;

    @PostMapping
    public ResponseEntity<?> createBlog(@Valid @RequestBody BlogRequest blogRequest, BindingResult result, Principal principal) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getFieldErrors());
        }
        try {
            String username = principal.getName();
            int userId = blogService.getUserIdByUsername(username); // Retrieve the user ID
            logger.info("Creating blog for user ID: {}", userId);
            Blog createdBlog = blogService.createBlog(userId, blogRequest.getBlog()); // Pass user ID here
            return ResponseEntity.status(HttpStatus.CREATED).body(createdBlog);
        } catch (RuntimeException e) {
            logger.error("Runtime exception during blog creation: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Exception occurred during blog creation: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating blog: " + e.getMessage());
        }
    }


    @GetMapping
    public ResponseEntity<List<Blog>> getAllBlogs() {
        List<Blog> blogs = blogService.getAllBlogs();
        return ResponseEntity.ok(blogs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBlogById(@PathVariable int id) {
        return blogService.getBlogById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Blog with ID " + id + " not found"));
    }

    @Transactional
    @PutMapping("/{id}")
    public ResponseEntity<?> updateBlog(@PathVariable int id, @Valid @RequestBody Blog updatedBlog, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getFieldErrors());
        }
        try {
            Blog blog = blogService.updateBlog(id, updatedBlog);
            return ResponseEntity.ok(blog);
        } catch (RuntimeException e) {
            logger.error("Error updating blog: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Exception during blog update: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating blog: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBlog(@PathVariable int id) {
        try {
            blogService.deleteBlog(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            logger.warn("Blog with ID {} not found for deletion", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            logger.error("Exception occurred during blog deletion: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/user/username/{username}")
    public ResponseEntity<List<Blog>> getBlogsByUser(@PathVariable String username) {
        List<Blog> blogs = blogService.getBlogsByUsername(username);
        return ResponseEntity.ok(blogs);
    }

    @GetMapping("/user/id/{id}")
    public ResponseEntity<List<Blog>> getBlogsByUserId(@PathVariable int id) {
        List<Blog> blogs = blogService.getBlogsByUserId(id);
        return ResponseEntity.ok(blogs);
    }

    @GetMapping("/sorted")
    public ResponseEntity<Page<Blog>> getAllBlogsSorted(Pageable pageable) {
        Page<Blog> blogs = blogService.getAllBlogsSorted(pageable);
        return ResponseEntity.ok(blogs);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Blog>> searchBlogs(@RequestParam String title, Pageable pageable) {
        Page<Blog> blogs = blogService.searchBlogsByTitle(title, pageable);
        return ResponseEntity.ok(blogs);
    }
}
