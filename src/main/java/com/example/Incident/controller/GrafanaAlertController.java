package com.example.Incident.controller;

import com.example.Incident.model.GrafanaAlert;
import com.example.Incident.services.GrafanaAlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/grafana-alerts")
@RequiredArgsConstructor
public class GrafanaAlertController {
    private final GrafanaAlertService grafanaAlertService;

    @GetMapping("/all")
    public ResponseEntity<List<GrafanaAlert>> getAllAlerts() {
        List<GrafanaAlert> alerts = grafanaAlertService.getAllAlerts();
        return ResponseEntity.ok(alerts);
    }

    // TEMPORARY: Save a mock alert (until we get the real API)
    @PostMapping("/mock")
    public ResponseEntity<GrafanaAlert> saveMockAlert() {
        GrafanaAlert alert = grafanaAlertService.saveMockAlert();
        return ResponseEntity.ok(alert);
    }
}
