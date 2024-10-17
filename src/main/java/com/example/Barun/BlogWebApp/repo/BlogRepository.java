package com.example.Barun.BlogWebApp.repo;

import com.example.Barun.BlogWebApp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BlogRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
