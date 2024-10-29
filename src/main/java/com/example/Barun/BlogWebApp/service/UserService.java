package com.example.Barun.BlogWebApp.service;

import com.example.Barun.BlogWebApp.model.User;
import com.example.Barun.BlogWebApp.model.UserPrincipal;
import com.example.Barun.BlogWebApp.repo.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {
    private static final String ROLE_ADMIN = "ADMIN";
    private static final String ROLE_USER = "USER";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JWTService jwtService;

    @Value("${admin.username}")
    private String adminUsername;

    @Value("${admin.password}")
    private String adminPassword;

    @Value("${admin.email}")
    private String adminEmail;

    @PostConstruct
    public void initAdminUser() {
        if (!userRepository.findByEmail(adminEmail).isPresent()) {
            User adminUser = new User();
            adminUser.setUsername(adminUsername);
            adminUser.setEmail(adminEmail);
            adminUser.setPassword(passwordEncoder.encode(adminPassword));
            adminUser.setRole(ROLE_ADMIN);
            userRepository.save(adminUser);
        }
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(int id) {
        return userRepository.findById(id);
    }

    public boolean isEmailTaken(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public boolean isUsernameTaken(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    public User createUser(User user) {
        if (isEmailTaken(user.getEmail())) {
            throw new RuntimeException("Email already taken");
        }
        if (isUsernameTaken(user.getUsername())) {
            throw new RuntimeException("Username already taken");
        }
        user.setRole(ROLE_USER);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User updateUser(int id, User updatedUser) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setUsername(updatedUser.getUsername());
        user.setEmail(updatedUser.getEmail());
        user.setRole(updatedUser.getRole());
        return userRepository.save(user);
    }

    public void deleteUser(int id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            userRepository.deleteById(id);
        } else {
            throw new RuntimeException("User not found");
        }
    }

    public Map<String, String> authenticateAndGenerateTokens(String usernameOrEmail, String password) {
        try {
            Optional<User> userOptional = userRepository.findByUsername(usernameOrEmail)
                    .or(() -> userRepository.findByEmail(usernameOrEmail));

            if (userOptional.isEmpty()) {
                throw new UsernameNotFoundException("User not found with username or email: " + usernameOrEmail);
            }

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(usernameOrEmail, password)
            );
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

            // Generate both access and refresh tokens
            String accessToken = jwtService.generateAccessToken(userPrincipal.getUsername());
            String refreshToken = jwtService.generateRefreshToken(userPrincipal.getUsername());

            Map<String, String> tokens = new HashMap<>();
            tokens.put("accessToken", accessToken);
            tokens.put("refreshToken", refreshToken);

            return tokens;
        } catch (UsernameNotFoundException e) {
            throw e;
        } catch (BadCredentialsException e) {
            throw new UsernameNotFoundException("Invalid Credentials");
        } catch (Exception e) {
            throw new RuntimeException("Authentication failed");
        }
    }
}
