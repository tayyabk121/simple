package com.tyb.mytest.service;

import com.tyb.mytest.model.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class UserService {

    // In-memory storage for demonstration. In production, use a database.
    private final Map<String, User> users = new ConcurrentHashMap<>();
    private final Map<Long, User> usersById = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public User saveOrUpdateUser(User user) {
        User existingUser = findByEmail(user.getEmail());
        
        if (existingUser != null) {
            // Update existing user
            existingUser.setName(user.getName());
            existingUser.setPicture(user.getPicture());
            existingUser.setEmailVerified(user.getEmailVerified());
            existingUser.setProvider(user.getProvider());
            existingUser.setEnabled(user.getEnabled());
            existingUser.setUpdatedAt(LocalDateTime.now());
            
            if (user.getGoogleId() != null) {
                existingUser.setGoogleId(user.getGoogleId());
            }
            
            return existingUser;
        } else {
            // Create new user
            user.setId(idGenerator.getAndIncrement());
            users.put(user.getEmail(), user);
            usersById.put(user.getId(), user);
            return user;
        }
    }

    public User findByEmail(String email) {
        return users.get(email);
    }

    public User findById(Long id) {
        return usersById.get(id);
    }

    public User findByGoogleId(String googleId) {
        return users.values().stream()
                .filter(user -> googleId.equals(user.getGoogleId()))
                .findFirst()
                .orElse(null);
    }

    public boolean existsByEmail(String email) {
        return users.containsKey(email);
    }

    public void deleteUser(String email) {
        User user = users.remove(email);
        if (user != null) {
            usersById.remove(user.getId());
        }
    }

    public Map<String, User> getAllUsers() {
        return users;
    }
}