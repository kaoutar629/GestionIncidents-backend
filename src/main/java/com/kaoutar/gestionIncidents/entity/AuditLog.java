package com.kaoutar.gestionIncidents.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name="logs")
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String action;

    private Long entityId;

    private String entityType;

    private Long performedBy;

    private LocalDateTime timestamp = LocalDateTime.now();
}
