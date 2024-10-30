package com.example.Barun.BlogWebApp.repo;

import com.example.Barun.BlogWebApp.model.Blog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Integer> {

    List<Blog> findByUserUsername(String username);

    List<Blog> findByUserId(int userId);

    Page<Blog> findAllByOrderByCreatedAtDesc(Pageable pageable); // Get blogs sorted by latest

    Page<Blog> findByTitleContainingIgnoreCase(String title, Pageable pageable); // Search blogs by title
}
