package com.tradeflow.auth_server.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = { "http://localhost:3000", "http://127.0.0.1:3000" })
public class TestController {

    @GetMapping("/public")
    public String publicEndpoint() {
        return "This is a public endpoint - no authentication required";
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('VIEWER') or hasRole('STAFF') or hasRole('MANAGER') or hasRole('ADMIN')")
    public String userEndpoint() {
        return "This endpoint requires authentication - any role can access";
    }

    @GetMapping("/staff")
    @PreAuthorize("hasRole('STAFF') or hasRole('MANAGER') or hasRole('ADMIN')")
    public String staffEndpoint() {
        return "This endpoint requires STAFF, MANAGER, or ADMIN role";
    }

    @GetMapping("/manager")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public String managerEndpoint() {
        return "This endpoint requires MANAGER or ADMIN role";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminEndpoint() {
        return "This endpoint requires ADMIN role only";
    }
}
