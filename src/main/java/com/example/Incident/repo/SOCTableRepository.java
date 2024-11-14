package com.example.Incident.repo;

import com.example.Incident.model.SOCTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SOCTableRepository extends JpaRepository<SOCTable, Long> {

    SOCTable findByReportDateAndShift(LocalDate reportDate, String shift);}