package com.example.Incident.services;
import com.example.Incident.model.Incident;
import com.example.Incident.repo.IncidentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class IncidentService {
    @Autowired
    private IncidentRepository incidentRepository;
//    @Autowired
//    private NotificationService notificationService;

    public Incident createIncident(Incident incident) {
        return incidentRepository.save(incident);
    }

    public List<Incident> getAllIncidents() {
        return incidentRepository.findAll();
    }
    public Incident escalateIncident(Long incidentId, String escalatedTo ) {
        Incident incident = incidentRepository.findById(incidentId)
                .orElseThrow();
        incident.setEscalated(true);
       incident.setEscalatedTo(escalatedTo);
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
    public void deleteAllIncidents() {
        incidentRepository.deleteAll();
    }



}
