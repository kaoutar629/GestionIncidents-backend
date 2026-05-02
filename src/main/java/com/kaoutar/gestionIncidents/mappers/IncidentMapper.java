package com.kaoutar.gestionIncidents.mappers;

import com.kaoutar.gestionIncidents.dto.CreateIncidentDto;
import com.kaoutar.gestionIncidents.dto.IncidentDto;
import com.kaoutar.gestionIncidents.entity.Incident;
import com.kaoutar.gestionIncidents.entity.User;
import com.kaoutar.gestionIncidents.enums.IncidentStatus;
import com.kaoutar.gestionIncidents.enums.Priority;
import com.kaoutar.gestionIncidents.repository.UserRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class IncidentMapper {

    @Autowired
    protected UserRepository userRepository;

    @Mapping(target = "id",         ignore = true)
    @Mapping(target = "status",     expression = "java(com.kaoutar.gestionIncidents.enums.IncidentStatus.OPEN)")
    @Mapping(target = "createdAt",  expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt",  ignore = true)
    @Mapping(target = "resolvedAt", ignore = true)
    @Mapping(target = "createdBy",  source = "createdById",  qualifiedByName = "idToUser")
    @Mapping(target = "priority",   source = "priority",     qualifiedByName = "stringToPriority")
    @Mapping(target = "category",   source = "category")
    public abstract Incident toEntity(CreateIncidentDto requestDto);
    @Mapping(target = "createdById",  source = "createdBy.id")
    @Mapping(target = "status",   expression = "java(incident.getStatus()  != null ? incident.getStatus().name()  : null)")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "priority", expression = "java(incident.getPriority() != null ? incident.getPriority().name() : null)")
    public abstract IncidentDto toDto(Incident incident);


    @Named("idToUser")
    protected User idToUser(Long id) {
        if (id == null) return null;

        return userRepository.getReferenceById(id);
    }

    @Named("stringToPriority")
    protected Priority stringToPriority(String priority) {
        if (priority == null) return Priority.LOW;
        return Priority.valueOf(priority.toUpperCase());
    }
}
