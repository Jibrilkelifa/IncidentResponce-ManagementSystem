package com.example.Incident.model;

public  class IncidentWithEscalatedTo {
    private Incident incident;
    private String escalatedTo;

    public IncidentWithEscalatedTo(Incident incident, String escalatedTo) {
        this.incident = incident;
        this.escalatedTo = escalatedTo;
    }

    public Incident getIncident() {
        return incident;
    }

    public String getEscalatedTo() {
        return escalatedTo;
    }
}
