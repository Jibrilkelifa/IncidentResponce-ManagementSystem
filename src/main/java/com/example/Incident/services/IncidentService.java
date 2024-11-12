package com.example.Incident.services;
import com.example.Incident.model.Incident;
import com.example.Incident.model.Update;
import com.example.Incident.repo.IncidentRepository;
import com.example.Incident.repo.UpdateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class IncidentService {
    @Autowired
    private IncidentRepository incidentRepository;
    @Autowired
    private UpdateRepository updateRepository;
   @Autowired
   private NotificationService notificationService;

    public Incident createIncident(Incident incident) {
        return incidentRepository.save(incident);
    }

    public List<Incident> getAllIncidents() {
        return incidentRepository.findAll();
    }
    public Incident escalateIncident(Long id, Incident escalationDetails) {
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Incident not found"));

        incident.setEscalated(true);
        incident.setEscalatedTo(escalationDetails.getEscalatedTo());
        incident.setEscalatedToEmail(escalationDetails.getEscalatedToEmail());
        incident.setEscalatedToPhoneNumber(escalationDetails.getEscalatedToPhoneNumber());
        incident.setSource(escalationDetails.getSource());
        incident.setStatus("Escalated");

        // Send SMS with detailed message
        notificationService.sendEscalationSms(
                escalationDetails.getEscalatedToPhoneNumber(),
                incident
        );

        // Send email with detailed message
        notificationService.sendEscalationEmail(
                escalationDetails.getEscalatedToEmail(),
                "Incident Escalation Notification",
                incident
        );

        return incidentRepository.save(incident);
    }





    public void deleteIncident(Long id) {
        if (!incidentRepository.existsById(id)) {
            throw new RuntimeException("Incident not found");
        }
        incidentRepository.deleteById(id);
    }
    public Incident getIncidentById(Long id) {
        Optional<Incident> incident = incidentRepository.findById(id);
        if (incident.isPresent()) {
            return incident.get();
        } else {
            throw new RuntimeException("Incident not found for ID: " + id);
        }
    }
    public Update addUpdate(Long incidentId, Update update) {
        Incident incident = incidentRepository.findById(incidentId)
                .orElseThrow(() -> new RuntimeException("Incident not found"));

        // Update the status of the incident if a new status is provided
        if (update.getNewStatus() != null && !update.getNewStatus().isEmpty()) {
            incident.setStatus(update.getNewStatus());
        }

        // Link update to the incident
        update.setIncident(incident);
        update.setTimestamp(LocalDateTime.now()); // Set the current time

        // Save the update
        incidentRepository.save(incident); // Save the updated incident first
        return updateRepository.save(update); // Then save the update
    }


    public void deleteAllIncidents() {
        incidentRepository.deleteAll();
    }


    public List<Incident> getEscalatedIncidentsForUser(String loggedInUserEmail) {
        return incidentRepository.findByEscalatedToEmail(loggedInUserEmail);
    }
    public Map<String, Long> countIncidentsByAffectedSystem() {
        return incidentRepository.findAll().stream()
                .collect(Collectors.groupingBy(Incident::getAffectedSystem, Collectors.counting()));
    }

    // Get escalated incidents grouped by escalatedTo with count
    public Map<String, List<Incident>> getEscalatedIncidentsGroupedByEscalatedTo() {
        return incidentRepository.findAll().stream()
                .filter(Incident::isEscalated) // Only include escalated incidents
                .collect(Collectors.groupingBy(Incident::getEscalatedTo));
    }

    // Get incidents grouped by source with only sources having more than one incident
    public Map<String, List<Incident>> getIncidentsWithMultipleSources() {
        return incidentRepository.findAll().stream()
                .filter(incident -> incident.getSource() != null) // Exclude null sources
                .collect(Collectors.groupingBy(Incident::getSource))
                .entrySet().stream()
                .filter(entry -> entry.getValue().size() > 1) // Only sources with more than one incident
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

}
