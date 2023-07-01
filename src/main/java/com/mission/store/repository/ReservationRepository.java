package com.mission.store.repository;

import com.mission.store.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    // 예약 여부 확인
    boolean existsByReservationTime(String reservationTime);
}
