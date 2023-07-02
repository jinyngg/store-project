package com.mission.store.repository;

import com.mission.store.domain.Reservation;
import com.mission.store.domain.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    // 예약 중복 여부 확인
    boolean existsByReservationDateAndReservationTime(LocalDate reservationDate, String reservationTime);

    // 매장과 관련된 모든 예약 조회
    List<Reservation> findByStore(Store store);
}
