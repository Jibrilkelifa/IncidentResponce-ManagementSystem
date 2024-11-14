package com.example.Incident.controller;

import com.example.Incident.model.IncidentEntry;
import com.example.Incident.model.SOCTable;
import com.example.Incident.services.NotificationService;
import com.example.Incident.services.SOCReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/soc")
public class SOCReportController {

    @Autowired
    private SOCReportService socReportService;

    @Autowired
    private NotificationService notificationService;

    // Endpoint to create a new SOC report
    @PostMapping("/report")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<com.example.Incident.dto.SOCTableDTO> createSOCReport(@RequestBody com.example.Incident.dto.SOCTableDTO socTableDTO) {
        SOCTable socTable = convertToEntity(socTableDTO);
        SOCTable createdReport = socReportService.createSOCReport(socTable);
        com.example.Incident.dto.SOCTableDTO responseDTO = convertToDTO(createdReport);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @GetMapping("/download/{reportId}")
    public ResponseEntity<byte[]> downloadSOCReport(@PathVariable Long reportId) {
        // Fetch the report by ID
        SOCTable report = socReportService.getReportById(reportId);

        // Generate PDF from the report data
        byte[] pdfData = socReportService.generateSOCReportPDF(report);

        // Set headers to download the file as a PDF
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "SOC_Report.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfData);
    }
    @GetMapping("/reportId")
    public ResponseEntity<Long> getReportId(
            @RequestParam("reportDate") String reportDate,
            @RequestParam("shift") String shift) {
        Long reportId = socReportService.findReportIdByDateAndShift(reportDate, shift);
        return reportId != null
                ? ResponseEntity.ok(reportId)
                : ResponseEntity.notFound().build();
    }
    @PostMapping("/sendReport/{reportId}")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<String> sendSOCReportByEmail(@PathVariable Long reportId) {
        // Fetch the report by ID
        SOCTable report = socReportService.getReportById(reportId);

        // Generate the PDF
        byte[] pdfData = socReportService.generateSOCReportPDF(report);

        // Get the logged-in user's email
        String senderEmail = getLoggedInUserEmail();

        // List of manager and RIP emails
        List<String> managerEmails = List.of("Iyob.Hambisa@coopbanoromiasc.com", "Leti.Tadesse@coopbankoromiasc.com", "Jibril.Kalifa@coopbankoromiasc.com");

        // Send the email with the attachment to each manager email
        try {
            notificationService.sendReportEmail(senderEmail, managerEmails, "SOC Report", "Please find attached the SOC report.", pdfData);
            return ResponseEntity.ok("Report sent successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send report: " + e.getMessage());
        }
    }





    private SOCTable convertToEntity(com.example.Incident.dto.SOCTableDTO dto) {
        List<IncidentEntry> incidents = dto.getIncidents().stream()
                .map(incidentDTO -> new IncidentEntry(
                        null, // let JPA generate the ID
                        incidentDTO.getOffenceName(),
                        incidentDTO.getRootCause(),
                        incidentDTO.getAffectedAsset(),
                        incidentDTO.getIpAddress(),
                        incidentDTO.getRecommendedAction(),
                        null // we will set the report later
                ))
                .collect(Collectors.toList());

        return new SOCTable(null, LocalDate.now(), dto.getShift(), incidents);
    }

    private com.example.Incident.dto.SOCTableDTO convertToDTO(SOCTable report) {
        List<com.example.Incident.dto.IncidentDTO> incidentDTOs = report.getIncidents().stream()
                .map(incident -> new com.example.Incident.dto.IncidentDTO(
                        incident.getOffenceName(),
                        incident.getRootCause(),
                        incident.getAffectedAsset(),
                        incident.getIpAddress(),
                        incident.getRecommendedAction()))
                .collect(Collectors.toList());

        return new com.example.Incident.dto.SOCTableDTO(report.getReportDate().atStartOfDay(), report.getShift(), incidentDTOs);
    }
    private String getLoggedInUserEmail() {
        System.out.println(SecurityContextHolder.getContext().getAuthentication().getName());
        return SecurityContextHolder.getContext().getAuthentication().getName();

    }
}