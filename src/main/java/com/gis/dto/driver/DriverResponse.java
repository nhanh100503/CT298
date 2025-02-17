package com.gis.dto.driver;

import com.gis.enums.DriverStatus;
import com.gis.enums.ERole;
import com.gis.enums.UserStatus;
import com.gis.model.Car;
import com.gis.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DriverResponse {
    String id;
    String name;
    String phone;
    String email;
    String username;
    Boolean gender;
    String avatar;
    LocalDateTime time;
    Double latitude;
    Double longitude;
    String driverLicense;
    DriverStatus driverStatus;
    UserStatus status;
    ERole role;
    User user;
    Car car;
}
