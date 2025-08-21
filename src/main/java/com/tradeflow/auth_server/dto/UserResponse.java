package com.tradeflow.auth_server.dto;

import com.tradeflow.auth_server.model.User;

import java.time.LocalDateTime;
import java.util.List;

public class UserResponse {

    private Long id;
    private String username;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastLogin;
    private boolean enabled;
    private List<String> roles;

    public UserResponse() {
    }

    public UserResponse(Long id, String username,LocalDateTime createdAt, LocalDateTime updatedAt,
                        LocalDateTime lastLogin, boolean enabled, List<String> roles) {
        this.id = id;
        this.username = username;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.lastLogin = lastLogin;
        this.enabled = enabled;
        this.roles = roles;
    }

    public UserResponse(User user) {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    @Override
    public String toString() {
        return "UserResponse{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", lastLogin=" + lastLogin +
                ", enabled=" + enabled +
                ", roles=" + roles +
                '}';
    }
}
