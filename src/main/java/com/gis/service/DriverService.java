package com.gis.service;

import com.gis.dto.driver.DriverRegisterRequest;
import com.gis.dto.driver.DriverResponse;
import com.gis.enums.DriverStatus;
import com.gis.enums.ERole;
import com.gis.enums.UserStatus;
import com.gis.exception.AppException;
import com.gis.mapper.UserMapper;
import com.gis.model.User;
import com.gis.repository.UserRepository;
import com.gis.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DriverService {
    private final UserRepository userRepository;
    private final PasswordUtil passwordUtil;
    private final UserMapper userMapper;

    public DriverResponse registerDriver(DriverRegisterRequest request){
        boolean existedDriver = userRepository.existsByEmail(request.getEmail());
        if (existedDriver) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Email has existed", "auth-e-01");
        }
        String password = request.getPassword();
        String hashedPassword = passwordUtil.encodePassword(password);
        User user = User.builder()
                .name(request.getName())
                .phone(request.getPhone())
                .email(request.getEmail())
                .driverLicense(request.getDriverLicense())
                .driverStatus(DriverStatus.INACTIVE)
                .gender(request.getGender())
                .role(ERole.DRIVER)
                .status(UserStatus.ACTIVE)
                .username(request.getUsername())
                .password(hashedPassword)
                .build();
        userRepository.save(user);
        return userMapper.toDriverResponse(user);
    }
}
