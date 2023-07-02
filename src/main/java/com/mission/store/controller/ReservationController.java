package com.mission.store.controller;

import com.mission.store.dto.ReservationRegistration;
import com.mission.store.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequestMapping("/store/api/v1")
@RestController
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    /** 예약 요청 */
    @PostMapping("/reservations")
    public ResponseEntity<?> reserve(
            @Valid @RequestBody ReservationRegistration.Request request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservationService.reserve(request));
    }

    /** 예약 취소 */
    @PutMapping("/reservations/{reservationId}/cancel")
    public ResponseEntity<?> cancelReservation(@PathVariable Long reservationId) {
        reservationService.cancelReservation(reservationId);
        return ResponseEntity.ok().build();
    }
    
    /** 키오스크 예약 방문 확인 */
    @PutMapping("/reservations/{reservationId}/kiosk/confirm")
    public ResponseEntity<?> confirmVisit(
            @PathVariable Long reservationId
            , @RequestParam("reservationCode") String reservationCode) {
        reservationService.confirmVisit(reservationId, reservationCode);
        return ResponseEntity.ok().build();
    }
}
