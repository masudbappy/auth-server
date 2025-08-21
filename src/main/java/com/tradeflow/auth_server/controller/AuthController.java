package com.tradeflow.auth_server.controller;

import com.tradeflow.auth_server.dto.JwtResponse;
import com.tradeflow.auth_server.dto.LoginRequest;
import com.tradeflow.auth_server.dto.MessageResponse;
import com.tradeflow.auth_server.dto.RegisterRequest;
import com.tradeflow.auth_server.model.User;
import com.tradeflow.auth_server.service.JwtTokenService;
import com.tradeflow.auth_server.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = { "http://localhost:3000", "http://127.0.0.1:3000" })
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenService jwtTokenService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            logger.info("Attempting authentication for user: {}", loginRequest.getUsername());
            logger.info("Password provided: {}", loginRequest.getPassword().length() + " characters");

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtTokenService.generateToken(authentication);

            User userPrincipal = (User) authentication.getPrincipal();

            // Update last login time
            userService.updateLastLogin(userPrincipal.getUsername());

            JwtResponse jwtResponse = new JwtResponse(
                    jwt,
                    userPrincipal.getId(),
                    userPrincipal.getUsername(),
                    userPrincipal.getRoleNames());

            logger.info("User {} authenticated successfully", userPrincipal.getUsername());
            return ResponseEntity.ok(jwtResponse);
        } catch (Exception e) {
            logger.error("Authentication failed for user {}: {}", loginRequest.getUsername(), e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Authentication failed: " + e.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            User user = userService.registerUser(registerRequest);

            logger.info("User {} registered successfully", user.getUsername());
            return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
        } catch (RuntimeException e) {
            logger.error("Registration failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(e.getMessage()));
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validateToken() {
        // If this endpoint is reached, the token is valid
        // Spring Security would have already validated it through the filter
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            User user = (User) authentication.getPrincipal();

            // Return user information for frontend
            JwtResponse response = new JwtResponse(
                    null, // Don't return token in validation response
                    user.getId(),
                    user.getUsername(),
                    user.getRoleNames());

            return ResponseEntity.ok(response);
        }

        return ResponseEntity.badRequest()
                .body(new MessageResponse("Token validation failed"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(new MessageResponse("Logout successful"));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            User user = (User) authentication.getPrincipal();

            JwtResponse response = new JwtResponse(
                    null,
                    user.getId(),
                    user.getUsername(),
                    user.getRoleNames());

            return ResponseEntity.ok(response);
        }

        return ResponseEntity.badRequest()
                .body(new MessageResponse("User not authenticated"));
    }
}

