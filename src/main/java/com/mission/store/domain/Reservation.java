package com.mission.store.domain;

import com.mission.store.type.ReservationApprovalStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Reservation extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", referencedColumnName = "id", nullable = false)
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", referencedColumnName = "id", nullable = false)
    private Member customer;

    private LocalDate reservationDate; // 예약 시간(2023.06.22)
    private String reservationTime; // 예약 시간(13:00)
    private String reservationMemo; // 예약 메모
    private int numberOfCustomer; // 예약 인원 수

    private String reservationNumber; // 예약번호
    private String reservationCode; // 예약코드

    @Enumerated(EnumType.STRING)
    private ReservationApprovalStatus reservationApprovalStatus; // 예약 승인 상태
}
