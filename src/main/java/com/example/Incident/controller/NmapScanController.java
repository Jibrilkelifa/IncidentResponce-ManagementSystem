package com.example.Incident.controller;

import com.example.Incident.model.NmapScan;
import com.example.Incident.services.NmapScanService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/nmap")
public class NmapScanController {

    private final NmapScanService nmapScanService;

    public NmapScanController(NmapScanService nmapScanService) {
        this.nmapScanService = nmapScanService;
    }

    @PostMapping("/start")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<NmapScan> startScan(
            @RequestParam String target,
            @RequestParam String scanType,
            @RequestParam(required = false) String zombieHost) {

        if (scanType.equalsIgnoreCase("idle_scan") && (zombieHost == null || zombieHost.isEmpty())) {
            return ResponseEntity.badRequest().body(null); // Zombie host is required for idle scans
        }

        NmapScan scan = nmapScanService.startScan(target, scanType, zombieHost);
        return ResponseEntity.ok(scan);
    }

    @GetMapping("/scans")
    public ResponseEntity<List<NmapScan>> getAllScans() {
        return ResponseEntity.ok(nmapScanService.getAllScans());
    }

    @GetMapping("/scan/{id}")
    public ResponseEntity<?> getScanById(@PathVariable Long id) {
        Optional<NmapScan> scan = nmapScanService.getScanById(id);
        return scan.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

}