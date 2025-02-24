package com.gis.controller;

import com.gis.dto.booking.BookingRequest;
import com.gis.dto.booking.BookingResponse;
import com.gis.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class WebSocketController {
    private final BookingService bookingService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/booking")
    public void handleBookingRequest(@Payload BookingRequest request) {
        System.out.println("Nhận JSON từ client: " + request);

        // Gọi service để xử lý booking
        BookingResponse response = bookingService.booking(request);

        // Gửi thông báo tới driver qua WebSocket
        String driverId = request.getUser().getId();
        if (driverId != null && !driverId.isEmpty()) {
            System.out.println("Gửi thông báo tới: /user/" + driverId + "/ride-request");
            messagingTemplate.convertAndSendToUser(driverId, "/ride-request", response);
        } else {
            System.out.println("Driver ID không hợp lệ");
        }
    }
}
