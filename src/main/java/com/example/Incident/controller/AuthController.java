package com.example.Incident.controller;

import com.example.Incident.model.AuthRequest;
import com.example.Incident.model.User;
import com.example.Incident.repo.UserRepository;
import com.example.Incident.services.jwt.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody AuthRequest authRequest) throws AuthenticationException {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
        );
        String token = jwtUtil.generateToken(authRequest.getUsername());
        return ResponseEntity.ok(Map.of("token", token));
    }


    @PostMapping("/signup")
    public ResponseEntity<Map<String, String>> signup(@RequestBody AuthRequest authRequest) {
        // Check if username already exists
        if (userRepository.findByUsername(authRequest.getUsername()).isPresent()) {
            return new ResponseEntity<>(Map.of("message", "Username already exists"), HttpStatus.CONFLICT);
        }

        // Check if passwords match
        if (!authRequest.getPassword().equals(authRequest.getConfirmPassword())) {
            return new ResponseEntity<>(Map.of("message", "Passwords do not match"), HttpStatus.BAD_REQUEST);
        }

        // Create new user
        User user = new User();
        user.setUsername(authRequest.getUsername());
        user.setPassword(passwordEncoder.encode(authRequest.getPassword()));
        user.setRole("USER"); // Set default role
        user.setFullName(authRequest.getFullName()); // Set full name
        user.setJobTitle(authRequest.getJobTitle()); // Set job title

        userRepository.save(user);

        return new ResponseEntity<>(Map.of("message", "User registered successfully"), HttpStatus.CREATED);
    }



}
