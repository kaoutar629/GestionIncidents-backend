package com.kaoutar.gestionIncidents.controller;

import com.kaoutar.gestionIncidents.dto.CreateIncidentDto;
import com.kaoutar.gestionIncidents.dto.IncidentDto;
import com.kaoutar.gestionIncidents.service.IncidentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/incidents")
@RequiredArgsConstructor
@Tag(name = "Incidents", description = "Gestion des incidents")
@SecurityRequirement(name = "bearerAuth")
public class IncidentController {

    private final IncidentService incidentService;

    @GetMapping
    @Operation(summary = "Liste paginee des incidents (filtree par role)")
    public Page<IncidentDto> getAllIncidents(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false)    String status,
            @RequestParam(required = false)    String priority,
            Authentication auth) {

        String email = auth.getName();
        if (priority != null) return incidentService.getByPriority(page, size, priority, email);
        if (status   != null) return incidentService.getByStatus(page, size, status, email);
        return incidentService.getAllIncidents(page, size, email);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detail d un incident")
    public ResponseEntity<IncidentDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(incidentService.getIncidentById(id));
    }

    @PostMapping
    @Operation(summary = "Creer un incident")
    public ResponseEntity<IncidentDto> createIncident(@RequestBody @Valid CreateIncidentDto dto) {
        return ResponseEntity.ok(incidentService.createIncident(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un incident")
    public ResponseEntity<IncidentDto> updateIncident(
            @RequestBody IncidentDto dto,
            @PathVariable Long id,
            Authentication auth) {
        return ResponseEntity.ok(incidentService.updateIncident(dto, id, auth.getName()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un incident (admin uniquement)")
    public ResponseEntity<Void> deleteById(@PathVariable Long id, Authentication auth) {
        incidentService.deleteById(id, auth.getName());
        return ResponseEntity.noContent().build();
    }
}
