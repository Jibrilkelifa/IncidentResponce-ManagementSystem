package com.example.Incident.model;

import lombok.Data;

import java.util.List;

@Data
public class ToolHealthCheckDTO {
    private String analystName;
    private String shiftTime;
    private List<ToolCheckItemDTO> items;
}

