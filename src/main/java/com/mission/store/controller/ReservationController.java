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

    private ReservationService reservationService;

    /** 당일 예약 요청 */
    @PostMapping("/reservation")
    public ResponseEntity<?> register(
            @Valid @RequestBody ReservationRequest request) {
        reservationService.reserve(request);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
