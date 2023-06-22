package com.mission.store.domain;

import com.mission.store.type.ReservationApprovalStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

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

     private int numberOfCustomer; // 예약 인원 수
     private String reservationMemo; // 예약 메모

    @Enumerated(EnumType.STRING)
    private ReservationApprovalStatus reservationApprovalStatus; // 예약 승인 상태

    private LocalDateTime requestedAt; // 예약 요청 시간
    private String reservedAt; // 예약 시간(23-06-22 오후 1시)
}
