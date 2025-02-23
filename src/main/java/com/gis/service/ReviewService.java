package com.gis.service;

import com.gis.dto.review.ReviewRequest;
import com.gis.dto.review.ReviewResponse;
import com.gis.exception.AppException;
import com.gis.model.*;
import com.gis.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final BookingRepository bookingRepository;
    private final CriteriaRepository criteriaRepository;
    private final UserRepository userRepository;
    private final DetailReviewRepository detailReviewRepository;

//    public ReviewResponse createReview(ReviewRequest request) {
//        Booking booking = bookingRepository.findById(request.getBooking().getId()).orElseThrow(()
//            -> new AppException(HttpStatus.NOT_FOUND, "Booking not found", "booking-e-02"));
//        List<Review> reviews = request.getCriteriaList().stream().map(criteriaData -> {
//            Criteria criteria = criteriaRepository.findById(criteriaData.getCriteriaId())
//                    .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "criteria not found", "criteria-e-02"));
//            Review review = new Review();
//            review.setBooking(booking);
//            review.setCriteria(criteria);
//            review.setStar(criteriaData.getStar());
//            return review;
//        }).toList();
//        reviewRepository.saveAll(reviews);
//
//        User user = booking.getUser();
//        double oldStar = user.getStar();
//        long reviewCount = reviewRepository.countByBooking_User(user);
//        double totalStarsNewReviews = reviews.stream().mapToDouble(Review::getStar).sum();
//        long previousReviewCount = Math.max(reviewCount - reviews.size(), 0);
//        double newTotalStars = (oldStar * previousReviewCount) + totalStarsNewReviews;
//        long newReviewCount = previousReviewCount + reviews.size();
//        double newAverageStar = (newReviewCount > 0) ? (newTotalStars / newReviewCount) : 0.0;
//        user.setStar(newAverageStar);
//        userRepository.save(user);
//
//
//
//
//        List<ReviewRequest.ReviewCriteria> criteriaList = request.getCriteriaList();
//        ReviewResponse response = new ReviewResponse();
//        response.setBooking(booking);
//        response.setCriteriaList(criteriaList);
//        return response;
//    }

    public ReviewResponse createReview(ReviewRequest request) {
        Booking booking = bookingRepository.findById(request.getBooking().getId())
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Booking not found", "booking-e-02"));

        User user = booking.getUser();
        List<Review> reviews = new ArrayList<>();
        List<DetailReview> detailReviews = new ArrayList<>();

        Map<Criteria, Double> totalPointsMap = new HashMap<>();
        Map<Criteria, Long> countMap = new HashMap<>();

        for (ReviewRequest.ReviewCriteria criteriaData : request.getCriteriaList()) {
            Criteria criteria = criteriaRepository.findById(criteriaData.getCriteriaId())
                    .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Criteria not found", "criteria-e-02"));

            // 🟢 Lưu vào Review
            Review review = new Review();
            review.setBooking(booking);
            review.setCriteria(criteria);
            review.setStar(criteriaData.getStar());
            reviews.add(review);

            // 🟢 Lưu vào DetailReview
            DetailReview detailReview = new DetailReview();
            detailReview.setUser(user);
            detailReview.setCriteria(criteria);
            detailReview.setPoint((double) criteriaData.getStar());
            detailReviews.add(detailReview);

            // 🟢 Tính tổng điểm và số lượng đánh giá hiện tại
            totalPointsMap.put(criteria, detailReviewRepository.getTotalPoints(criteria.getId()) + criteriaData.getStar());
            countMap.put(criteria, detailReviewRepository.getReviewCount(criteria.getId()) + 1);
        }

        reviewRepository.saveAll(reviews);
        detailReviewRepository.saveAll(detailReviews);

        // 🟢 Cập nhật trung bình sao của User
        updateUserStar(user, reviews);

        // 🟢 Cập nhật trung bình sao của từng tiêu chí
        updateDetailReviewPoints(reviews);

        // 🟢 Trả về response
        ReviewResponse response = new ReviewResponse();
        response.setBooking(booking);
        response.setCriteriaList(request.getCriteriaList());
        return response;
    }

    private void updateUserStar(User user, List<Review> newReviews) {
        double oldStar = user.getStar();
        long reviewCount = reviewRepository.countByBooking_User(user);

        double totalStarsNewReviews = newReviews.stream().mapToDouble(Review::getStar).sum();
        long previousReviewCount = Math.max(reviewCount - newReviews.size(), 0);
        double newTotalStars = (oldStar * previousReviewCount) + totalStarsNewReviews;
        long newReviewCount = previousReviewCount + newReviews.size();

        double newAverageStar = (newReviewCount > 0) ? (newTotalStars / newReviewCount) : 0.0;
        user.setStar(newAverageStar);
        userRepository.save(user);
    }

    private void updateDetailReviewPoints(List<Review> reviews) {
        for (Review review : reviews) {
            Criteria criteria = review.getCriteria();
            User user = review.getBooking().getUser();

            // ✅ Lấy danh sách tất cả DetailReview trước đó theo Criteria và User
            List<DetailReview> detailReviews = detailReviewRepository.findByCriteriaAndUser(criteria, user);

            // ✅ Tính tổng điểm và số lượng đánh giá
            double totalPoints = detailReviews.stream().mapToDouble(DetailReview::getPoint).sum() + review.getStar();
            long totalReviews = detailReviews.size() + 1; // Thêm review mới

            // ✅ Tính trung bình điểm mới
            double newAveragePoint = totalPoints / totalReviews;

            // ✅ Lưu vào DetailReview mới
            DetailReview detailReview = new DetailReview();
            detailReview.setUser(user);
            detailReview.setCriteria(criteria);
            detailReview.setPoint(newAveragePoint); // Lưu trung bình vào point
            detailReviewRepository.save(detailReview);
        }
    }


}
