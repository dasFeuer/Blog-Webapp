package com.example.Barun.BlogWebApp.controller;

import com.example.Barun.BlogWebApp.model.LoginRequest;
import com.example.Barun.BlogWebApp.model.User;
import com.example.Barun.BlogWebApp.service.JWTService;
import com.example.Barun.BlogWebApp.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JWTService jwtService;

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable int id) {
        Optional<User> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping("/register")
    public ResponseEntity<?> createUser(@Valid @RequestBody User user) {
        try {
            User createdUser = userService.createUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (RuntimeException e) {
            String message = e.getMessage();
            if ("Username already taken".equals(message)) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already taken");
            } else if ("Email already taken".equals(message)) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already taken");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
            }
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        System.out.println("Login: " + loginRequest.getUsername());
        System.out.println("Password: " + loginRequest.getPassword());

        try {
            Map<String, String> tokens = userService.authenticateAndGenerateTokens(
                    loginRequest.getUsername(), loginRequest.getPassword());

            // Retrieve user details
            User user = userService.getUserByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            Map<String, Object> response = new HashMap<>();
            response.put("id", user.getId()); // Assuming User has a getId() method
            response.put("username", user.getUsername());
            response.put("email", user.getEmail()); // Assuming User has a getEmail() method
            response.put("role", user.getRole());
            response.put("accessToken", tokens.get("accessToken"));
            response.put("refreshToken", tokens.get("refreshToken")); // Add refresh token if applicable

            return ResponseEntity.ok(response);

        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception for debugging
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestParam("refreshToken") String refreshToken) {
        try {
            // Validate the refresh token
            if (jwtService.validateRefreshToken(refreshToken)) {
                // Get the username from the refresh token and generate a new access token
                String username = jwtService.extractUsername(refreshToken);
                String newAccessToken = jwtService.generateAccessToken(username);

                Map<String, String> tokenResponse = new HashMap<>();
                tokenResponse.put("accessToken", newAccessToken);
                tokenResponse.put("refreshToken", refreshToken); // Optionally send the same refresh token

                return ResponseEntity.ok(tokenResponse);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired refresh token");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while refreshing token");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        // Clear the authentication context
        SecurityContextHolder.clearContext();

        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        return ResponseEntity.ok("Logged out successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable int id, @RequestBody User updatedUser) {
        try {
            User user = userService.updateUser(id, updatedUser);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

//    @DeleteMapping("/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<Void> deleteUser(@PathVariable int id) {
//        userService.deleteUser(id);
//        return ResponseEntity.noContent().build();
//    }
}
