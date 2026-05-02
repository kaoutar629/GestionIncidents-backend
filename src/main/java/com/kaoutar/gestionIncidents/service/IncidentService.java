package com.kaoutar.gestionIncidents.service;

import com.kaoutar.gestionIncidents.dto.CreateIncidentDto;
import com.kaoutar.gestionIncidents.dto.IncidentDto;
import com.kaoutar.gestionIncidents.entity.Incident;
import com.kaoutar.gestionIncidents.entity.User;
import com.kaoutar.gestionIncidents.enums.IncidentStatus;
import com.kaoutar.gestionIncidents.enums.Priority;
import com.kaoutar.gestionIncidents.enums.UserRole;
import com.kaoutar.gestionIncidents.events.IncidentEvent;
import com.kaoutar.gestionIncidents.exception.IncidentNotFoundException;
import com.kaoutar.gestionIncidents.mappers.IncidentMapper;
import com.kaoutar.gestionIncidents.repository.IncidentRepository;
import com.kaoutar.gestionIncidents.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class IncidentService {

    private final ApplicationEventPublisher publisher;
    private final IncidentRepository        incidentRepository;
    private final IncidentMapper            incidentMapper;
    private final NotificationService       notificationService;
    private final UserRepository            userRepository;

    private static final int MAX_BASE64_LENGTH = 5 * 1024 * 1024 * 4 / 3;

    private Pageable pageable(int page, int size) {
        return PageRequest.of(page, size, Sort.by("createdAt").descending());
    }

    public Page<IncidentDto> getAllIncidents(int page, int size, String callerEmail) {
        User caller = userRepository.findByEmail(callerEmail)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        Pageable p = pageable(page, size);
        if (caller.getRole() == UserRole.ADMIN)
            return incidentRepository.findAll(p).map(incidentMapper::toDto);
        return incidentRepository.findByCreatedById(caller.getId(), p).map(incidentMapper::toDto);
    }

    public Page<IncidentDto> getByStatus(int page, int size, String status, String callerEmail) {
        User caller = userRepository.findByEmail(callerEmail).orElseThrow();
        IncidentStatus s = IncidentStatus.valueOf(status.toUpperCase());
        Pageable p = pageable(page, size);
        if (caller.getRole() == UserRole.ADMIN)
            return incidentRepository.findByStatus(s, p).map(incidentMapper::toDto);
        return incidentRepository.findByStatusAndCreatedById(s, caller.getId(), p).map(incidentMapper::toDto);
    }

    public Page<IncidentDto> getByPriority(int page, int size, String priority, String callerEmail) {
        User caller = userRepository.findByEmail(callerEmail).orElseThrow();
        Priority pr = Priority.valueOf(priority.toUpperCase());
        Pageable p  = pageable(page, size);
        if (caller.getRole() == UserRole.ADMIN)
            return incidentRepository.findByPriority(pr, p).map(incidentMapper::toDto);
        return incidentRepository.findByPriorityAndCreatedById(pr, caller.getId(), p).map(incidentMapper::toDto);
    }

    public IncidentDto getIncidentById(Long id) {
        return incidentRepository.findById(id)
                .map(incidentMapper::toDto)
                .orElseThrow(() -> new IncidentNotFoundException("Incident introuvable : " + id));
    }

    public IncidentDto createIncident(CreateIncidentDto dto) {
        if (dto.getImageBase64() != null && dto.getImageBase64().length() > MAX_BASE64_LENGTH)
            throw new IllegalArgumentException("Image trop lourde. Taille maximum : 5 MB");

        var incident = incidentMapper.toEntity(dto);
        var saved    = incidentRepository.save(incident);

        try {
            String createdBy = saved.getCreatedBy() != null
                    ? saved.getCreatedBy().getEmail()
                    : "inconnu";

            // ✅ FIX 2: Notifier TOUS les admins par leur email réel
            List<User> admins = userRepository.findAll().stream()
                    .filter(u -> u.getRole() == UserRole.ADMIN)
                    .toList();

            for (User admin : admins) {
                publisher.publishEvent(
                        new IncidentEvent(
                                saved.getId(),
                                admin.getEmail(),   // ← email réel de l'admin
                                "Nouvel incident créé par " + createdBy + " : " + saved.getTitle(),
                                "CREATED"
                        )
                );
            }
        } catch (Exception e) {
            log.warn("Event incident creation failed: {}", e.getMessage());
        }

        return incidentMapper.toDto(saved);
    }

    public IncidentDto updateIncident(IncidentDto dto, Long id, String callerEmail) {
        var incident = incidentRepository.findById(id)
                .orElseThrow(() -> new IncidentNotFoundException("Incident introuvable : " + id));

        User caller   = userRepository.findByEmail(callerEmail).orElseThrow();
        boolean isAdmin   = caller.getRole() == UserRole.ADMIN;
        boolean isCreator = incident.getCreatedBy() != null
                && incident.getCreatedBy().getId().equals(caller.getId());

        if (!isAdmin && !isCreator)
            throw new AccessDeniedException("Vous n'êtes pas autorisé à modifier cet incident");

        IncidentStatus oldStatus = incident.getStatus();

        incident.setTitle(dto.getTitle());
        incident.setCategory(dto.getCategory());
        incident.setDescription(dto.getDescription());

        if (dto.getStatus() != null) {
            IncidentStatus newStatus = IncidentStatus.valueOf(dto.getStatus().toUpperCase());
            incident.setStatus(newStatus);
            if (newStatus == IncidentStatus.RESOLVED && incident.getResolvedAt() == null)
                incident.setResolvedAt(LocalDateTime.now());
        }

        if (dto.getPriority() != null)
            incident.setPriority(Priority.valueOf(dto.getPriority().toUpperCase()));

        if (dto.getImageBase64() != null)
            incident.setImageBase64(dto.getImageBase64());



        incident.setUpdatedAt(LocalDateTime.now());
        Incident saved = incidentRepository.save(incident);

        try {
            boolean statusChanged = dto.getStatus() != null
                    && !IncidentStatus.valueOf(dto.getStatus().toUpperCase()).equals(oldStatus);

            if (statusChanged && saved.getCreatedBy() != null) {
                String ownerEmail = saved.getCreatedBy().getEmail();
                publisher.publishEvent(
                        new IncidentEvent(
                                saved.getId(),
                                ownerEmail,   // ← email du créateur de l'incident
                                "Statut de votre incident \"" + saved.getTitle() + "\" mis à jour : " + saved.getStatus(),
                                "STATUS_UPDATE"
                        )
                );
            }
        } catch (Exception e) {
            log.warn("Event update failed: {}", e.getMessage());
        }

        return incidentMapper.toDto(saved);
    }

    public void deleteById(Long id, String callerEmail) {
        User caller = userRepository.findByEmail(callerEmail).orElseThrow();
        if (caller.getRole() != UserRole.ADMIN)
            throw new AccessDeniedException("Seul un admin peut supprimer un incident");
        if (!incidentRepository.existsById(id))
            throw new IncidentNotFoundException("Incident introuvable : " + id);
        incidentRepository.deleteById(id);
    }
}
