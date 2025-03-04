package com.gis.mapper;

import com.gis.dto.booking.BookingResponse;
import com.gis.model.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    @Mapping(target = "status", expression = "java(booking.getStatuses() != null && !booking.getStatuses().isEmpty() ? booking.getStatuses().get(0) : null)")
    BookingResponse toBookingResponse(Booking booking);
}

