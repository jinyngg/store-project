package com.mission.store.repository;

import com.mission.store.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    // 예약 중복 여부 확인
    boolean existsByReservationDateAndReservationTime(LocalDate reservationDate, String reservationTime);
}
