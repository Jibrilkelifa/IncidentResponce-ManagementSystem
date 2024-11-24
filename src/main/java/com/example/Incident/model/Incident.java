package com.example.Incident.model;

import com.fasterxml.jackson.annotation.JsonManagedReference; // Import Jackson annotation
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Incident {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "TEXT")
    private String description;
    private String title;
    private String status;
    private LocalDateTime createdAt;
    private String escalatedTo;
    private String severity;
    private String affectedSystem;
    private String escalatedBy;
    private boolean escalated;
    private String source;
    private String assignee;
    private String escalatedToEmail;
    private String escalatedToPhoneNumber;
    @Column(columnDefinition = "TEXT")
    private String recommendedAction;

    @OneToMany(mappedBy = "incident", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference // Manage serialization for updates
    private List<Update> updates; // List of updates associated with the incident
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
