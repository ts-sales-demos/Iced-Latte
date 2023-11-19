package com.zufar.icedlatte.security.converter;

import com.zufar.icedlatte.security.dto.UserRegistrationRequest;
import com.zufar.icedlatte.openapi.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RegistrationDtoConverter {

    UserDto toDto(final UserRegistrationRequest userRegistrationRequest);
}