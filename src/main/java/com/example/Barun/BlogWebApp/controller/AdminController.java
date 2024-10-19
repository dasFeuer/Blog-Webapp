package com.example.Barun.BlogWebApp.controller;

import com.example.Barun.BlogWebApp.model.User;
import com.example.Barun.BlogWebApp.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PutMapping("/promote/{userId}")
    public ResponseEntity<User> promoteToAdmin(@PathVariable int userId) {
        return ResponseEntity.ok((adminService.promoteToAdmin(userId)));
    }

    @PutMapping("/demote/{userId}")
    public ResponseEntity<User> demoteToAdmin(@PathVariable int userId) {
        return ResponseEntity.ok((adminService.demoteToUser(userId)));
    }

    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable int userId){
        adminService.deleteUser(userId);
        return ResponseEntity.ok("User and associated data deleted successfully");
    }
}
