package com.gis.mapper;

import com.gis.dto.jwt.JWTPayloadDto;
import com.gis.enums.ERole;
import com.gis.model.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    @Mapping(source = "role", target = "scope", qualifiedByName = "roleToScope")
    JWTPayloadDto toJWTPayloadDto(Customer customer);

    @org.mapstruct.Named("roleToScope")
    static String roleToScope(ERole role) {
        return String.format("ROLE_%s", role.name());
    }
}
