package com.kaoutar.gestionIncidents.controller;

import com.kaoutar.gestionIncidents.entity.Notification;
import com.kaoutar.gestionIncidents.repository.NotificationRepository;
import com.kaoutar.gestionIncidents.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService    service;
    private final NotificationRepository repo;

    /** SSE stream — le frontend s'y abonne au démarrage */
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(Authentication auth) {
        SseEmitter emitter = new SseEmitter(0L); // pas de timeout
        service.register(auth.getName(), emitter);
        return emitter;
    }

    /** Historique des notifications de l'utilisateur connecté */
    @GetMapping
    public List<Notification> getAll(Authentication auth) {
        return repo.findByUsername(auth.getName());
    }

    /** Marquer une notification comme lue */
    @PatchMapping("/{id}/read")
    public void markRead(@PathVariable Long id) {
        Notification n = repo.findById(id).orElseThrow();
        n.setRead(true);
        repo.save(n);
    }

    /** Nombre de notifications non lues */
    @GetMapping("/unread")
    public int unread(Authentication auth) {
        return repo.findByUsernameAndIsReadFalse(auth.getName()).size();
    }
}
