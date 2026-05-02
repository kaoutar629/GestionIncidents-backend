package com.kaoutar.gestionIncidents.exception;

public class IncidentNotFoundException extends RuntimeException{
    public IncidentNotFoundException(Long id) {
        super("Incident not found: " + id);
    }
    public IncidentNotFoundException(String message) {
        super(message);
    }
}
