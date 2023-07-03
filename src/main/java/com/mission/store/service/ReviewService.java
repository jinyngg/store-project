package com.mission.store.service;

import com.mission.store.domain.Reservation;
import com.mission.store.domain.Review;
import com.mission.store.domain.Store;
import com.mission.store.dto.ReviewRegistration;
import com.mission.store.repository.ReservationRepository;
import com.mission.store.repository.ReviewRepository;
import com.mission.store.repository.StoreRepository;
import com.mission.store.type.ReviewStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReservationRepository reservationRepository;
    private final StoreRepository storeRepository;

    /** 리뷰 작성 */
    public void review(ReviewRegistration request) {
        // 1. 예약 번호 확인
        Reservation reservation = reservationRepository.findById(request.getReservationId())
                .orElseThrow(() -> new RuntimeException("올바르지 않은 예약번호입니다."));

        // 2. 리뷰어가 예약한 매장 손님인지 확인
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedUserEmail = authentication.getName();
        String customerEmail = reservation.getCustomer().getEmail();
        if (!Objects.equals(customerEmail, authenticatedUserEmail)) {
            throw new RuntimeException("리뷰 작성은 본인만 가능합니다.");
        }

        // 3. 이미 작성된 리뷰가 있는지 확인
        Review review = reviewRepository.findByReservation(reservation);
        if (review != null) {
            throw new RuntimeException("이미 리뷰를 작성한 예약입니다.");
        }

        // 4. 리뷰어가 작성할 수 있는 리뷰 상태 확인
        ReviewStatus reviewStatus = request.getReviewStatus();
        if (!(reviewStatus == ReviewStatus.PUBLIC || reviewStatus == ReviewStatus.OWNER_PUBLIC)) {
            throw new RuntimeException("공개 또는 점주 공개만 가능합니다.");
        }

        // 5. 리뷰 작성
        reviewRepository.save(Review.builder()
                .store(reservation.getStore())
                .reviewer(reservation.getCustomer())
                .reservation(reservation)
                .message(request.getMessage())
                .rating(request.getRating())
                .reviewStatus(reviewStatus)
                .visitedDate(reservation.getReservationDate())
                .build());
        
        // 상점 리뷰 개수, 상점 리뷰 평점 업데이트
        Store store = reservation.getStore();
        int reviewCount = store.getReviewCount();
        double averageRating =  store.getAverageRating();

        // 1. 리뷰 개수 증가
        reviewCount++;
        store.updateReviewCount(reviewCount);

        // 2. 리뷰 평점 업데이트
        double totalRating = averageRating * (reviewCount - 1);
        double newRating = request.getRating();
        averageRating = (totalRating + newRating) / reviewCount;
        store.updateAverageRating(averageRating);

        // 3. 리뷰 적용 후 변경
        storeRepository.save(store);
    }
}
