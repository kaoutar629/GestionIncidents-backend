package com.kaoutar.gestionIncidents.service;

import com.kaoutar.gestionIncidents.dto.CreateIncidentDto;
import com.kaoutar.gestionIncidents.dto.IncidentDto;
import com.kaoutar.gestionIncidents.entity.Incident;
import com.kaoutar.gestionIncidents.entity.User;
import com.kaoutar.gestionIncidents.enums.UserRole;
import com.kaoutar.gestionIncidents.repository.IncidentRepository;
import com.kaoutar.gestionIncidents.repository.UserRepository;
import com.kaoutar.gestionIncidents.mappers.IncidentMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IncidentServiceTest {

    @Mock
    private IncidentRepository incidentRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private IncidentMapper incidentMapper;
    @Mock
    private ApplicationEventPublisher publisher;
    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private IncidentService incidentService;

    // ✅ CREATE INCIDENT
    @Test
    void shouldCreateIncident() {

        CreateIncidentDto dto = new CreateIncidentDto();
        dto.setTitle("Bug");

        Incident incident = new Incident();
        incident.setTitle("Bug");

        Incident saved = new Incident();
        saved.setId(1L);
        saved.setTitle("Bug");

        when(incidentMapper.toEntity(dto)).thenReturn(incident);
        when(incidentRepository.save(incident)).thenReturn(saved);
        when(incidentMapper.toDto(saved)).thenReturn(new IncidentDto());

        IncidentDto result = incidentService.createIncident(dto);

        assertNotNull(result);
        verify(incidentRepository).save(incident);
    }

    // ❌ IMAGE TOO LARGE
    @Test
    void shouldThrowIfImageTooLarge() {
        CreateIncidentDto dto = new CreateIncidentDto();
        dto.setImageBase64("A".repeat(10_000_000)); // trop grand

        assertThrows(IllegalArgumentException.class, () -> {
            incidentService.createIncident(dto);
        });
    }

    // ✅ GET BY ID
    @Test
    void shouldGetIncidentById() {
        Incident incident = new Incident();
        incident.setId(1L);

        when(incidentRepository.findById(1L))
                .thenReturn(Optional.of(incident));

        when(incidentMapper.toDto(incident))
                .thenReturn(new IncidentDto());

        IncidentDto result = incidentService.getIncidentById(1L);

        assertNotNull(result);
    }

    // ❌ DELETE NOT ADMIN
    @Test
    void shouldThrowIfNotAdminDelete() {
        User user = new User();
        user.setRole(UserRole.USER);

        when(userRepository.findByEmail("user@mail.com"))
                .thenReturn(Optional.of(user));

        assertThrows(Exception.class, () -> {
            incidentService.deleteById(1L, "user@mail.com");
        });
    }

    // ✅ DELETE ADMIN
    @Test
    void shouldDeleteIfAdmin() {
        User admin = new User();
        admin.setRole(UserRole.ADMIN);

        when(userRepository.findByEmail("admin@mail.com"))
                .thenReturn(Optional.of(admin));

        when(incidentRepository.existsById(1L)).thenReturn(true);

        incidentService.deleteById(1L, "admin@mail.com");

        verify(incidentRepository).deleteById(1L);
    }
}