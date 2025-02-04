package com.example.Incident.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "nmap_scans")
public class NmapScan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String target;  // IP or hostname

    @Column(name = "scan_type")
    private String scanType;  // quick, detailed, ports

    private String status;  // pending, running, completed

    @Lob
    private String result;  // Store scan output as text

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "finished_at")
    private LocalDateTime finishedAt;

    public NmapScan(String target, String scanType) {
        this.target = target;
        this.scanType = scanType;
        this.createdAt = LocalDateTime.now();
        this.status = "pending";
    }
}
