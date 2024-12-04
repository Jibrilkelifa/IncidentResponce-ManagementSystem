package com.example.Incident.controller;

import com.example.Incident.model.AuthRequest;
import com.example.Incident.model.ResetPasswordRequest;
import com.example.Incident.model.User;
import com.example.Incident.repo.UserRepository;
import com.example.Incident.services.NotificationService;
import com.example.Incident.services.jwt.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private  final NotificationService notificationService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserRepository userRepository, NotificationService notificationService, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
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
        user.setPhoneNumber(authRequest.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(authRequest.getPassword()));
        user.setRole("USER"); // Set default role
        user.setFullName(authRequest.getFullName()); // Set full name
        user.setJobTitle(authRequest.getJobTitle()); // Set job title

        userRepository.save(user);

        return new ResponseEntity<>(Map.of("message", "User registered successfully"), HttpStatus.CREATED);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        Optional<User> userOptional = userRepository.findByUsername(email);

        if (userOptional.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        User user = userOptional.get();
        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(15));
        userRepository.save(user);

        // Send reset password email with the reset token
        notificationService.sendResetPasswordEmail(email, "Password Reset Request", token);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Password reset link has been sent to your email.");
        return ResponseEntity.ok(response);
    }
    @GetMapping("/current-user")
   @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, String>> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        // If userDetails is null, return an error message
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "User not authenticated or token expired"));
        }

        Optional<User> user = userRepository.findByUsername(userDetails.getUsername());
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "User not found"));
        }

        return ResponseEntity.ok(Map.of("username", user.get().getUsername(), "fullName", user.get().getFullName()));
    }

    @GetMapping("/validate-token")
    public ResponseEntity<Map<String, String>> validateToken(@RequestHeader("Authorization") String authHeader) {
        // Extract token from the Authorization header
        String token = authHeader.replace("Bearer ", "");

        // Check if token is valid
        boolean isValid = jwtUtil.validateToken(token, jwtUtil.extractUsername(token));

        // Prepare response message
        Map<String, String> response = new HashMap<>();
        if (isValid) {
            response.put("message", "Token is valid");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Token is invalid or expired");
            return ResponseEntity.status(401).body(response);
        }
    }
    // Reset password
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        String token = request.getToken();
        String newPassword = request.getNewPassword();
        String confirmPassword = request.getConfirmPassword();

        // Check if the passwords match
        if (!newPassword.equals(confirmPassword)) {
            return ResponseEntity.badRequest().body("Passwords do not match.");
        }

        // Find the user by reset token
        Optional<User> userOptional = userRepository.findByResetToken(token);

        // Log the result of the token lookup
        if (userOptional.isEmpty()) {
            System.out.println("No user found with token: " + token);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid token.");
        }

        User user = userOptional.get();
        System.out.println("User found: " + user.getUsername());

        // Check if the token has expired
        if (user.getResetTokenExpiry() != null && user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token has expired.");
        }

        // Hash the new password before saving
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(hashedPassword);

        // Clear the reset token and expiry date
        user.setResetToken(null);
        user.setResetTokenExpiry(null);

        // Save the updated user
        userRepository.save(user);

        return ResponseEntity.ok("Password reset successfully.");
    }





}
