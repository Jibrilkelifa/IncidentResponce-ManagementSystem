package com.example.Incident.model;

import com.example.Incident.model.Update;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    @Column(columnDefinition = "TEXT")
    private String recommendedAction;

    private String title;
    private String status;
    private LocalDateTime createdAt;

    @ElementCollection
    @CollectionTable(name = "incident_escalated_to", joinColumns = @JoinColumn(name = "incident_id"))
    @Column(name = "escalated_to")
    private List<String> escalatedTo = new ArrayList<>(); // Initialize as an empty list

    private String severity;
    @ElementCollection
    @CollectionTable(name = "affected_systems", joinColumns = @JoinColumn(name = "incident_id"))
    @Column(name = "affectedSystem")
    private List<String> affectedSystems = new ArrayList<>();
    private String escalatedBy;
    private boolean escalated;

    @ElementCollection
    @CollectionTable(name = "incident_sources", joinColumns = @JoinColumn(name = "incident_id"))
    @Column(name = "source")
    private List<String> sources = new ArrayList<>();

    private String assignee;

    @ElementCollection
    @CollectionTable(name = "incident_escalated_to_emails", joinColumns = @JoinColumn(name = "incident_id"))
    @Column(name = "escalated_to_email")
    private List<String> escalatedToEmails = new ArrayList<>(); // Initialize as an empty list

    @ElementCollection
    @CollectionTable(name = "incident_escalated_to_phones", joinColumns = @JoinColumn(name = "incident_id"))
    @Column(name = "escalated_to_phone")
    private List<String> escalatedToPhones = new ArrayList<>(); // Initialize as an empty list

    @OneToMany(mappedBy = "incident", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference // Manage serialization for updates
    private List<Update> updates; // List of updates associated with the incident

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
