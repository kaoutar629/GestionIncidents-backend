package com.kaoutar.gestionIncidents.service;

import com.kaoutar.gestionIncidents.entity.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.*;
import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
public class NotificationService {

    // Clé = email de l'utilisateur connecté
    private final Map<String, List<SseEmitter>> emitters = new ConcurrentHashMap<>();

    public void register(String username, SseEmitter emitter) {
        emitters.computeIfAbsent(username, k -> new CopyOnWriteArrayList<>())
                .add(emitter);
        emitter.onCompletion(() -> remove(username, emitter));
        emitter.onTimeout(()    -> remove(username, emitter));
        emitter.onError((e)     -> remove(username, emitter));
    }

    private void remove(String username, SseEmitter emitter) {
        List<SseEmitter> list = emitters.get(username);
        if (list != null) list.remove(emitter);
    }

    /**
     * Pousse la notification vers l'utilisateur identifié par son email.
     * Chaque destinataire est maintenant ciblé explicitement (plus de magic string "ADMIN").
     */
    public void push(String recipientEmail, Notification notif) {
        List<SseEmitter> list = emitters.get(recipientEmail);
        if (list != null) send(list, notif);
    }

    private void send(List<SseEmitter> list, Notification notif) {
        for (SseEmitter emitter : new ArrayList<>(list)) {
            try {
                emitter.send(SseEmitter.event().name("notification").data(notif));
            } catch (Exception e) {
                emitter.complete();
                list.remove(emitter);
            }
        }
    }
}
