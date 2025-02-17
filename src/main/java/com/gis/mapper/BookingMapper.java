package com.gis.mapper;

import com.gis.dto.booking.BookingResponse;
import com.gis.model.Booking;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    BookingResponse toBookingResponse(Booking booking);
}
