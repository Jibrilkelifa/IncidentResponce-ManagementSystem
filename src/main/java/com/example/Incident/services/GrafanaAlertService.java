package com.example.Incident.services;

import com.example.Incident.model.Alert;
import com.example.Incident.repo.AlertRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class GrafanaAlertService {

    @Autowired
    private AlertRepository alertRepository;

    private final RestTemplate restTemplate;

    @Value("${grafana.api.url}")
    private String grafanaApiUrl;

    @Value("${grafana.api.token}")
    private String grafanaApiToken; // API token injected from properties

    public GrafanaAlertService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @Scheduled(fixedRate = 300000) // Runs every 5 minutes
    public void fetchAndSaveGrafanaAlerts() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + grafanaApiToken); // Add token to header
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    grafanaApiUrl,
                    HttpMethod.GET,
                    entity,
                    Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                // ✅ Fix: Ensure "alerts" is extracted correctly from "data"
                Map<String, Object> responseData = response.getBody();
                if (responseData != null && responseData.containsKey("data")) {
                    Map<String, Object> data = (Map<String, Object>) responseData.get("data");

                    if (data.containsKey("alerts")) {
                        List<Map<String, Object>> alerts = (List<Map<String, Object>>) data.get("alerts");

                        for (Map<String, Object> alert : alerts) {
                            Map<String, Object> labels = (Map<String, Object>) alert.get("labels");

                            String alertName = (String) labels.getOrDefault("alertname", "Unknown");
                            String host = (String) labels.getOrDefault("host", "Unknown");
                            String state = (String) alert.get("state");
                            String activeAt = (String) alert.get("activeAt");
                            String grafanaFolder = (String) labels.getOrDefault("grafana_folder", "Unknown");
                            String source = (String) labels.getOrDefault("Source", "Unknown");

                            // Check if alert already exists
                            Optional<Alert> existingAlert = alertRepository.findExistingAlert(alertName, host, activeAt);
                            if (existingAlert.isEmpty()) {
                                Alert newAlert = new Alert();
                                newAlert.setAlertName(alertName);
                                newAlert.setHost(host);
                                newAlert.setState(state);
                                newAlert.setActiveAt(activeAt);
                                newAlert.setGrafanaFolder(grafanaFolder);
                                newAlert.setSource(source);

                                alertRepository.save(newAlert);
                                System.out.println("Saved new alert: " + alertName);
                            }
                        }
                    } else {
                        System.out.println("No 'alerts' found in Grafana response.");
                    }
                } else {
                    System.err.println("Unexpected response format: 'data' field missing.");
                }
            } else {
                System.err.println("Failed to fetch alerts: " + response.getStatusCode());
            }
        } catch (ClassCastException e) {
            System.err.println("ClassCastException: " + e.getMessage());
        } catch (RestClientException e) {
            System.err.println("Error connecting to Grafana API: " + e.getMessage());
        }
        System.out.println("fetching alerts");
    }
}
