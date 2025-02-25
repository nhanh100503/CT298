package com.gis.service;

import com.gis.dto.status.StatusResponse;
import com.gis.dto.status.StatusTraceRequest;
import com.gis.dto.status.StatusUpdateRequest;
import com.gis.enums.BookingStatus;
import com.gis.exception.AppException;
import com.gis.mapper.StatusMapper;
import com.gis.model.Booking;
import com.gis.model.Status;
import com.gis.repository.BookingRepository;
import com.gis.repository.StatusRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatusService {
    private final StatusRepository statusRepository;
    private final BookingRepository bookingRepository;
    private final StatusMapper statusMapper;

    public StatusResponse updateStatus(StatusUpdateRequest request) {
        Booking booking = bookingRepository.findById(request.getBooking().getId())
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Booking not found", "booking-e-02"));

        Map<BookingStatus, BookingStatus> nextStatusMap = Map.of(
                BookingStatus.SUCCESS, BookingStatus.PICKING,
                BookingStatus.PICKING, BookingStatus.TRANSPORTING,
                BookingStatus.TRANSPORTING, BookingStatus.FINISH
        );

        BookingStatus currentStatus = statusRepository.findTopByBookingOrderByTimeDesc(booking)
                .map(Status::getBookingStatus)
                .map(nextStatusMap::get)
                .orElse(BookingStatus.PICKING);

        Status status = Status.builder()
                .time(LocalDateTime.now())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .booking(booking)
                .bookingStatus(currentStatus)
                .build();

        statusRepository.save(status);
        return statusMapper.toStatusResponse(status);
    }

    public StatusResponse trace(StatusTraceRequest request) {
        Booking booking = bookingRepository.findById(request.getBooking().getId())
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Booking not found", "booking-e-02"));
        Status status = Status.builder()
                .time(LocalDateTime.now())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .booking(booking)
                .bookingStatus(request.getStatus())
                .build();
        statusRepository.save(status);
        return statusMapper.toStatusResponse(status);
    }
}
