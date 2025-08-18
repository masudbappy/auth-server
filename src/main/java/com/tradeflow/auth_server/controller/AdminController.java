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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = { "http://localhost:3000", "http://127.0.0.1:3000" })
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

	/*@PutMapping("/users/{userId}")
	public ResponseEntity<?> updateUser(@PathVariable Long userId,
	                                    @Valid @RequestBody UpdateUserRequest updateRequest) {
		try {
			User updatedUser = userService.updateUserByAdmin(userId, updateRequest);
			logger.info("Admin updated user: {}", updatedUser.getUsername());
			return ResponseEntity.ok(new UserResponse(updatedUser));
		} catch (RuntimeException e) {
			logger.error("User update failed: {}", e.getMessage());
			return ResponseEntity.badRequest()
					.body(new MessageResponse(e.getMessage()));
		}
	}*/

	/*@PostMapping("/users/{userId}/reset-password")
	public ResponseEntity<?> resetUserPassword(@PathVariable Long userId,
	                                           @Valid @RequestBody ResetPasswordRequest resetRequest) {
		try {
			userService.resetPasswordByAdmin(userId, resetRequest.getNewPassword());
			logger.info("Admin reset password for user ID: {}", userId);
			return ResponseEntity.ok(new MessageResponse("Password reset successfully"));
		} catch (RuntimeException e) {
			logger.error("Password reset failed: {}", e.getMessage());
			return ResponseEntity.badRequest()
					.body(new MessageResponse(e.getMessage()));
		}
	}*/

	/*@PutMapping("/users/{userId}/status")
	public ResponseEntity<?> updateUserStatus(@PathVariable Long userId,
	                                          @RequestBody UpdateStatusRequest statusRequest) {
		try {
			User updatedUser = userService.updateUserStatus(userId, statusRequest.isEnabled());
			logger.info("Admin updated status for user: {}", updatedUser.getUsername());
			return ResponseEntity.ok(new UserResponse(updatedUser));
		} catch (RuntimeException e) {
			logger.error("Status update failed: {}", e.getMessage());
			return ResponseEntity.badRequest()
					.body(new MessageResponse(e.getMessage()));
		}
	}*/
}