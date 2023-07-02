package com.mission.store.repository;

import com.mission.store.domain.Reservation;
import com.mission.store.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    // 작성된 리뷰가 존재하는지 확인
    Review findByReservation(Reservation reservation);

}
