package com.example.Barun.BlogWebApp.controller;

import com.example.Barun.BlogWebApp.model.User;
import com.example.Barun.BlogWebApp.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/promote/{userId}")
    public ResponseEntity<User> promoteToAdmin(@PathVariable int userId) {
        try{
            User user = adminService.promoteToAdmin(userId);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/demote/{userId}")
    public ResponseEntity<User> demoteToUser(@PathVariable int userId) {
        try{
            User user = adminService.demoteToUser(userId);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable int userId){
        try{
            adminService.deleteUser(userId);
            return ResponseEntity.ok("User and associated data deleted successfully");
        } catch (RuntimeException e) {
            return  ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }
}
