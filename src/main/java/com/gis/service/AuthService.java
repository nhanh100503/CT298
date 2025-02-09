package com.gis.service;

import com.gis.dto.auth.AuthRegisterCustomerRequest;
import com.gis.dto.auth.AuthResponse;
import com.gis.enums.ERole;
import com.gis.enums.UserStatus;
import com.gis.exception.AppException;
import com.gis.model.Customer;
import com.gis.repository.CustomerRepository;
import com.gis.util.PasswordUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)

public class AuthService {
    final PasswordUtil passwordUtil;
    final CustomerRepository customerRepository;

    public void registerCustomer(AuthRegisterCustomerRequest request) {
        boolean existedCustomer = customerRepository.existsByEmailAndIsOutsideFalse(request.getEmail());
        if (existedCustomer) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Customer email already exists", "auth-e-01");
        }
    }

    public AuthResponse verifyRegisterCustomer(AuthRegisterCustomerRequest request) {
        boolean existedCustomer = customerRepository.existsByEmailAndIsOutsideFalse(request.getEmail());
        if (existedCustomer) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Customer email already exists", "auth-e-01");
        }
        String hashedPassword = passwordUtil.encodePassword(request.getPassword());
        request.setPassword(hashedPassword);
        Customer customer = Customer.builder()
                .id(UUID.randomUUID().toString())
                .name(request.getName())
                .email(request.getEmail())
                .password(request.getPassword())
                .role(ERole.CUSTOMER)
                .isOutside(false)
                .status(UserStatus.ACTIVE)
                .build();
        customerRepository.save(customer);
        String accessTokenString = accessTokenUtil.generateTokenCustomer(customerMapper.toJWTPayloadDto(customer));
        String refreshTokenString = refreshTokenUtil.generateTokenCustomer(customerMapper.toJWTPayloadDto(customer), customer);
        return AuthResponse.builder()
                .accessToken(accessTokenString)
                .refreshToken(refreshTokenString)
                .build();
    }
}
