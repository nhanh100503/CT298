package com.gis.service;

import com.gis.dto.status.StatusResponse;
import com.gis.dto.status.StatusTraceRequest;
import com.gis.dto.status.StatusUpdateRequest;
import com.gis.enums.BookingStatus;
import com.gis.enums.DriverStatus;
import com.gis.exception.AppException;
import com.gis.mapper.StatusMapper;
import com.gis.model.Booking;
import com.gis.model.Customer;
import com.gis.model.Status;
import com.gis.model.User;
import com.gis.repository.BookingRepository;
import com.gis.repository.CustomerRepository;
import com.gis.repository.StatusRepository;
import com.gis.repository.UserRepository;
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
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final StatusMapper statusMapper;

    public StatusResponse updateStatus(StatusUpdateRequest request) {
        Booking booking = bookingRepository.findById(request.getBooking().getId())
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Booking not found", "booking-e-02"));
        Customer customer = customerRepository.findById(booking.getCustomer().getId())
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Customer not found", "customer-e-02"));
        User user = userRepository.findById(booking.getUser().getId())
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "User not found", "user-e-02"));
        Map<BookingStatus, BookingStatus> nextStatusMap = Map.of(
                BookingStatus.SUCCESS, BookingStatus.PICKING,
                BookingStatus.PICKING, BookingStatus.TRANSPORTING,
                BookingStatus.TRANSPORTING, BookingStatus.FINISH
        );
        BookingStatus lastStatus = statusRepository.findTopByBookingOrderByTimeDesc(booking)
                .map(Status::getBookingStatus)
                .orElse(BookingStatus.PICKING);
        BookingStatus currentStatus = nextStatusMap.getOrDefault(lastStatus, lastStatus);
        Status status = Status.builder()
                .time(LocalDateTime.now())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .booking(booking)
                .bookingStatus(currentStatus)
                .build();
        statusRepository.save(status);
        if(lastStatus == BookingStatus.SUCCESS && currentStatus == BookingStatus.PICKING) {
            user.setDriverStatus(DriverStatus.BUSY);
            userRepository.save(user);
        }
        if (lastStatus == BookingStatus.TRANSPORTING && currentStatus == BookingStatus.FINISH) {
            long pointsEarned = (long) (booking.getPrice() / 100);
            customer.setAccumulate(customer.getAccumulate() + pointsEarned);
            customerRepository.save(customer);

            user.setDriverStatus(DriverStatus.FREE);
            user.setLatitude(booking.getDestinationX());
            user.setLongitude(booking.getDestinationY());
            userRepository.save(user);
        }
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
