package com.kaoutar.gestionIncidents.dto.Response;

import lombok.Data;

/**
 * ✅ FIX : champs enrichis pour que le frontend puisse identifier l'utilisateur
 */
@Data
public class JwtResponse {
    private String token;
    private Long id;
    private String email;
    private String role;
    private String firstName;
    private String lastName;
}
