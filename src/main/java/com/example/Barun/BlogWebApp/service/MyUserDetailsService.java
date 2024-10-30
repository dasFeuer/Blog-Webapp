package com.example.Barun.BlogWebApp.service;

import com.example.Barun.BlogWebApp.model.UserPrincipal;
import com.example.Barun.BlogWebApp.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(MyUserDetailsService.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (username == null || username.trim().isEmpty()) {
            logger.error("Username or email is null or empty");
            throw new UsernameNotFoundException("Username or email cannot be empty");
        }

        logger.info("Attempting to load user: {}", username);

        return userRepository.findByUsername(username)
                .or(() -> userRepository.findByEmail(username))
                .map(UserPrincipal::new)
                .orElseThrow(() -> {
                    logger.error("User Not Found with username or email: {}", username);
                    return new UsernameNotFoundException("User not found with username or email: " + username);
                });
    }
}
