package com.example.Incident.services;

import com.example.Incident.model.IPReputation;
import com.example.Incident.repo.IPReputationRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AbuseIPDBService {

    @Value("${abuseipdb.api.key}")
    private String apiKey;

    private final String BASE_URL = "https://api.abuseipdb.com/api/v2/check";

    private final IPReputationRepository ipReputationRepository;

    public AbuseIPDBService(IPReputationRepository ipReputationRepository) {
        this.ipReputationRepository = ipReputationRepository;
    }

    public IPReputation getIPReputation(String ipAddress) {
        // Check if the IP exists in the database
        Optional<IPReputation> existingReputation = ipReputationRepository.findByIpAddress(ipAddress);

        // Skip API call if the reputation was updated within the last 24 hours
        if (existingReputation.isPresent() && existingReputation.get().getLastUpdated().isAfter(LocalDateTime.now().minusHours(24))) {
            System.out.println("Using cached reputation data for IP: " + ipAddress);
            return existingReputation.get(); // Return cached data if it's fresh
        }

        // Query AbuseIPDB API
        System.out.println("Fetching data from AbuseIPDB for IP: " + ipAddress);

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Key", apiKey);
        headers.set("Accept", "application/json");

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        String url = BASE_URL + "?ipAddress=" + ipAddress + "&maxAgeInDays=90";

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);

            System.out.println("Response from AbuseIPDB for IP " + ipAddress + ": " + response.getBody());

            // Parse the response from AbuseIPDB
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response.getBody());

            IPReputation reputation = existingReputation.orElse(new IPReputation());
            reputation.setIpAddress(ipAddress);
            reputation.setAbuseConfidenceScore(rootNode.path("data").path("abuseConfidenceScore").asInt());
            reputation.setCategories(String.join(",", rootNode.path("data").path("categories").findValuesAsText("name")));
            reputation.setMalicious(reputation.getAbuseConfidenceScore() > 50);
            reputation.setLastUpdated(LocalDateTime.now());
            reputation.setRawResponse(response.getBody());

            System.out.println("Saving IP reputation for IP: " + ipAddress);
            return ipReputationRepository.save(reputation);
        } catch (Exception e) {
            System.err.println("Error fetching reputation for IP: " + ipAddress);
            e.printStackTrace();
            throw new RuntimeException("Error fetching reputation for IP: " + ipAddress, e);
        }
    }
}