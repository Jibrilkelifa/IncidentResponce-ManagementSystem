package com.example.Incident.services;

import com.example.Incident.model.GrafanaAlert;
import com.example.Incident.repo.GrafanaAlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GrafanaAlertService {
    private final GrafanaAlertRepository grafanaAlertRepository;

    // Get All Alerts
    public List<GrafanaAlert> getAllAlerts() {
        return grafanaAlertRepository.findAll();
    }

    // Save an alert (mock for now, real API later)
    public GrafanaAlert saveMockAlert() {
        GrafanaAlert alert = new GrafanaAlert();
        alert.setState("firing");
        alert.setAlertName("CPUHighUsage");
        alert.setSeverity("critical");
        alert.setDescription("CPU usage exceeded 90%");
        alert.setValue("92.5%");
        alert.setActiveAt(LocalDateTime.now().minusMinutes(10));
        return grafanaAlertRepository.save(alert);
    }
}
