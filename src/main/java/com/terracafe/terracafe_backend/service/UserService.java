package com.terracafe.terracafe_backend.service;

import com.terracafe.terracafe_backend.model.User;
import com.terracafe.terracafe_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

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
}
