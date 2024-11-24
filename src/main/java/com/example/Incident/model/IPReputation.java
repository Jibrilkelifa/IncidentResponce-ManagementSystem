package com.example.Incident.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IPReputation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ipAddress;
    private int abuseConfidenceScore;
    private String categories; // Comma-separated categories
    private boolean malicious; // True if the score exceeds a threshold
    private LocalDateTime lastUpdated; // Timestamp of the last update

    @Column(columnDefinition = "TEXT")
    private String rawResponse; // Store full API response for auditing
}
