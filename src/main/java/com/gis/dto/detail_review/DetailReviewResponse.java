package com.gis.dto.detail_review;

import com.gis.model.Criteria;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DetailReviewResponse {
    String id;
    Double point;
    Criteria criteria;
}
