package com.kaoutar.gestionIncidents.mappers;

import com.kaoutar.gestionIncidents.dto.AuditDto;
import com.kaoutar.gestionIncidents.entity.AuditLog;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuditLogtMapper {
    AuditDto toDto(AuditLog log);
}
