package com.example.Incident.model;

public class IncidentWithSource {
    private Incident incident;
    private String source;

    public IncidentWithSource(Incident incident, String source) {
        this.incident = incident;
        this.source = source;
    }

    public Incident getIncident() {
        return incident;
    }

    public String getSource() {
        return source;
    }
}
