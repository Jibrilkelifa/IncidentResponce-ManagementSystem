package com.example.Incident.services;

import com.example.Incident.model.NmapScan;
import com.example.Incident.repo.NmapScanRepository;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NmapScanService {

    private final NmapScanRepository nmapScanRepository;

    public NmapScanService(NmapScanRepository nmapScanRepository) {
        this.nmapScanRepository = nmapScanRepository;
    }

    public NmapScan startScan(String target, String scanType) {
        NmapScan scan = new NmapScan(target, scanType);
        scan.setStatus("running");
        scan = nmapScanRepository.save(scan);

        final Long scanId = scan.getId();

        // Run Nmap scan asynchronously (to avoid blocking API call)
        new Thread(() -> executeNmapScan(scanId, target, scanType)).start();

        return scan;
    }

    private void executeNmapScan(Long scanId, String target, String scanType) {
        Optional<NmapScan> scanOpt = nmapScanRepository.findById(scanId);
        if (scanOpt.isEmpty()) return;

        NmapScan scan = scanOpt.get();
        String command = buildNmapCommand(target, scanType);

        try {
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            process.waitFor();

            scan.setResult(output.toString());
            scan.setStatus("completed");
            scan.setFinishedAt(LocalDateTime.now());
        } catch (Exception e) {
            scan.setResult("Error: " + e.getMessage());
            scan.setStatus("failed");
        }

        nmapScanRepository.save(scan);
    }

    private String buildNmapCommand(String target, String scanType) {
        return switch (scanType.toLowerCase()) {
            case "quick" -> "nmap -F " + target;
            case "detailed" -> "nmap -A " + target;
            case "ports" -> "nmap -p- " + target;
            default -> "nmap " + target;
        };
    }

    public List<NmapScan> getAllScans() {
        return nmapScanRepository.findAll();
    }

    public Optional<NmapScan> getScanById(Long id) {
        return nmapScanRepository.findById(id);
    }
}
