package com.mission.store.service;

import com.mission.store.dto.ReservationDto;
import com.mission.store.dto.ReservationRegistration;
import com.mission.store.type.ReservationApprovalStatus;

import java.util.List;

public interface ReservationService {
    ReservationRegistration.Response reserve(ReservationRegistration.Request request);

    void cancelReservation(Long reservationId);

    void confirmVisit(Long reservationId, String reservationCode);

    List<ReservationDto> getReservationsByStoreId(Long storeId);

    void approveOrRejectReservation(Long reservationId, ReservationApprovalStatus approvalStatus);
}