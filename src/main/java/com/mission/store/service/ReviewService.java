package com.mission.store.service;

import com.mission.store.dto.ReviewRegistration;

public interface ReviewService {
    void writeReview(ReviewRegistration request);
}