package com.tyb.mytest.controller;

import com.tyb.mytest.model.User;
import com.tyb.mytest.service.JwtService;
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
    private JwtService jwtService;

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
                // Generate JWT token
                Map<String, Object> extraClaims = new HashMap<>();
                extraClaims.put("name", user.getName());
                extraClaims.put("email", user.getEmail());
                extraClaims.put("picture", user.getPicture());
                
                String jwtToken = jwtService.generateToken(user.getEmail(), extraClaims);
                
                // Create session
                HttpSession session = request.getSession(true);
                session.setAttribute("user", user);
                session.setAttribute("authenticated", true);
                
                Map<String, Object> responseData = new HashMap<>();
                responseData.put("message", "Authentication successful");
                responseData.put("user", user);
                responseData.put("token", jwtToken);
                responseData.put("sessionId", session.getId());
                
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
                if (session != null) {
                    response.put("sessionId", session.getId());
                }
                
                return ResponseEntity.ok(response);
            }
        }
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("authenticated", false);
        errorResponse.put("message", "User not authenticated");
        return ResponseEntity.unauthorized().body(errorResponse);
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
            
            User user = (User) session.getAttribute("user");
            if (user != null) {
                response.put("user", user);
                response.put("authenticated", session.getAttribute("authenticated"));
            }
        } else {
            response.put("message", "No active session");
        }
        
        return ResponseEntity.ok(response);
    }
}