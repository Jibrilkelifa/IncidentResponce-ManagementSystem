package com.example.Incident.services;

import com.example.Incident.model.SOCTable;
import com.example.Incident.repo.SOCTableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SOCReportService {

    @Autowired
    private SOCTableRepository socTableRepository;

    public SOCReportService(SOCTableRepository socTableRepository) {
        this.socTableRepository = socTableRepository;
    }

    public SOCTable createSOCReport(SOCTable socTable) {
        socTable.setReportDate(LocalDateTime.now()); // Set the current date and time
        return socTableRepository.save(socTable); // Save and return the SOC report
    }

    // Method to fetch SOC reports by date and shift
    public List<SOCTable> fetchSOCReports(LocalDateTime date, String shift) {
        return socTableRepository.findByReportDateAndShift(date, shift);
    }

}
