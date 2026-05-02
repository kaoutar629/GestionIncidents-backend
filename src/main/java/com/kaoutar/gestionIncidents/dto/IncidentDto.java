package com.kaoutar.gestionIncidents.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class IncidentDto {

    private Long id;
    private String title;
    private String description;
    private String status;
    private String priority;
    private String category;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime resolvedAt;

    private Long createdById;
    private Long assignedToId;


    private String imageBase64;
}
