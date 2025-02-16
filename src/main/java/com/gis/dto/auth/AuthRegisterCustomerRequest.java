package com.gis.dto.auth;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)

public class AuthRegisterCustomerRequest {
    String name;
    String email;
    String phone;
    String password;
}
