package com.example.Incident.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "grafana_alerts")
public class GrafanaAlert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String state;  // "firing" or "resolved"
    private String alertName;
    private String severity;
    private String description;
    private String value;
    private LocalDateTime activeAt;
    private LocalDateTime createdAt = LocalDateTime.now();
}
