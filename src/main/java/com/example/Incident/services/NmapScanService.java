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

    public NmapScan startScan(String target, String scanType, String zombieHost) {
        NmapScan scan = new NmapScan(target, scanType);
        scan.setStatus("running");
        scan = nmapScanRepository.save(scan);

        final Long scanId = scan.getId();

        // Run the Nmap scan asynchronously
        new Thread(() -> executeNmapScan(scanId, target, scanType, zombieHost)).start();

        return scan;
    }

    private void executeNmapScan(Long scanId, String target, String scanType, String zombieHost) {
        Optional<NmapScan> scanOpt = nmapScanRepository.findById(scanId);
        if (scanOpt.isEmpty()) return;

        NmapScan scan = scanOpt.get();
        String command = buildNmapCommand(target, scanType, zombieHost);

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

    private String buildNmapCommand(String target, String scanType, String zombieHost) {
        return switch (scanType.toLowerCase()) {
            case "ping_sweep" -> "nmap -sn " + target;
            case "tcp_connect" -> "nmap -sT " + target;
            case "syn_scan" -> "nmap -sS " + target;
            case "udp_scan" -> "nmap -sU " + target;
            case "service_version" -> "nmap -sV " + target;
            case "os_detection" -> "nmap -O " + target;
            case "idle_scan" -> "nmap -sI " + zombieHost + " " + target; // Requires zombie host

            // **New predefined scan types**
            case "intense_scan" -> "nmap -T4 -A -v " + target;
            case "intense_scan_udp" -> "nmap -sS -sU -T4 -A -v " + target;
            case "intense_all_tcp" -> "nmap -p 1-65535 -T4 -A -v " + target;
            case "intense_no_ping" -> "nmap -T4 -A -v -Pn " + target;
            case "ping_scan" -> "nmap -sn " + target;
            case "quick_scan" -> "nmap -T4 -F " + target;
            case "quick_scan_plus" -> "nmap -sV -T4 -O -F --version-light " + target;
            case "quick_traceroute" -> "nmap -sn --traceroute " + target;
            case "regular_scan" -> "nmap " + target;
            case "slow_comprehensive" -> "nmap -sS -sU -T4 -A -v -p 1-65535 " + target;
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