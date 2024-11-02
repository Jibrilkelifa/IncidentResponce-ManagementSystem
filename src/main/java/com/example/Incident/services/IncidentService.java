package com.example.Incident.services;
import com.example.Incident.model.Incident;
import com.example.Incident.model.Update;
import com.example.Incident.repo.IncidentRepository;
import com.example.Incident.repo.UpdateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
        incident.setSource(escalationDetails.getSource());
        incident.setStatus("Escalated");

//        notificationService.sendEscalationNotification(incident, escalationDetails.getEscalatedToEmail());
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

}
