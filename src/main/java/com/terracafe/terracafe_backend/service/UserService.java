package com.terracafe.terracafe_backend.service;

import com.terracafe.terracafe_backend.model.User;
import com.terracafe.terracafe_backend.repository.UserRepository;
import com.terracafe.terracafe_backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public User saveUser(User user) {
        // Encode password if it's not already encoded (starts with $2a$ or $2b$ for BCrypt)
        if (user.getPasswordHash() != null && 
            !user.getPasswordHash().startsWith("$2a$") && 
            !user.getPasswordHash().startsWith("$2b$") &&
            !user.getPasswordHash().startsWith("$2y$")) {
            user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        }
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // Authentication method
    public boolean authenticate(String username, String password) {
        Optional<User> userOpt = getUserByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            String storedPassword = user.getPasswordHash();
            
            // Jika password di database adalah plain text (tidak ter-encode), compare langsung
            if (!storedPassword.startsWith("$2a$") && 
                !storedPassword.startsWith("$2b$") &&
                !storedPassword.startsWith("$2y$")) {
                return password.equals(storedPassword);
            }
            
            // Jika password ter-encode dengan BCrypt, gunakan matches
            return passwordEncoder.matches(password, storedPassword);
        }
        return false;
    }

    // Generate JWT token for user
    public String generateJwtToken(User user) {
        return jwtUtil.generateToken(
            user.getUsername(),
            user.getRole().getName().toUpperCase(), // Uppercase untuk konsistensi dengan Spring Security
            user.getId()
        );
    }

    // Extract username dari token
    public String extractUsernameFromToken(String token) {
        return jwtUtil.extractUsername(token);
    }

    // Logout method - untuk implementasi stateless JWT
    public boolean logout(String token, String username) {
        // Untuk JWT stateless, logout dilakukan di client side dengan menghapus token
        // Method ini bisa diperluas untuk:
        // 1. Token blacklisting jika diperlukan
        // 2. Logging aktivitas logout
        // 3. Invalidasi refresh token jika ada
        
        if (token != null && username != null) {
            // Log aktivitas logout untuk audit trail
            System.out.println("User " + username + " logged out successfully");
            
            // TODO: Implementasi token blacklisting jika diperlukan
            // blacklistedTokens.add(token);
            
            return true;
        }
        
        // Jika token null atau username null, tetap anggap logout berhasil
        // karena tujuan logout adalah menghapus session/token
        return true;
    }
}
