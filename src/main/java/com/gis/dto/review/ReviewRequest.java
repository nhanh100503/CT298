package com.gis.dto.review;

import com.gis.model.Booking;
import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
public class ReviewRequest {
    private Booking booking;
    private List<ReviewCriteria> criteriaList;
    private String text;

    @Getter
    public static class ReviewCriteria {
        private String criteriaId;
        private String name;
        @Getter
        private Integer star;
    }
}
