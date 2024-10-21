package com.example.Barun.BlogWebApp.service;

import com.example.Barun.BlogWebApp.model.User;
import com.example.Barun.BlogWebApp.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    private static final String ROLE_ADMIN = "ADMIN";
    private static final String ROLE_USER = "USER";

    public User promoteToAdmin(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setRole(ROLE_ADMIN);
        return userRepository.save(user);
    }

    public User demoteToUser(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setRole(ROLE_USER);
        return userRepository.save(user);
    }

    public void deleteUser(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        userRepository.delete(user);
    }

}
