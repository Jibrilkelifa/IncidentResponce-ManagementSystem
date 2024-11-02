package com.example.Incident.controller;

import com.example.Incident.model.Incident;
import com.example.Incident.model.Update;
import com.example.Incident.services.IncidentService;
import com.example.Incident.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
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
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<List<Incident>> getAllIncidents() {
        List<Incident> incidents = incidentService.getAllIncidents();
        return ResponseEntity.ok(incidents);

    }

    @GetMapping("/find/{id}")
    public ResponseEntity<Incident> getIncidentById(@PathVariable Long id) {
        Incident incident = incidentService.getIncidentById(id);
        return ResponseEntity.ok(incident);
    }

    @PutMapping("/{id}/escalate")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<Incident> escalateIncident(
            @PathVariable Long id,
            @RequestBody Incident escalationDetails) {
        Incident updatedIncident = incidentService.escalateIncident(id, escalationDetails);
        return ResponseEntity.ok(updatedIncident);
    }
    @GetMapping("/escalations")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<List<Incident>> getEscalatedIncidents() {
        // Fetch the email of the logged-in user
        String loggedInUserEmail = getLoggedInUserEmail();
        // Fetch escalated incidents assigned to this user
        List<Incident> escalatedIncidents = incidentService.getEscalatedIncidentsForUser(loggedInUserEmail);
        return ResponseEntity.ok(escalatedIncidents);
    }

    private String getLoggedInUserEmail() {
        System.out.println(SecurityContextHolder.getContext().getAuthentication().getName());
        return SecurityContextHolder.getContext().getAuthentication().getName();

    }



    @DeleteMapping("/delete/all")
    public ResponseEntity<Void> deleteAllIncidents() {
        incidentService.deleteAllIncidents();
        return ResponseEntity.noContent().build(); // 204 No Content
    }
    @PostMapping("/{id}/updates")
    public ResponseEntity<Update> addUpdate(@PathVariable Long id, @RequestBody Update update) {
        Update addedUpdate = incidentService.addUpdate(id, update);
        return ResponseEntity.status(HttpStatus.CREATED).body(addedUpdate);
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteIncident(@PathVariable Long id) {
        incidentService.deleteIncident(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }


}

