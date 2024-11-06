package com.example.Incident.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IncidentEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String offenceName;
    private String rootCause;
    private String affectedAsset;
    private String ipAddress;
    private String recommendedAction;

    // Add a ManyToOne relationship to SOCTable
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "soc_report_id")
    private SOCTable report; // Reference to the associated SOC report
}