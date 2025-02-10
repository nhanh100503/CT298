package com.gis.service;

import com.gis.dto.auth.AuthCustomerLoginRequest;
import com.gis.dto.auth.AuthCustomerRegisterRequest;
import com.gis.dto.auth.AuthResponse;
import com.gis.dto.jwt.JWTPayloadDto;
import com.gis.enums.ERole;
import com.gis.enums.UserStatus;
import com.gis.exception.AppException;
import com.gis.mapper.CustomerMapper;
import com.gis.model.Customer;
import com.gis.repository.CustomerRepository;
import com.gis.util.PasswordUtil;
import com.gis.util.jwt.AccessTokenUtil;
import com.gis.util.jwt.RefreshTokenUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)

public class AuthService {
    final PasswordUtil passwordUtil;
    final AccessTokenUtil accessTokenUtil;
    final RefreshTokenUtil refreshTokenUtil;
    final CustomerRepository customerRepository;
    final CustomerMapper customerMapper;

    public void registerCustomer(AuthCustomerRegisterRequest request) {
        boolean existedCustomer = customerRepository.existsByEmailAndIsOutsideFalse(request.getEmail());
        if (existedCustomer) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Customer email already exists", "auth-e-01");
        }
    }

    public AuthResponse verifyRegisterCustomer(AuthCustomerRegisterRequest request) {
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

    public AuthResponse loginCustomer(AuthCustomerLoginRequest request) {
        Customer customer = customerRepository.findByEmailAndIsOutsideFalse(request.getEmail()).orElseThrow(
                () -> new AppException(HttpStatus.NOT_FOUND, "Customer email not found", "auth-e-02")
        );
        checkCustomerStatus(customer);
        boolean isMatchPassword = passwordUtil.checkPassword(request.getPassword(), customer.getPassword());
        if (!isMatchPassword) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Wrong password", "auth-e-03");
        }
        String accessTokenString = accessTokenUtil.generateTokenCustomer(customerMapper.toJWTPayloadDto(customer));
        String refreshTokenString = refreshTokenUtil.generateTokenCustomer(customerMapper.toJWTPayloadDto(customer), customer);
        return AuthResponse.builder()
                .accessToken(accessTokenString)
                .refreshToken(refreshTokenString)
                .build();
    }

    public AuthResponse loginOAuth2Success(OAuth2User oAuth2User) {
        String userOAuthId = oAuth2User.getAttribute("sub");
        String providerName = oAuth2User.getAttribute("provider");
        Customer customer = customerRepository.findByIsOutsideTrueAndProviderNameAndProviderId(providerName, userOAuthId)
                .orElseGet(() -> {
                    Customer newCustomer = Customer.builder()
                            .email(oAuth2User.getAttribute("email"))
                            .name(oAuth2User.getAttribute("name"))
                            .isOutside(true)
                            .providerId(userOAuthId)
                            .providerName(providerName)
                            .avatar(oAuth2User.getAttribute("picture"))
                            .role(ERole.CUSTOMER)
                            .status(UserStatus.ACTIVE)
                            .build();
                    return customerRepository.save(newCustomer);
                });
        checkCustomerStatus(customer);
        JWTPayloadDto payload = customerMapper.toJWTPayloadDto(customer);
        String accessToken = accessTokenUtil.generateTokenCustomer(payload);
        String refreshToken = refreshTokenUtil.generateTokenCustomer(payload, customer);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    private void checkCustomerStatus(Customer customer){
        if(!customer.getStatus().equals(UserStatus.ACTIVE)){
            throw new AppException(HttpStatus.BAD_REQUEST, "Customer is not active", "auth-e-06");
        }
    }
}
