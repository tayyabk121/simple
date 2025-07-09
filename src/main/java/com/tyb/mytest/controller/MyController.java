package com.tyb.mytest.controller;

import com.tyb.mytest.model.User;
import com.tyb.mytest.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
public class MyController {

    @Autowired
    private UserService userService;

    @GetMapping("/greet")
    public ResponseEntity<Map<String, Object>> greet() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Hello from Sunduq API!");
        response.put("timestamp", LocalDateTime.now());
        
        if (authentication != null && authentication.isAuthenticated() 
            && !authentication.getName().equals("anonymousUser")) {
            User user = userService.findByEmail(authentication.getName());
            if (user != null) {
                response.put("personalizedMessage", "Hi " + user.getName() + "!");
                response.put("authenticated", true);
            }
        } else {
            response.put("authenticated", false);
        }
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/simple")
    public ResponseEntity<Map<String, String>> simple() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Simple endpoint response");
        response.put("status", "success");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/code")
    public ResponseEntity<Map<String, Object>> code() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Hello, I'm Tayyab Khan");
        response.put("developer", "Tayyab Khan");
        response.put("timestamp", LocalDateTime.now());
        
        if (authentication != null && authentication.isAuthenticated() 
            && !authentication.getName().equals("anonymousUser")) {
            response.put("userEmail", authentication.getName());
            response.put("authenticated", true);
        } else {
            response.put("authenticated", false);
        }
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.isAuthenticated() 
            && !authentication.getName().equals("anonymousUser")) {
            
            User user = userService.findByEmail(authentication.getName());
            if (user != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("user", user);
                response.put("authenticated", true);
                response.put("timestamp", LocalDateTime.now());
                return ResponseEntity.ok(response);
            }
        }
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "User not authenticated");
        errorResponse.put("authenticated", false);
        return ResponseEntity.status(401).body(errorResponse);
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.isAuthenticated() 
            && !authentication.getName().equals("anonymousUser")) {
            
            User user = userService.findByEmail(authentication.getName());
            HttpSession session = request.getSession(false);
            
            Map<String, Object> response = new HashMap<>();
            response.put("welcome", "Welcome to your dashboard, " + (user != null ? user.getName() : "User"));
            response.put("user", user);
            response.put("authenticated", true);
            response.put("timestamp", LocalDateTime.now());
            
            if (session != null) {
                response.put("sessionInfo", Map.of(
                    "sessionId", session.getId(),
                    "createdAt", session.getCreationTime(),
                    "lastAccessed", session.getLastAccessedTime()
                ));
            }
            
            // Dashboard stats
            response.put("stats", Map.of(
                "totalUsers", userService.getAllUsers().size(),
                "userProvider", user != null ? user.getProvider() : "unknown",
                "emailVerified", user != null ? user.getEmailVerified() : false
            ));
            
            return ResponseEntity.ok(response);
        }
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "Access denied. Please authenticate first.");
        errorResponse.put("authenticated", false);
        return ResponseEntity.status(401).body(errorResponse);
    }

    @GetMapping("/public/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now());
        response.put("service", "Sunduq API");
        response.put("version", "1.0.0");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/public/info")
    public ResponseEntity<Map<String, Object>> getApiInfo() {
        Map<String, Object> response = new HashMap<>();
        response.put("name", "Sunduq API");
        response.put("description", "Spring Boot application with Google OAuth2 authentication");
        response.put("version", "1.0.0");
        response.put("features", Map.of(
            "authentication", "Google OAuth2",
            "authorization", "Session based",
            "security", "Spring Security",
            "sessionManagement", "JDBC Sessions"
        ));
        response.put("endpoints", Map.of(
            "auth", "/auth/*",
            "public", "/public/*",
            "protected", "/* (requires authentication)"
        ));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/data")
    public ResponseEntity<Map<String, Object>> createData(@RequestBody Map<String, Object> requestData) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.isAuthenticated() 
            && !authentication.getName().equals("anonymousUser")) {
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Data created successfully");
            response.put("receivedData", requestData);
            response.put("createdBy", authentication.getName());
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(response);
        }
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "Authentication required to create data");
        return ResponseEntity.status(401).body(errorResponse);
    }

    @PutMapping("/api/data/{id}")
    public ResponseEntity<Map<String, Object>> updateData(@PathVariable String id, @RequestBody Map<String, Object> requestData) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.isAuthenticated() 
            && !authentication.getName().equals("anonymousUser")) {
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Data updated successfully");
            response.put("id", id);
            response.put("updatedData", requestData);
            response.put("updatedBy", authentication.getName());
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(response);
        }
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "Authentication required to update data");
        return ResponseEntity.status(401).body(errorResponse);
    }

    @DeleteMapping("/api/data/{id}")
    public ResponseEntity<Map<String, Object>> deleteData(@PathVariable String id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.isAuthenticated() 
            && !authentication.getName().equals("anonymousUser")) {
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Data deleted successfully");
            response.put("id", id);
            response.put("deletedBy", authentication.getName());
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(response);
        }
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "Authentication required to delete data");
        return ResponseEntity.status(401).body(errorResponse);
    }
}
