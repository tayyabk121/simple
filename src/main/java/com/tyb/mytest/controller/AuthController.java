package com.tyb.mytest.controller;

import com.tyb.mytest.model.User;
import com.tyb.mytest.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public ResponseEntity<Map<String, String>> login() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Please authenticate with Google OAuth2");
        response.put("googleLoginUrl", "/oauth2/authorization/google");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/success")
    public ResponseEntity<Map<String, Object>> loginSuccess(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.getPrincipal() instanceof OAuth2User) {
            OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
            String email = oauth2User.getAttribute("email");
            String name = oauth2User.getAttribute("name");
            
            // Find user in our system
            User user = userService.findByEmail(email);
            
            if (user != null) {
                // Create session and store user information
                HttpSession session = request.getSession(true);
                session.setAttribute("user", user);
                session.setAttribute("authenticated", true);
                session.setAttribute("email", user.getEmail());
                session.setAttribute("name", user.getName());
                session.setAttribute("picture", user.getPicture());
                
                // Set session timeout (30 minutes)
                session.setMaxInactiveInterval(1800);
                
                Map<String, Object> responseData = new HashMap<>();
                responseData.put("message", "Authentication successful");
                responseData.put("user", user);
                responseData.put("sessionId", session.getId());
                responseData.put("maxInactiveInterval", session.getMaxInactiveInterval());
                
                return ResponseEntity.ok(responseData);
            }
        }
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "Authentication failed");
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @GetMapping("/failure")
    public ResponseEntity<Map<String, String>> loginFailure() {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Authentication failed");
        response.put("message", "Google OAuth2 authentication was unsuccessful");
        return ResponseEntity.badRequest().body(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpServletRequest request, HttpServletResponse response) {
        try {
            // Invalidate session
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            
            // Clear security context
            SecurityContextHolder.clearContext();
            
            Map<String, String> responseData = new HashMap<>();
            responseData.put("message", "Logout successful");
            return ResponseEntity.ok(responseData);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Logout failed");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/user")
    public ResponseEntity<Map<String, Object>> getCurrentUser(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            User user = userService.findByEmail(email);
            
            if (user != null) {
                HttpSession session = request.getSession(false);
                
                Map<String, Object> response = new HashMap<>();
                response.put("user", user);
                response.put("authenticated", true);
                response.put("authenticationMethod", "session");
                
                if (session != null) {
                    response.put("sessionId", session.getId());
                    response.put("sessionValid", true);
                    response.put("maxInactiveInterval", session.getMaxInactiveInterval());
                    response.put("lastAccessedTime", session.getLastAccessedTime());
                } else {
                    response.put("sessionValid", false);
                }
                
                return ResponseEntity.ok(response);
            }
        }
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("authenticated", false);
        errorResponse.put("message", "User not authenticated");
        return ResponseEntity.status(401).body(errorResponse);
    }

    @GetMapping("/session")
    public ResponseEntity<Map<String, Object>> getSessionInfo(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        
        Map<String, Object> response = new HashMap<>();
        
        if (session != null) {
            response.put("sessionId", session.getId());
            response.put("creationTime", session.getCreationTime());
            response.put("lastAccessedTime", session.getLastAccessedTime());
            response.put("maxInactiveInterval", session.getMaxInactiveInterval());
            response.put("isNew", session.isNew());
            response.put("valid", true);
            
            User user = (User) session.getAttribute("user");
            if (user != null) {
                response.put("user", user);
                response.put("authenticated", session.getAttribute("authenticated"));
            }
        } else {
            response.put("valid", false);
            response.put("message", "No active session");
        }
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        Map<String, Object> response = new HashMap<>();
        
        boolean isAuthenticated = authentication != null && 
                                authentication.isAuthenticated() && 
                                !"anonymousUser".equals(authentication.getName());
        
        boolean hasValidSession = session != null && 
                                session.getAttribute("authenticated") != null &&
                                (Boolean) session.getAttribute("authenticated");
        
        response.put("authenticated", isAuthenticated);
        response.put("hasValidSession", hasValidSession);
        response.put("authenticationMethod", "session");
        
        if (isAuthenticated && hasValidSession) {
            User user = (User) session.getAttribute("user");
            if (user != null) {
                response.put("user", user);
            }
            response.put("sessionId", session.getId());
        }
        
        return ResponseEntity.ok(response);
    }
}