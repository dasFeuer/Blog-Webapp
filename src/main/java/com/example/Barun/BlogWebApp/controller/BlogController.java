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
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/blogs")
public class BlogController {

    private static final Logger logger = LoggerFactory.getLogger(BlogController.class);

    @Autowired
    private BlogService blogService;

    @Value("${file.upload-dir}")
    private String uploadDir;

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

    @PostMapping("/upload")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String contentType = file.getContentType();
            if (!isValidContentType(contentType)) {
                logger.warn("Invalid file type: {}", contentType);
                return ResponseEntity.badRequest().body("Invalid file type: " + contentType);
            }

            ensureDirectoryExists(uploadDir);

            String uniqueFileName = UUID.randomUUID().toString() + "_" + sanitizeFilename(file.getOriginalFilename());
            Path path = Paths.get(uploadDir, uniqueFileName);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            return ResponseEntity.ok("File uploaded successfully: " + path.toString());
        } catch (IOException e) {
            logger.error("Error uploading file: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading file: " + e.getMessage());
        }
    }

    private boolean isValidContentType(String contentType) {
        return contentType != null &&
                (contentType.equals("image/jpeg") || contentType.equals("image/png") || contentType.equals("image/gif"));
    }

    private void ensureDirectoryExists(String dir) {
        File directory = new File(dir);
        if (!directory.exists() && !directory.mkdirs()) {
            throw new RuntimeException("Could not create upload directory");
        }
    }

    private String sanitizeFilename(String filename) {
        return filename.replaceAll("[^a-zA-Z0-9._-]", "_");
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
