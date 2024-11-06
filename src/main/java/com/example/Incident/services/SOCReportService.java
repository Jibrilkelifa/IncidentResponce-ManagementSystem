package com.example.Incident.services;

import com.example.Incident.model.Incident;
import com.example.Incident.model.IncidentEntry;
import com.example.Incident.model.SOCTable;
import com.example.Incident.repo.SOCTableRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class SOCReportService {

    @Autowired
    private SOCTableRepository socTableRepository;

    public SOCReportService(SOCTableRepository socTableRepository) {
        this.socTableRepository = socTableRepository;
    }

    public SOCTable createSOCReport(SOCTable socTable) {
        socTable.setReportDate(LocalDateTime.now()); // Set the current date and time

        // Save and link each IncidentEntry to the report
        if (socTable.getIncidents() != null) {
            for (IncidentEntry incident : socTable.getIncidents()) {
                incident.setReport(socTable); // Link each incident to the report
            }
        }

        return socTableRepository.save(socTable); // Save and return the SOC report
    }

    public SOCTable getReportById(Long id) {
        Optional<SOCTable> report = socTableRepository.findById(id);
        if (report.isPresent()) {
            return report.get();
        } else {
            throw new RuntimeException("Report not found for ID: " + id);
        }
    }

    // Method to fetch SOC reports by date and shift
//    public List<SOCTable> fetchSOCReports(LocalDateTime date, String shift) {
//        return socTableRepository.findByReportDateAndShift(date, shift);
//    }


    public byte[] generateSOCReportPDF(SOCTable report) {
        Document document = new Document();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, outputStream);
            document.open();

            // Title and Header
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.BLACK);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.DARK_GRAY);
            Font tableHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.WHITE);
            Font tableBodyFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);

            Paragraph title = new Paragraph("SOC Daily Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            document.add(new Paragraph("Date: " + report.getReportDate().toLocalDate(), headerFont));
            document.add(new Paragraph("Shift: " + report.getShift(), headerFont));
            document.add(new Paragraph(" ")); // Spacer line

            // Table for Incidents
            PdfPTable table = new PdfPTable(5); // 5 columns
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            // Set column widths
            float[] columnWidths = {2f, 3f, 3f, 2f, 3f};
            table.setWidths(columnWidths);

            // Table Header
            PdfPCell cell;
            cell = new PdfPCell(new Phrase("Offense Name", tableHeaderFont));
            cell.setBackgroundColor(BaseColor.GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Root Cause", tableHeaderFont));
            cell.setBackgroundColor(BaseColor.GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Affected Asset", tableHeaderFont));
            cell.setBackgroundColor(BaseColor.GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("IP Address", tableHeaderFont));
            cell.setBackgroundColor(BaseColor.GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Recommended Action", tableHeaderFont));
            cell.setBackgroundColor(BaseColor.GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            // Table Body
            for (IncidentEntry incident : report.getIncidents()) {
                // Offense Name
                cell = new PdfPCell(new Phrase(incident.getOffenceName(), tableBodyFont));
                cell.setPadding(5);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);

                // Root Cause
                cell = new PdfPCell(new Phrase(incident.getRootCause(), tableBodyFont));
                cell.setPadding(5);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);

                // Affected Asset
                cell = new PdfPCell(new Phrase(incident.getAffectedAsset(), tableBodyFont));
                cell.setPadding(5);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);

                // IP Address
                cell = new PdfPCell(new Phrase(incident.getIpAddress(), tableBodyFont));
                cell.setPadding(5);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);

                // Recommended Action
                cell = new PdfPCell(new Phrase(incident.getRecommendedAction(), tableBodyFont));
                cell.setPadding(5);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);
            }

            // Add the table to the document
            document.add(table);

        } catch (DocumentException e) {
            e.printStackTrace();
        } finally {
            document.close();
        }

        return outputStream.toByteArray();
    }

    public Long findReportIdByDateAndShift(String reportDateTime, String shift) {
        // Parse the input date string as LocalDateTime
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
        LocalDateTime dateTime = LocalDateTime.parse(reportDateTime, formatter);

        // Fetch the report by date and shift type using LocalDateTime
        SOCTable report = socTableRepository.findByReportDateAndShift(dateTime, shift);

        return report != null ? report.getId() : null;
    }
}
