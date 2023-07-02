package com.mission.store.controller;

import com.mission.store.dto.ReservationRequest;
import com.mission.store.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RequestMapping("/store/api/v1")
@RestController
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    /** 예약 생성 */
    @PostMapping("/reservation")
    public ResponseEntity<?> reserve(
            @Valid @RequestBody ReservationRequest request) {
        reservationService.reserve(request);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
