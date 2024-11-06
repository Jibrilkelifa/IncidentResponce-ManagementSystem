package com.example.Incident.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IncidentDTO {
    private String offenceName;
    private String rootCause;
    private String affectedAsset;
    private String ipAddress;
    private String recommendedAction;
}
