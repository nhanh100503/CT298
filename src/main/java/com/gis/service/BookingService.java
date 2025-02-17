package com.gis.service;

import com.gis.dto.booking.BookingRequest;
import com.gis.dto.booking.BookingResponse;
import com.gis.exception.AppException;
import com.gis.mapper.BookingMapper;
import com.gis.model.Booking;
import com.gis.model.Customer;
import com.gis.model.User;
import com.gis.repository.BookingRepository;
import com.gis.repository.CustomerRepository;
import com.gis.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;

    private final BookingMapper bookingMapper;

    public BookingResponse booking(BookingRequest request) {
        Customer customer = customerRepository.findById(request.getCustomer().getId()).orElseThrow(()
            -> new AppException(HttpStatus.NOT_FOUND, "Customer not found", "auth-e-02"));
        User user = userRepository.findById(request.getUser().getId()).orElseThrow(()
            -> new AppException(HttpStatus.NOT_FOUND, "Customer not found", "auth-e-02"));
        Booking booking = Booking.builder()
                .kilometer(request.getKilometer())
                .startingX(request.getStartingX())
                .startingY(request.getStartingY())
                .destinationX(request.getDestinationX())
                .destinationY(request.getDestinationY())
                .bookingTime(LocalDateTime.now())
                .accumulatedDiscount(request.getAccumulatedDiscount())
                .memberDiscount(request.getMemberDiscount())
                .price(request.getPrice())
                .customer(customer)
                .user(user)
                .build();
        bookingRepository.save(booking);
        return bookingMapper.toBookingResponse(booking);
    }
}
