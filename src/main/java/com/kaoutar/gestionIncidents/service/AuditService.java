package com.kaoutar.gestionIncidents.service;

import com.kaoutar.gestionIncidents.entity.AuditLog;
import com.kaoutar.gestionIncidents.repository.LogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final LogRepository logRepository;

    @Async
    public void log(String action, String entityType,
                    Long entityId, Long performedBy) {
        AuditLog entry = new AuditLog();
        entry.setAction(action);
        entry.setEntityType(entityType);
        entry.setEntityId(entityId);
        entry.setPerformedBy(performedBy);
        logRepository.save(entry);
    }
}
