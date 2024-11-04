package com.example.Incident.controller;

import com.example.Incident.model.SOCTable;
import com.example.Incident.services.SOCReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/soc")
public class SOCReportController {

    @Autowired
    private SOCReportService socReportService;

    // Endpoint to create a new SOC report
    @PostMapping("/report")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<SOCTable> createSOCReport(@RequestBody SOCTable socTable) {
        SOCTable createdReport = socReportService.createSOCReport(socTable);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdReport);
    }

    // Endpoint to fetch SOC reports based on date and shift
    @GetMapping("/soc-reports")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<List<SOCTable>> getSOCReports(@RequestParam LocalDateTime date, @RequestParam String shift) {
        List<SOCTable> reports = socReportService.fetchSOCReports(date, shift);
        return ResponseEntity.ok(reports);
    }
}