package com.mission.store.controller;

import com.mission.store.dto.ReviewRegistration;
import com.mission.store.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RequestMapping("/store/api/v1")
@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    
    /** 리뷰 작성 */
    @PostMapping("/reviews")
    public ResponseEntity<?> createReview(@Valid @RequestBody ReviewRegistration request) {
        reviewService.writeReview(request);
        return ResponseEntity.ok().build();
    }
}
