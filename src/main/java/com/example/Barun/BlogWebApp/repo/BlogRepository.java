package com.example.Barun.BlogWebApp.repo;

import com.example.Barun.BlogWebApp.model.Blog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Integer> {
    List<Blog> findByUserUsername(String username);
    List<Blog> findByUserId(int userId); // Fetch blogs by user ID

}
