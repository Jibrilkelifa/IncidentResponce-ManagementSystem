package com.example.Incident.controller;

import com.example.Incident.model.Alert;
import com.example.Incident.model.Incident;
import com.example.Incident.services.GrafanaAlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/alerts")
public class GrafanaAlertController {

    @Autowired
    private GrafanaAlertService grafanaAlertService;

    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<List<Alert>> getAllAlerts() {
        List<Alert> alerts = grafanaAlertService.getAlerts();
        return ResponseEntity.ok(alerts);

    }
    @GetMapping("/save")
    @PreAuthorize("hasAnyRole('USER')")

    public void fetchAlerts() {
        grafanaAlertService.fetchAndSaveGrafanaAlerts(); // Calling the service to fetch alerts
    }

}
