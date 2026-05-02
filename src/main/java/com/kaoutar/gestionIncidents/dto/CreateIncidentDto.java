package com.kaoutar.gestionIncidents.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateIncidentDto {
    @NotBlank
    private String title;

    private String description;

    @NotNull
    private String priority;
    private String category;
    private Long createdById;
    private Long assignedToId;

    private String imageBase64;
}

