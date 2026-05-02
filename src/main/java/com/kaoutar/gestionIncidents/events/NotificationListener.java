package com.kaoutar.gestionIncidents.events;

import com.kaoutar.gestionIncidents.entity.Notification;
import com.kaoutar.gestionIncidents.repository.NotificationRepository;
import com.kaoutar.gestionIncidents.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationListener {

    private final NotificationRepository repo;
    private final NotificationService    service;

    @Async
    @EventListener
    public void handleTicketEvent(IncidentEvent event) {

        Notification notif = new Notification();
        notif.setTitle(event.getMessage());
        notif.setMessage(event.getMessage());
        notif.setType(event.getType());
        notif.setUsername(event.getUsername());
        notif.setTicketId(event.getTicketId());
        notif.setPriority(event.getPriority());
        notif.setCreatedBy(event.getCreatedBy());

        repo.save(notif);

        // Push SSE live vers le destinataire
        service.push(event.getUsername(), notif);
    }
}
