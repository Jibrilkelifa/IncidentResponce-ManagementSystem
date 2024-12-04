package com.example.Incident.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SOCTableDTO {
    private LocalDateTime reportDate;
    private String shift;
    private List<com.example.Incident.dto.IncidentDTO> incidents;
}
