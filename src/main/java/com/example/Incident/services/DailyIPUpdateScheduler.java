package com.example.Incident.services;

import com.example.Incident.model.IPReputation;
import com.example.Incident.repo.IPReputationRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DailyIPUpdateScheduler {

    private final IPReputationRepository ipReputationRepository;
    private final AbuseIPDBService abuseIPDBService;

    public DailyIPUpdateScheduler(IPReputationRepository ipReputationRepository, AbuseIPDBService abuseIPDBService) {
        this.ipReputationRepository = ipReputationRepository;
        this.abuseIPDBService = abuseIPDBService;
    }

    @Scheduled(cron = "0 03 19 * * ?") // Runs daily at 6:30 PM
    public void updateIPReputations() {
        System.out.println("Scheduler is running...");

        // Fetch all IP reputations from the database
        ipReputationRepository.findAll().forEach(reputation -> {
            try {
                // Update IP reputation using the AbuseIPDB service
                System.out.println("Updating reputation for IP: " + reputation.getIpAddress());
                abuseIPDBService.getIPReputation(reputation.getIpAddress());
            } catch (Exception e) {
                System.err.println("Failed to update reputation for IP: " + reputation.getIpAddress());
                e.printStackTrace();
            }
        });
    }
}