package com.kaoutar.gestionIncidents.enums;

public enum IncidentStatus {
    OPEN,
    IN_PROGRESS,
    RESOLVED,
    CLOSED;
    public boolean canTransitionTo(IncidentStatus next) {
        return switch (this) {
            case OPEN        -> next == IN_PROGRESS;
            case IN_PROGRESS -> next == RESOLVED || next == OPEN;
            case RESOLVED    -> next == CLOSED || next == IN_PROGRESS;
            case CLOSED      -> false; // terminal state
        };
    }
}
