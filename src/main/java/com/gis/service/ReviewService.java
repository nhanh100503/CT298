package com.gis.service;

import com.gis.dto.review.ReviewRequest;
import com.gis.dto.review.ReviewResponse;
import com.gis.exception.AppException;
import com.gis.model.Booking;
import com.gis.model.Criteria;
import com.gis.model.Review;
import com.gis.model.User;
import com.gis.repository.BookingRepository;
import com.gis.repository.CriteriaRepository;
import com.gis.repository.ReviewRepository;
import com.gis.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final BookingRepository bookingRepository;
    private final CriteriaRepository criteriaRepository;
    private final UserRepository userRepository;

    public ReviewResponse createReview(ReviewRequest request) {
        Booking booking = bookingRepository.findById(request.getBooking().getId()).orElseThrow(()
            -> new AppException(HttpStatus.NOT_FOUND, "Booking not found", "booking-e-02"));
        List<Review> reviews = request.getCriteriaList().stream().map(criteriaData -> {
            Criteria criteria = criteriaRepository.findById(criteriaData.getCriteriaId())
                    .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "criteria not found", "criteria-e-02"));
            Review review = new Review();
            review.setBooking(booking);
            review.setCriteria(criteria);
            review.setStar(criteriaData.getStar());
            return review;
        }).toList();
        reviewRepository.saveAll(reviews);

        // 🟢 Lấy User từ booking
        User user = booking.getUser();

// 🟢 Lấy số sao trung bình cũ và số lượng đánh giá cũ
        double oldStar = user.getStar();
        long reviewCount = reviewRepository.countByBooking_User(user);

// 🟢 Tính tổng số sao mới từ danh sách đánh giá mới
        double totalStarsNewReviews = reviews.stream().mapToDouble(Review::getStar).sum();

// 🟢 Kiểm tra số lượng đánh giá cũ
        long previousReviewCount = Math.max(reviewCount - reviews.size(), 0);

// 🟢 Tính tổng số sao mới
        double newTotalStars = (oldStar * previousReviewCount) + totalStarsNewReviews;
        long newReviewCount = previousReviewCount + reviews.size();

// 🟢 Đảm bảo không chia cho 0
        double newAverageStar = (newReviewCount > 0) ? (newTotalStars / newReviewCount) : 0.0;

// 🟢 Cập nhật lại star của User
        user.setStar(newAverageStar);
        userRepository.save(user); // Lưu lại User vào database


        List<ReviewRequest.ReviewCriteria> criteriaList = request.getCriteriaList();
        ReviewResponse response = new ReviewResponse();
        response.setBooking(booking);
        response.setCriteriaList(criteriaList);
        return response;
    }
}
