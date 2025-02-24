package com.gis.controller;

import com.gis.dto.ApiResponse;
import com.gis.dto.status.StatusRequest;
import com.gis.dto.status.StatusResponse;
import com.gis.service.StatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/status")
@RequiredArgsConstructor
public class StatusController {
    private final StatusService statusService;

    @PostMapping("/trace")
    public ResponseEntity<ApiResponse<StatusResponse>> trace(@Valid @RequestBody StatusRequest request) {
        ApiResponse<StatusResponse> apiResponse = ApiResponse.<StatusResponse>builder()
                .code("status-s-01")
                .message("Truy vết tài xế thành công")
                .data(statusService.trace(request))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }
}
