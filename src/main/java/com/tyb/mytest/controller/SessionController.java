package com.tyb.mytest.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/session")
public class SessionController {

    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getSessionInfo(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        Map<String, Object> response = new HashMap<>();
        
        if (session != null) {
            response.put("sessionExists", true);
            response.put("sessionId", session.getId());
            response.put("creationTime", session.getCreationTime());
            response.put("lastAccessedTime", session.getLastAccessedTime());
            response.put("maxInactiveInterval", session.getMaxInactiveInterval());
            response.put("isNew", session.isNew());
            
            // Get session attributes
            Map<String, Object> attributes = new HashMap<>();
            Collections.list(session.getAttributeNames()).forEach(name -> {
                attributes.put(name, session.getAttribute(name));
            });
            response.put("attributes", attributes);
        } else {
            response.put("sessionExists", false);
        }
        
        if (authentication != null) {
            response.put("authenticated", authentication.isAuthenticated());
            response.put("principal", authentication.getName());
        } else {
            response.put("authenticated", false);
        }
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/attribute")
    public ResponseEntity<Map<String, String>> setSessionAttribute(
            @RequestParam String key, 
            @RequestParam String value,
            HttpServletRequest request) {
        
        HttpSession session = request.getSession(true);
        session.setAttribute(key, value);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Attribute set successfully");
        response.put("key", key);
        response.put("value", value);
        response.put("sessionId", session.getId());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/attribute/{key}")
    public ResponseEntity<Map<String, Object>> getSessionAttribute(
            @PathVariable String key,
            HttpServletRequest request) {
        
        HttpSession session = request.getSession(false);
        Map<String, Object> response = new HashMap<>();
        
        if (session != null) {
            Object value = session.getAttribute(key);
            response.put("key", key);
            response.put("value", value);
            response.put("exists", value != null);
            response.put("sessionId", session.getId());
        } else {
            response.put("error", "No active session");
            response.put("exists", false);
        }
        
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/attribute/{key}")
    public ResponseEntity<Map<String, String>> removeSessionAttribute(
            @PathVariable String key,
            HttpServletRequest request) {
        
        HttpSession session = request.getSession(false);
        
        if (session != null) {
            session.removeAttribute(key);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Attribute removed successfully");
            response.put("key", key);
            response.put("sessionId", session.getId());
            
            return ResponseEntity.ok(response);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("error", "No active session");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/invalidate")
    public ResponseEntity<Map<String, String>> invalidateSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        
        if (session != null) {
            String sessionId = session.getId();
            session.invalidate();
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Session invalidated successfully");
            response.put("sessionId", sessionId);
            
            return ResponseEntity.ok(response);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("message", "No active session to invalidate");
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/create")
    public ResponseEntity<Map<String, Object>> createSession(HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Session created");
        response.put("sessionId", session.getId());
        response.put("isNew", session.isNew());
        response.put("creationTime", session.getCreationTime());
        response.put("maxInactiveInterval", session.getMaxInactiveInterval());
        
        return ResponseEntity.ok(response);
    }
}