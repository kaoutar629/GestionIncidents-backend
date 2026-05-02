
package com.kaoutar.gestionIncidents.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AiClassifyRequest {

    @NotBlank(message = "Le titre est obligatoire")
    @Size(max = 500)
    private String title;

    @Size(max = 2000)
    private String description;
}