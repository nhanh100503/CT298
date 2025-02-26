package com.gis.controller;
import com.gis.dto.ApiResponse;
import com.gis.dto.review.ReviewRequest;
import com.gis.dto.review.ReviewResponse;
import com.gis.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/review")
@RequiredArgsConstructor

public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping("/add")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(@Valid @RequestBody ReviewRequest request) {
        ApiResponse<ReviewResponse> apiResponse = ApiResponse.<ReviewResponse>builder()
                .code("review-s-01")
                .message("Thêm đánh giá thành công")
                .data(reviewService.createReview(request))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }
}
