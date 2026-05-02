package com.kaoutar.gestionIncidents.repository;

import com.kaoutar.gestionIncidents.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LogRepository extends JpaRepository<AuditLog,Long> {
    List<AuditLog> findByEntityType(String entityType);
    List<AuditLog> findByPerformedBy(Long userId);
}
