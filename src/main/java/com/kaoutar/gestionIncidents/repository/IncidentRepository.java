package com.kaoutar.gestionIncidents.repository;

import com.kaoutar.gestionIncidents.entity.Incident;
import com.kaoutar.gestionIncidents.enums.IncidentStatus;
import com.kaoutar.gestionIncidents.enums.Priority;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IncidentRepository extends JpaRepository<Incident, Long> {

    Page<Incident> findByStatus(IncidentStatus status, Pageable pageable);
    Page<Incident> findByPriority(Priority priority, Pageable pageable);
    Page<Incident> findByCreatedById(Long userId, Pageable pageable);

    // FIX 2: filtered queries for non-admin users
    Page<Incident> findByStatusAndCreatedById(IncidentStatus status, Long userId, Pageable pageable);
    Page<Incident> findByPriorityAndCreatedById(Priority priority, Long userId, Pageable pageable);
}
