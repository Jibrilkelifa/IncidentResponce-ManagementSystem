package com.example.Incident.controller;

import com.example.Incident.model.Incident;
import com.example.Incident.model.Update;
import com.example.Incident.model.User;
import com.example.Incident.repo.UserRepository;
import com.example.Incident.services.DailyIPUpdateScheduler;
import com.example.Incident.services.IncidentService;
import com.example.Incident.services.NotificationService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/incidents")
public class IncidentController {
    @Autowired
    private IncidentService incidentService;

    @Autowired
    private DailyIPUpdateScheduler dailyIPUpdateScheduler;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private NotificationService notificationService;

    @PostMapping("/create")
    public ResponseEntity<Incident> createIncident(@RequestBody Incident incident) {
        Incident createdIncident = incidentService.createIncident(incident);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdIncident);

    }
    @GetMapping("/export")
    public ResponseEntity<?> exportIncidents(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        try {
            ByteArrayInputStream in = incidentService.exportIncidents(from, to);
            InputStreamResource resource = new InputStreamResource(in);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=incidents.xlsx")
                    .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(in.available()))
                    .contentType(MediaType.valueOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(resource);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred while exporting the report."));
        }
    }




    @PutMapping("/update/{id}")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<Incident> updateIncidentFields(
            @PathVariable Long id,
            @RequestBody Map<String, Object> updatedFields) {
        Incident incident = incidentService.getIncidentById(id);

        // Update specific fields
        if (updatedFields.containsKey("title")) {
            incident.setTitle((String) updatedFields.get("title"));
        }
        if (updatedFields.containsKey("recommendedAction")) {
            incident.setRecommendedAction((String) updatedFields.get("recommendedAction"));
        }
        if (updatedFields.containsKey("description")) {
            incident.setDescription((String) updatedFields.get("description"));
        }
        if (updatedFields.containsKey("affectedSystems")) {
            incident.setAffectedSystems((List<String>) updatedFields.get("affectedSystems"));
        }
        if (updatedFields.containsKey("sources")) {
            incident.setSources((List<String>) updatedFields.get("sources"));
        }
        if (updatedFields.containsKey("severity")) {
            incident.setSeverity((String) updatedFields.get("severity"));
        }

        // Save updated incident
        Incident updatedIncident = incidentService.saveIncident(incident);

        return ResponseEntity.ok(updatedIncident);
    }

    @GetMapping("/list/all")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<List<Incident>> getAllIncidents() {
        List<Incident> incidents = incidentService.getAllIncidents();
        return ResponseEntity.ok(incidents);

    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<List<Incident>> searchIncidents(@RequestParam(required = false) String searchTerm) {
        List<Incident> incidents;
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            incidents = incidentService.searchIncidents(searchTerm);
        } else {
            incidents = incidentService.getAllIncidents(); // Get all incidents if no search term
        }
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
    @GetMapping("/users")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
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

    @GetMapping("/count-by-affected-system")
    @PreAuthorize("hasAnyRole('USER')")
    public Map<String, Long> getCountByAffectedSystem() {
        return incidentService.countIncidentsByAffectedSystem();
    }

//Endpoint to get escalated incidents grouped by escalatedTo
    @GetMapping("/escalated-grouped-by-escalated-to")
    @PreAuthorize("hasAnyRole('USER')")
    public Map<String, List<Incident>> getEscalatedIncidentsGroupedByEscalatedTo() {
        return incidentService.getEscalatedIncidentsGroupedByEscalatedTo();
    }

    // Endpoint to get incidents with sources that have more than one incident
    @GetMapping("/grouped-by-source-multiple")
    @PreAuthorize("hasAnyRole('USER')")
    public Map<String, List<Incident>> getIncidentsWithMultipleSources() {
        return incidentService.getIncidentsWithMultipleSources();
    }
    @GetMapping("/update-ip-reputations")
    @PreAuthorize("hasAnyRole('USER')")
    public String triggerIPUpdate() {
        dailyIPUpdateScheduler.updateIPReputations();  // Call the method directly
        return "IP Reputations Update Triggered!";
    }
    // Inside IncidentController.java




    @GetMapping("/today")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<List<Incident>> getTodaysIncidents() {
        LocalDate today = LocalDate.now();
        List<Incident> incidents = incidentService.getIncidentsByDateRange(today, today);
        return ResponseEntity.ok(incidents);
    }

    @GetMapping("/week")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<List<Incident>> getThisWeeksIncidents() {
        LocalDate startOfWeek = LocalDate.now().with(DayOfWeek.MONDAY); // Get the start of the week
        LocalDate endOfWeek = LocalDate.now().with(DayOfWeek.SUNDAY);  // Get the end of the week
        List<Incident> incidents = incidentService.getIncidentsByDateRange(startOfWeek, endOfWeek);
        return ResponseEntity.ok(incidents);
    }

    @GetMapping("/month")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<List<Incident>> getThisMonthsIncidents() {
        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1); // Get the first day of the month
        LocalDate endOfMonth = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()); // Get the last day of the month
        List<Incident> incidents = incidentService.getIncidentsByDateRange(startOfMonth, endOfMonth);
        return ResponseEntity.ok(incidents);
    }

    @GetMapping("/metrics/summary")
    public Map<String, Long> getIncidentSummary() {
        Map<String, Long> metrics = new HashMap<>();
        metrics.put("totalIncidents", incidentService.getTotalIncidentsCount());
        metrics.put("resolvedIncidents", incidentService.getResolvedIncidentsCount());
        metrics.put("openIncidents", incidentService.getOpenIncidentsCount());
        metrics.put("criticalHighIncidents", incidentService.getCriticalHighIncidentsCount());
        return metrics;
    }

    @GetMapping("/metrics/total")
    public Map<String, Long> getTotalIncidents() {
        long count = incidentService.getTotalIncidentsCount();
        return Collections.singletonMap("totalIncidents", count);
    }

    @GetMapping("/metrics/resolved")
    public Map<String, Long> getResolvedIncidents() {
        long count = incidentService.getResolvedIncidentsCount();
        return Collections.singletonMap("resolvedIncidents", count);
    }
    @GetMapping("/metrics/trends")
    public ResponseEntity<Map<String, Integer>> getIncidentTrend() {
        Map<String, Integer> trendData = incidentService.getIncidentTrendData();
        return ResponseEntity.ok(trendData);
    }

    @GetMapping("/metrics/open")
    public Map<String, Long> getOpenIncidents() {
        long count = incidentService.getOpenIncidentsCount();
        return Collections.singletonMap("openIncidents", count);
    }

    @GetMapping("/metrics/critical-high")
    public Map<String, Long> getCriticalHighIncidents() {
        long count = incidentService.getCriticalHighIncidentsCount();
        return Collections.singletonMap("criticalHighIncidents", count);
    }







}

