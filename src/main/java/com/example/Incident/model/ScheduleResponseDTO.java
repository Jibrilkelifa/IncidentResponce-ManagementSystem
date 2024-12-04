package com.example.Incident.model;


import lombok.Data;

import java.time.LocalDate;

@Data
public class ScheduleResponseDTO {
    private Userr user;
    private Shift shift;
    private LocalDate date;

    // Constructors, getters, and setters
}
