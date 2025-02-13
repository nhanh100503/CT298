package com.gis.controller;

import com.gis.dto.ApiResponse;
import com.gis.dto.driver.DriverRegisterRequest;
import com.gis.dto.driver.DriverResponse;
import com.gis.service.DriverService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/driver")
@RequiredArgsConstructor

public class DriverController {
    private final DriverService driverService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<DriverResponse>> registerDriver(@Valid @RequestBody DriverRegisterRequest request) {
        ApiResponse<DriverResponse> apiResponse = ApiResponse.<DriverResponse>builder()
                .code("driver-s-01")
                .message("Đăng ký trở thành tài xế thành công")
                .data(driverService.registerDriver(request))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }
}
