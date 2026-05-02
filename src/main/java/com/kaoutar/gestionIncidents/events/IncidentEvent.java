package com.kaoutar.gestionIncidents.events;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class IncidentEvent {
    private Long   ticketId;
    private String username;    // destinataire (email)
    private String message;
    private String type;        // CREATED / STATUS_UPDATE
    private String priority;    // optionnel
    private String createdBy;   // email du créateur de l'incident

    // Constructeur raccourci sans priority/createdBy
    public IncidentEvent(Long ticketId, String username, String message, String type) {
        this(ticketId, username, message, type, null, null);
    }
}
