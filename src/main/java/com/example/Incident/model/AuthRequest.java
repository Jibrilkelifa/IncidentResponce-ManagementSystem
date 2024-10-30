package com.example.Incident.model;

import lombok.Data;

@Data
public class AuthRequest {
    private String username;
    private String password;
    private String confirmPassword;  // New field for password confirmation
    private String fullName;         // New field for full name
    private String jobTitle;
}
