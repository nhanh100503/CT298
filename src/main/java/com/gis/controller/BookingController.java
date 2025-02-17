package com.gis.controller;

import com.gis.dto.ApiResponse;
import com.gis.dto.booking.BookingRequest;
import com.gis.dto.booking.BookingResponse;
import com.gis.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("")
@RequiredArgsConstructor

public class BookingController {
    private final BookingService bookingService;

    @PostMapping("/booking")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<BookingResponse>> booking(@Valid @RequestBody BookingRequest request) {
        ApiResponse<BookingResponse> apiResponse = ApiResponse.<BookingResponse>builder()
                .code("booking-s-01")
                .data(bookingService.booking(request))
                .message("Đặt xe thành công")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }
}
