package com.example.Incident.model;

import com.fasterxml.jackson.annotation.JsonBackReference; // Import Jackson annotation
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Update {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message; // Message describing the update
    private LocalDateTime timestamp; // Time of the update

    @ManyToOne
    @JoinColumn(name = "incident_id", nullable = false)
    @JsonBackReference // Prevent serialization of the incident reference
    private Incident incident; // Link to the associated incident

    private String newStatus;
}
