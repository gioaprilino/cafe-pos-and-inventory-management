package com.terracafe.terracafe_backend.controller;

import com.terracafe.terracafe_backend.dto.LoginRequest;
import com.terracafe.terracafe_backend.dto.LoginResponse;
import com.terracafe.terracafe_backend.model.User;
import com.terracafe.terracafe_backend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users") // Base path untuk endpoint user
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('MANAGER')")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER') or #id == authentication.principal.id")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        if (user.getUsername() == null || user.getUsername().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        if (user.getPasswordHash() == null || user.getPasswordHash().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        if (user.getRole() == null) {
            return ResponseEntity.badRequest().build();
        }
        try {
            User savedUser = userService.saveUser(user);
            return ResponseEntity.ok(savedUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        Optional<User> existingUser = userService.getUserById(id);
        if (existingUser.isPresent()) {
            User updatedUser = existingUser.get();
            
            // Update hanya field yang tidak null
            if (userDetails.getUsername() != null) {
                if (userDetails.getUsername().isBlank()) {
                    return ResponseEntity.badRequest().build(); // Reject empty username
                }
                updatedUser.setUsername(userDetails.getUsername());
            }
            if (userDetails.getPasswordHash() != null) {
                if (userDetails.getPasswordHash().isBlank()) {
                    return ResponseEntity.badRequest().build(); // Reject empty password
                }
                updatedUser.setPasswordHash(userDetails.getPasswordHash());
            }
            if (userDetails.getRole() != null) {
                updatedUser.setRole(userDetails.getRole());
            }
            
            try {
                return ResponseEntity.ok(userService.saveUser(updatedUser));
            } catch (Exception e) {
                return ResponseEntity.badRequest().build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        Optional<User> existingUser = userService.getUserById(id);
        if (existingUser.isPresent()) {
            userService.deleteUser(id);
            return ResponseEntity.ok("User deleted successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        Optional<User> userOpt = userService.getUserByUsername(loginRequest.getUsername());
        
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401)
                    .body(new LoginResponse(null, null, null, null, "User not found"));
        }

        User user = userOpt.get();
        if (!userService.authenticate(loginRequest.getUsername(), loginRequest.getPassword())) {
            return ResponseEntity.status(401)
                    .body(new LoginResponse(null, null, null, null, "Invalid credentials"));
        }

        // Generate JWT token
        String token = userService.generateJwtToken(user);

        LoginResponse response = new LoginResponse(
            user.getId(),
            user.getUsername(),
            user.getRole().getId().toString(),
            user.getRole().getName(),
            token,
            "Login successful"
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpServletRequest request) {
        // Extract token dari Authorization header
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            try {
                username = userService.extractUsernameFromToken(token);
            } catch (Exception e) {
                // Token tidak valid, tapi tetap return success untuk logout
                Map<String, String> response = new HashMap<>();
                response.put("message", "Logout successful");
                response.put("status", "success");
                return ResponseEntity.ok(response);
            }
        }
        
        // Panggil service untuk logout (bisa diperluas untuk blacklist token)
        boolean logoutSuccess = userService.logout(token, username);
        
        Map<String, String> response = new HashMap<>();
        if (logoutSuccess) {
            response.put("message", "Logout successful");
            response.put("status", "success");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Logout failed");
            response.put("status", "error");
            return ResponseEntity.status(400).body(response);
        }
    }
}
