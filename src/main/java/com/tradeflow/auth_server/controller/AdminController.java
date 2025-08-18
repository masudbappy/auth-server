package com.tradeflow.auth_server.controller;

import com.tradeflow.auth_server.dto.MessageResponse;
import com.tradeflow.auth_server.dto.RegisterRequest;
import com.tradeflow.auth_server.dto.UserResponse;
import com.tradeflow.auth_server.model.User;
import com.tradeflow.auth_server.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000"})
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private UserService userService;

    @PostMapping("/users")
    public ResponseEntity<?> createUser(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            User user = userService.registerUser(registerRequest);
            logger.info("Admin created user: {}", user.getUsername());
            return ResponseEntity.ok("Created users successfully: " + user.getUsername());
        } catch (RuntimeException e) {
            logger.error("User creation failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(e.getMessage()));
        }
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        try {
            List<UserResponse> users = userService.getAllUsers();
            // Remove the redundant mapping since users is already List<UserResponse>
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            logger.error("Failed to fetch users: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Failed to fetch users"));
        }
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUserByAdmin(@PathVariable Long id, @Valid @RequestBody RegisterRequest updateRequest) {
        try {
            UserResponse updatedUser = userService.updateUserByAdmin(id, updateRequest);
            logger.info("Admin updated user with ID: {}", id);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            logger.error("User update failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(e.getMessage()));
        }
    }

    @PutMapping("/users/{id}/reset-password")
    public ResponseEntity<?> resetPasswordByAdmin(@PathVariable Long id, @RequestBody String newPassword) {
        try {
            userService.resetPasswordByAdmin(id, newPassword);
            logger.info("Admin reset password for user with ID: {}", id);
            return ResponseEntity.ok(new MessageResponse("Password reset successfully"));
        } catch (RuntimeException e) {
            logger.error("Password reset failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            logger.info("Admin deleted user with ID: {}", id);
            return ResponseEntity.ok(new MessageResponse("User deleted successfully"));
        } catch (RuntimeException e) {
            logger.error("User deletion failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(e.getMessage()));
        }
    }
}