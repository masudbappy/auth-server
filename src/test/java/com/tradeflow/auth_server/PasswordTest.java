package com.tradeflow.auth_server;

import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
public class PasswordTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void testAndFixPassword() {
        String rawPassword = "admin123";

        // Generate a NEW BCrypt hash for "admin123"
        String newCorrectHash = passwordEncoder.encode(rawPassword);
        System.out.println("NEW hash for 'admin123': " + newCorrectHash);

        // Test if the new hash works
        boolean newMatches = passwordEncoder.matches(rawPassword, newCorrectHash);
        System.out.println("New hash matches: " + newMatches); // Should be true

        // Test your current database hash
        String currentDBHash = "$2a$10$TQp.B8ZjK7FgOKv3DXpE5.DH9G4W1xJ0M.qkYLlV.F4S8BcJ5b5jS";
        boolean currentMatches = passwordEncoder.matches(rawPassword, currentDBHash);
        System.out.println("Current DB hash matches: " + currentMatches); // This is false
    }
}