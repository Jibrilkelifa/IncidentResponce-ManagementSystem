package com.example.Incident.services;
import com.example.Incident.model.Incident;
import com.example.Incident.model.IncidentWithEscalatedTo;
import com.example.Incident.model.IncidentWithSource;
import com.example.Incident.model.Update;
import com.example.Incident.repo.IncidentRepository;
import com.example.Incident.repo.UpdateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
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
        incident.setStatus("Escalated");

        // Handle multiple users
        incident.setEscalatedTo(escalationDetails.getEscalatedTo());
        incident.setEscalationMessage(escalationDetails.getEscalationMessage());
        incident.setEscalatedToEmails(escalationDetails.getEscalatedToEmails());
        incident.setEscalatedToPhones(escalationDetails.getEscalatedToPhones());

        // Ensure there are emails and phones to send notifications to
        if (escalationDetails.getEscalatedToEmails() != null && !escalationDetails.getEscalatedToEmails().isEmpty() &&
                escalationDetails.getEscalatedToPhones() != null && !escalationDetails.getEscalatedToPhones().isEmpty()) {

            // Send notifications to each selected user
            for (int i = 0; i < escalationDetails.getEscalatedToEmails().size(); i++) {
                String email = escalationDetails.getEscalatedToEmails().get(i);
                String phone = escalationDetails.getEscalatedToPhones().get(i);

                // Ensure that both email and phone exist before sending notifications
                if (email != null && !email.isEmpty() && phone != null && !phone.isEmpty()) {
                    notificationService.sendEscalationSms(phone, incident);
                    notificationService.sendEscalationEmail(email, "Incident Escalation Notification", incident);
                }
            }
        } else {
            throw new RuntimeException("No valid users selected for escalation");
        }

        return incidentRepository.save(incident);
    }






    public void deleteIncident(Long id) {
        if (!incidentRepository.existsById(id)) {
            throw new RuntimeException("Incident not found");
        }
        incidentRepository.deleteById(id);
    }
    public List<Incident> getIncidentsByDateRange(LocalDate startDate, LocalDate endDate) {
        return incidentRepository.findByCreatedAtBetween(startDate.atStartOfDay(), endDate.atTime(23, 59, 59));
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
        // Fetch the incident from the repository
        Incident incident = incidentRepository.findById(incidentId)
                .orElseThrow(() -> new RuntimeException("Incident not found"));

        // Validate status transition
        if (update.getNewStatus() != null && !update.getNewStatus().isEmpty()) {
            incident.setStatus(update.getNewStatus()); // Update the incident's status
        }

        // Link update to the incident
        update.setIncident(incident);
        update.setTimestamp(LocalDateTime.now()); // Set the current time

        // Save the update and the incident
        incidentRepository.save(incident); // Save the updated incident first
        return updateRepository.save(update); // Then save the update
    }




    public void deleteAllIncidents() {
        incidentRepository.deleteAll();
    }


    public List<Incident> getEscalatedIncidentsForUser(String loggedInUserEmail) {
        // This method assumes you have the correct repository query for 'findByEscalatedToEmail'
        return incidentRepository.findByEscalatedToEmailsContaining(loggedInUserEmail);
    }

    public Map<String, Long> countIncidentsByAffectedSystem() {
        return incidentRepository.findAll().stream()
                .flatMap(incident -> incident.getAffectedSystems().stream()) // Flatten the list of affectedSystems
                .collect(Collectors.groupingBy(system -> system, Collectors.counting())); // Group by each affectedSystem
    }


//Get escalated incidents grouped by escalatedTo with count
public Map<String, List<Incident>> getEscalatedIncidentsGroupedByEscalatedTo() {
    return incidentRepository.findAll().stream()
            .filter(Incident::isEscalated) // Only include escalated incidents
            .flatMap(incident -> incident.getEscalatedTo().stream() // Flatten the list of escalatedTo
                    .map(escalatedTo -> new IncidentWithEscalatedTo(incident, escalatedTo)))
            .collect(Collectors.groupingBy(IncidentWithEscalatedTo::getEscalatedTo,
                    Collectors.mapping(IncidentWithEscalatedTo::getIncident, Collectors.toList())));
}

    // Get incidents grouped by each 'source' (for sources having more than one incident)
    public Map<String, List<Incident>> getIncidentsWithMultipleSources() {
        return incidentRepository.findAll().stream()
                .filter(incident -> incident.getSources() != null) // Exclude incidents with null sources
                .flatMap(incident -> incident.getSources().stream() // Flatten the sources list
                        .map(source -> new IncidentWithSource(incident, source)))
                .collect(Collectors.groupingBy(IncidentWithSource::getSource,
                        Collectors.mapping(IncidentWithSource::getIncident, Collectors.toList())))
                .entrySet().stream()
                .filter(entry -> entry.getValue().size() > 1) // Only include sources with more than one incident
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public List<Incident> searchIncidents(String searchTerm) {
        return incidentRepository.findByTitleContainingIgnoreCaseOrAssigneeContainingIgnoreCaseOrStatusContainingIgnoreCaseOrEscalatedToContainingIgnoreCaseOrSourcesContainingIgnoreCaseOrAffectedSystemsContainingIgnoreCase(
                searchTerm, searchTerm, searchTerm, searchTerm, searchTerm, searchTerm);
    }


    public Map<String, Integer> getIncidentTrendData() {
        List<Object[]> results = incidentRepository.groupIncidentsByDate();
        Map<String, Integer> trendData = new LinkedHashMap<>();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (Object[] result : results) {
            LocalDate date = LocalDate.parse(result[0].toString()); // Parse the date
            Integer count = ((Number) result[1]).intValue(); // Count as integer
            trendData.put(date.format(dateFormatter), count); // Format date as "YYYY-MM-DD"
        }
        return trendData;
    }

    public Incident saveIncident(Incident incident) {
        return incidentRepository.save(incident);
    }
}
