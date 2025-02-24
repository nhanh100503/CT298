package com.gis.service;

import com.gis.dto.booking.BookingRequest;
import com.gis.dto.booking.BookingResponse;
import com.gis.enums.BookingStatus;
import com.gis.exception.AppException;
import com.gis.mapper.BookingMapper;
import com.gis.model.Booking;
import com.gis.model.Customer;
import com.gis.model.Status;
import com.gis.model.User;
import com.gis.repository.BookingRepository;
import com.gis.repository.CustomerRepository;
import com.gis.repository.StatusRepository;
import com.gis.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final StatusRepository statusRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final BookingMapper bookingMapper;

//    public BookingResponse booking(BookingRequest request) {
//        Customer customer = customerRepository.findById(request.getCustomer().getId()).orElseThrow(()
//            -> new AppException(HttpStatus.NOT_FOUND, "Customer not found", "auth-e-02"));
//        User driver = userRepository.findById(request.getUser().getId()).orElseThrow(()
//            -> new AppException(HttpStatus.NOT_FOUND, "User not found", "auth-e-02"));
//        Booking booking = Booking.builder()
//                .kilometer(request.getKilometer())
//                .startingX(request.getStartingX())
//                .startingY(request.getStartingY())
//                .destinationX(request.getDestinationX())
//                .destinationY(request.getDestinationY())
//                .bookingTime(LocalDateTime.now())
//                .accumulatedDiscount(request.getAccumulatedDiscount())
//                .memberDiscount(request.getMemberDiscount())
//                .price(request.getPrice())
//                .customer(customer)
//                .user(driver)
//                .build();
//        bookingRepository.save(booking);
//        BookingResponse bookingResponse = bookingMapper.toBookingResponse(booking);
//
//        String driverId = driver.getId();
//        if (driverId != null && !driverId.isEmpty()) {
//            messagingTemplate.convertAndSendToUser(driverId, "/ride-request", bookingResponse);
//        } else {
//            throw new AppException(HttpStatus.INTERNAL_SERVER_ERROR,
//                    "Driver ID is null or empty, cannot send WebSocket notification",
//                    "websocket-e-01");
//        }
//        return bookingResponse;
//    }
    public BookingResponse booking(BookingRequest request) {
        Customer customer = customerRepository.findById(request.getCustomer().getId())
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Customer not found", "auth-e-02"));
        User driver = userRepository.findById(request.getUser().getId())
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "User not found", "auth-e-02"));

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
                .user(driver)
                .build();
        bookingRepository.save(booking);

        Status status = Status.builder()
                .booking(booking)
                .bookingStatus(BookingStatus.SUCCESS)
                .time(LocalDateTime.now())
                .build();
        statusRepository.save(status);

        BookingResponse bookingResponse = bookingMapper.toBookingResponse(booking);
        bookingResponse.setStatus(status);

        String driverId = driver.getId();
        if (driverId != null && !driverId.isEmpty()) {
            // Thêm log để debug
            System.out.println("1. Driver ID: " + driverId);
            System.out.println("2. Đích gửi: /user/" + driverId + "/ride-request");
            System.out.println("3. Dữ liệu gửi: " + bookingResponse.toString());
            messagingTemplate.convertAndSendToUser(driverId, "/ride-request", bookingResponse);
            System.out.println("4. Đã gửi thông báo WebSocket");
        } else {
            throw new AppException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Driver ID is null or empty, cannot send WebSocket notification",
                    "websocket-e-01");
        }
        return bookingResponse;
    }
}
