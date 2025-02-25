package com.gis.dto.review;

import com.gis.model.Booking;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReviewResponse {
    Booking booking;
    List<ReviewRequest.ReviewCriteria> criteriaList;
}
