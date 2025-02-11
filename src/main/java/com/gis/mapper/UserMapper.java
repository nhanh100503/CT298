package com.gis.mapper;

import com.gis.dto.driver.DriverResponse;
import com.gis.dto.jwt.JWTPayloadDto;
import com.gis.dto.user.UserCreateAccountResponse;
import com.gis.enums.ERole;
import com.gis.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(source = "role", target = "scope", qualifiedByName = "roleToScope")
    JWTPayloadDto toJWTPayloadDto(User user);

    @org.mapstruct.Named("roleToScope")
    static String roleToScope(ERole role) {
        return String.format("ROLE_%s", role.name());
    }

    UserCreateAccountResponse toUserCreateAccountResponse(User user);

    DriverResponse toDriverResponse(User user);
}
