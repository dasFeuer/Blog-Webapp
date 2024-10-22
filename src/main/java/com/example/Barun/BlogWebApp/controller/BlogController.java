package com.example.Barun.BlogWebApp.controller;

import com.example.Barun.BlogWebApp.model.Blog;
import com.example.Barun.BlogWebApp.service.BlogService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@RestController
@RequestMapping("/api/blogs")
public class BlogController {

    @Autowired
    private BlogService blogService;

    @PostMapping
    private ResponseEntity<Blog> createBlog(@RequestBody Blog blog, @RequestParam int userId){
        Blog createdBlog = blogService.createBlog(userId, blog);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBlog);
    }

    @GetMapping
    public ResponseEntity<List<Blog>> getAllBlogs(){
        List<Blog> blogs = blogService.getAllBlogs();
        return ResponseEntity.ok(blogs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Blog> getBlogById(@PathVariable int id){
        return blogService.getBlogById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @Transactional
    @PutMapping("/{id}")
    public ResponseEntity<Blog> updateBlog(@PathVariable int id, @RequestBody Blog updatedBlog){
        try{
            Blog blog = blogService.updateBlog(id, updatedBlog);
            return ResponseEntity.ok(blog);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBlog(@PathVariable int id){
        blogService.deleteBlog(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<List<Blog>> getBlogsByUser(@PathVariable String username){
        List<Blog> blogs = blogService.getBlogsByUsername(username);
        return ResponseEntity.ok(blogs);
    }

    @PostMapping("/upload")
    @PreAuthorize("hasRole('ADMIN')")
    public String uploadFile(MultipartFile file) {
        try {
            String uploadDir = "C:\\Users\\Acer\\Desktop\\MediaUpload";  // Define your upload directory
            Path path = Paths.get(uploadDir + file.getOriginalFilename());
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            return path.toString();
        } catch (IOException e) {
            throw new RuntimeException("Error uploading file", e);
        }
    }
}
