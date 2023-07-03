package com.mission.store.service.impl;

import com.mission.store.domain.Reservation;
import com.mission.store.domain.Review;
import com.mission.store.domain.Store;
import com.mission.store.dto.ReviewRegistration;
import com.mission.store.exception.ReservationException;
import com.mission.store.exception.ReviewException;
import com.mission.store.repository.ReservationRepository;
import com.mission.store.repository.ReviewRepository;
import com.mission.store.repository.StoreRepository;
import com.mission.store.service.ReviewService;
import com.mission.store.type.ReviewStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static com.mission.store.type.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReservationRepository reservationRepository;
    private final StoreRepository storeRepository;

    /** 리뷰 작성 */
    @Override
    @Transactional
    public void writeReview(ReviewRegistration request) {
        // 1. 예약 정보 가져오기
        Reservation reservation = getReservationById(request.getReservationId());
        
        // 2. 유효성 검사
        validateReviewer(reservation);
        validateReviewNotExist(reservation);
        validateReviewStatus(request.getReviewStatus());

        // 3. 리뷰 저장
        saveReview(reservation, request);

        // 4. 상점 리뷰 정보 업데이트
        updateStoreReview(reservation.getStore(), request.getRating());
    }

    /** 예약 번호에 해당하는 예약 정보 조회 */
    private Reservation getReservationById(Long reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationException(INVALID_RESERVATION_ID));
    }

    /** 리뷰어가 예약한 매장 손님이 맞는지 확인 */
    private void validateReviewer(Reservation reservation) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedUserEmail = authentication.getName();
        String customerEmail = reservation.getCustomer().getEmail();
        if (!Objects.equals(customerEmail, authenticatedUserEmail)) {
            throw new ReviewException(ACCESS_DENIED_FOR_REVIEW);
        }
    }

    /** 이미 작성된 리뷰가 존재하는지 확인 */
    private void validateReviewNotExist(Reservation reservation) {
        Review existingReview = reviewRepository.findByReservation(reservation);
        if (existingReview != null) {
            throw new ReviewException(ALREADY_WRITTEN_REVIEW);
        }
    }

    /** 리뷰 상태가 유효한지 확인(리뷰는 '공개'와 '점주 공개'만 가능) */
    private void validateReviewStatus(ReviewStatus reviewStatus) {
        if (!(reviewStatus == ReviewStatus.PUBLIC || reviewStatus == ReviewStatus.OWNER_PUBLIC)) {
            throw new ReviewException(INVALID_REVIEW_TYPE);
        }
    }

    /** 리뷰 저장 */
    private void saveReview(Reservation reservation, ReviewRegistration request) {
        Review review = Review.builder()
                .store(reservation.getStore())
                .reviewer(reservation.getCustomer())
                .reservation(reservation)
                .message(request.getMessage())
                .rating(request.getRating())
                .reviewStatus(request.getReviewStatus())
                .visitedDate(reservation.getReservationDate())
                .build();

        reviewRepository.save(review);
    }

    /** 상점의 리뷰 정보 업데이트 */
    private void updateStoreReview(Store store, double newRating) {
        int reviewCount = store.getReviewCount() + 1;
        double totalRating = store.getAverageRating() * store.getReviewCount();
        double averageRating = (totalRating + newRating) / reviewCount;

        store.updateReviewCount(reviewCount);
        store.updateAverageRating(averageRating);

        storeRepository.save(store);
    }

}
