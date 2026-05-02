package com.kaoutar.gestionIncidents.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Titre affiché dans la navbar (ex: "Nouvel incident : Panne serveur")
    private String title;

    // Message détaillé
    private String message;

    // Type: CREATED / STATUS_UPDATE
    private String type;

    // Priorité optionnelle pour l'affichage couleur
    private String priority;

    // Qui a créé l'incident (pour affichage "Par xxx")
    private String createdBy;

    @Column(name = "is_read")
    private boolean isRead = false;

    // Username (email) du destinataire
    private String username;

    private Long ticketId;

    // Nommé "at" pour correspondre au frontend (n.at)
    @Column(name = "created_at")
    private LocalDateTime at = LocalDateTime.now();
}
