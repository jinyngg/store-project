package com.mission.store.dto;

import com.mission.store.domain.Reservation;
import com.mission.store.type.ReservationApprovalStatus;
import com.mission.store.type.ReservationVisitStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class ReservationDto {

    private Long id;
//    private Store store;
    private Customer customer;

    private LocalDate reservationDate; // 예약 시간(2023.06.22)
    private String reservationTime; // 예약 시간(13:00)
    private String reservationMemo; // 예약 메모
    private int numberOfCustomer; // 예약 인원 수

    private ReservationVisitStatus reservationVisitStatus; // 방문 상태
    private ReservationApprovalStatus reservationApprovalStatus; // 예약 승인 상태

    public static ReservationDto fromEntity(Reservation reservation) {
        return ReservationDto.builder()
                .id(reservation.getId())
//                .store(reservation.getStore())
                .customer(Customer.fromEntity(reservation.getCustomer()))
                .reservationDate(reservation.getReservationDate())
                .reservationTime(reservation.getReservationTime())
                .reservationMemo(reservation.getReservationMemo())
                .numberOfCustomer(reservation.getNumberOfCustomer())
                .reservationVisitStatus(reservation.getReservationVisitStatus())
                .reservationApprovalStatus(reservation.getReservationApprovalStatus())
                .build();
    }
}
