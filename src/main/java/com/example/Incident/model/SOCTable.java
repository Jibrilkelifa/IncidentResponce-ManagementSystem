package com.example.Incident.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SOCTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime reportDate;  // Date of the report
    private String shift;               // Shift (e.g., Shift 1, Shift 2)
    private String offenceName;         // Detected malicious activity
    private String rootCause;           // Root cause of the issue
    private String affectedAsset;       // Affected asset(s)
    private String ipAddress;           // IP address(es)
    private String recommendedAction;   // Recommended action
}