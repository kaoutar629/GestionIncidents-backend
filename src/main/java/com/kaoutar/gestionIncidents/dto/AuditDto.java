package com.kaoutar.gestionIncidents.dto;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class AuditDto {
    private Long id;
    private String action;
    private Long performedBy;
    private Long entityId;
    private String entityType;
    private LocalDateTime timestamp;
}
