package com.example.Incident.controller;

import com.example.Incident.model.Incident;
import com.example.Incident.services.IncidentService;
import com.example.Incident.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/incidents")
public class IncidentController {
    @Autowired
    private IncidentService incidentService;
    @Autowired
    private NotificationService notificationService;

    @PostMapping("/create")
    public ResponseEntity<Incident> createIncident(@RequestBody Incident incident) {
        Incident createdIncident = incidentService.createIncident(incident);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdIncident);

    }

    @GetMapping("/list/all")
    public ResponseEntity<List<Incident>> getAllIncidents() {
        List<Incident> incidents = incidentService.getAllIncidents();
        return ResponseEntity.ok(incidents);

    }

    @GetMapping("/find/{id}")
    public ResponseEntity<Incident> getIncidentById(@PathVariable Long id) {
        Incident incident = incidentService.getIncidentById(id);
        return ResponseEntity.ok(incident);
    }

    @PostMapping("/{id}/escalate")
    public ResponseEntity<Incident> escalateIncident(@PathVariable Long id, @RequestBody String escalatedTo) {
        Incident updatedIncident = incidentService.escalateIncident(id, escalatedTo);
//        notificationService.sendEscalationNotification(escalatedTo, updatedIncident); // Send email notification
        return ResponseEntity.ok(updatedIncident);
    }


    @DeleteMapping("/delete/all")
    public ResponseEntity<Void> deleteAllIncidents() {
        incidentService.deleteAllIncidents();
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteIncident(@PathVariable Long id) {
        incidentService.deleteIncident(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }


}

