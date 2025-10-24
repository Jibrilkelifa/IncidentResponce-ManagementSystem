package com.example.Incident.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ToolCheckItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String toolName;       // e.g., SIEM, EDR, Firewall
    private String checkItem;      // e.g., "SIEM Device Availability"
    private String response;       // e.g., "Available", "5 disconnected, 10 sending logs"

    @ManyToOne
    @JoinColumn(name = "session_id")
    private ToolHealthCheckSession session;
}
