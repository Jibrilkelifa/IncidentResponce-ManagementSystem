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

    @Column(columnDefinition = "TEXT")
    private String rootCause;

    private String affectedAsset;
    private String ipAddress;

    @Column(columnDefinition = "TEXT")
    private String recommendedAction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "soc_report_id")
    private SOCTable report;
}
