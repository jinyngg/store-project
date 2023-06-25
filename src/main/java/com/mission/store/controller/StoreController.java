package com.mission.store.controller;

import com.mission.store.dto.StoreRegistration;
import com.mission.store.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequestMapping("/store/api/v1")
@RestController
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    /** 매장 등록 */
    @PostMapping("/store/register/{memberId}")
    public ResponseEntity<?> register(
            @PathVariable Long memberId
            , @Valid @RequestBody StoreRegistration request) {

        // TODO BindingResult 공부
        storeService.registerStore(memberId, request);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
