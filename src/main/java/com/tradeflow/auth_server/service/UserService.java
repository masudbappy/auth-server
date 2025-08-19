package com.tradeflow.auth_server.service;

import com.tradeflow.auth_server.dto.RegisterRequest;
import com.tradeflow.auth_server.dto.UpdateRegisterRequest;
import com.tradeflow.auth_server.dto.UserResponse;
import com.tradeflow.auth_server.model.Role;
import com.tradeflow.auth_server.model.User;
import com.tradeflow.auth_server.repository.RoleRepository;
import com.tradeflow.auth_server.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerUser(RegisterRequest registerRequest) {
        // Check if username already exists
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new RuntimeException("Error: Username is already taken!");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Error: Email is already in use!");
        }

        // Create new user
        User user = new User(registerRequest.getUsername(),
                registerRequest.getEmail(),
                passwordEncoder.encode(registerRequest.getPassword()));

        user.setFullName(registerRequest.getFullName());
        user.setEnabled(registerRequest.isEnabled());

        if (registerRequest.getRoles() != null && !registerRequest.getRoles().isEmpty()) {
            Set<Role> roles = new HashSet<>();
            registerRequest.getRoles().forEach(roleName -> {
                Role role = getRoleByName(roleName.toLowerCase());
                role.setStatus(registerRequest.isStatus()); // Set role status
                roles.add(role);
            });
            user.setRoles(roles);
        }
        User savedUser = userRepository.save(user);

        logger.info("User registered successfully: {}", savedUser.getUsername());
        return savedUser;
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsernameWithRoles(username);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmailWithRoles(email);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToUserResponse)
                .collect(Collectors.toList());
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public void updateLastLogin(String username) {
        userRepository.findByUsername(username).ifPresent(user -> {
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);
        });
    }

    public User updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        user.setFirstName(userDetails.getFirstName());
        user.setLastName(userDetails.getLastName());
        user.setFullName(userDetails.getFullName());
        user.setEmail(userDetails.getEmail());

        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }

        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        userRepository.delete(user);
    }

    public User updateUserRoles(Long userId, Set<String> roleNames) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        Set<Role> roles = new HashSet<>();
        roleNames.forEach(roleName -> {
            Role role = roleRepository.findByName(roleName.toUpperCase())
                    .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
            roles.add(role);
        });

        user.setRoles(roles);
        return userRepository.save(user);
    }

    public UserResponse updateUserByAdmin(Long id, UpdateRegisterRequest updateRequest) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        // Update user details
        user.setFullName(updateRequest.getFullName());
        user.setUsername(updateRequest.getUsername());
        user.setEmail(updateRequest.getEmail());
        user.setEnabled(updateRequest.isEnabled()); // Update enabled status

        // Update roles if provided
        if (updateRequest.getRoles() != null && !updateRequest.getRoles().isEmpty()) {
            Set<Role> roles = new HashSet<>();
            updateRequest.getRoles().forEach(roleName -> {
                Role role = getRoleByName(roleName.toLowerCase());
                role.setStatus(updateRequest.isStatus()); // Set role status
                roles.add(role);
            });
            user.setRoles(roles);
        }

        User updatedUser = userRepository.save(user);
        logger.info("User updated by admin: {}", updatedUser.getUsername());
        return convertToUserResponse(updatedUser);
    }

    private Role getRoleByName(String roleName) {
        switch (roleName) {
            case "admin":
                return roleRepository.findByName("ADMIN")
                        .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            case "manager":
                return roleRepository.findByName("MANAGER")
                        .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            case "salesman":
                return roleRepository.findByName("SALESMAN")
                        .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            default:
                return roleRepository.findByName("VIEWER")
                        .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        }
    }

    public void resetPasswordByAdmin(Long id, String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        // Validate password is not empty
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new RuntimeException("Password cannot be empty");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        logger.info("Password reset by admin for user: {}", user.getUsername());
    }

    public boolean isCurrentUser(String username, Long userId) {
        Optional<User> user = userRepository.findByUsername(username);
        return user.isPresent() && user.get().getId().equals(userId);
    }

    private UserResponse convertToUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getLastLogin(),
                user.isEnabled(),
                user.getRoleNames());
    }
}
