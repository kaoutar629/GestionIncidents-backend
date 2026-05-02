package com.kaoutar.gestionIncidents.mappers;

import com.kaoutar.gestionIncidents.dto.CreateUserDto;
import com.kaoutar.gestionIncidents.dto.UserDto;
import com.kaoutar.gestionIncidents.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(CreateUserDto  requestDto);
    UserDto toDto (User user);
}
