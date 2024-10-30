package com.example.Incident.services;

import com.example.Incident.model.User;
import com.example.Incident.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public User signup(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword())); // Encrypt the password
        return userRepository.save(user);
    }

    public User signin(String username, String password) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return user; // Return user if password matches
        }
        return null; // Invalid credentials
    }
}

