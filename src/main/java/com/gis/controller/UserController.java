package com.gis.controller;

import com.gis.dto.ApiResponse;
import com.gis.dto.driver.DriverResponse;
import com.gis.dto.user.UserCreateAccountRequest;
import com.gis.dto.user.UserCreateAccountResponse;
import com.gis.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor

public class UserController {
    private final UserService userService;

    @PostMapping("/create-user")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserCreateAccountResponse>> createAccountUser(@Valid @RequestBody UserCreateAccountRequest request) {
        ApiResponse<UserCreateAccountResponse> apiResponse = ApiResponse.<UserCreateAccountResponse>builder()
                .code("user-s-01")
                .data(userService.createAccountUser(request))
                .message("Tạo tài khoản user thành công")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @PutMapping("/active-driver/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<ApiResponse<DriverResponse>> activeDriver(@PathVariable("id") String driverId) {
        ApiResponse<DriverResponse> apiResponse = ApiResponse.<DriverResponse>builder()
                .code("user-s-01")
                .data(userService.activeDriver(driverId))
                .message("Xác thực tài xế thành công")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }
}
