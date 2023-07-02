package com.mission.store.dto;

import com.mission.store.type.ReviewStatus;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.*;

@Getter
@Builder
public class ReviewRegistration {

    @NotNull(message = "예약 ID는 필수 값입니다.")
    private Long reservationId;

    @NotBlank(message = "상점 리뷰는 필수 값입니다.")
    @Size(min = 5, max = 100, message = "상점 리뷰는 5자 이상 100자 이하로 작성해야 합니다.")
    private String message;

    @PositiveOrZero(message = "상점 평점은 0.0 이상 5.0 이하의 소수점 한 자리를 입력해야 합니다.")
    @DecimalMin(value = "0.0", message = "상점 평점은 0.0 이상이어야 합니다.")
    @DecimalMax(value = "5.0", message = "상점 평점은 5.0 이하여야 합니다.")
    @Digits(integer = 1, fraction = 1, message = "상점 평점은 소수점 한 자리를 입력해야 합니다.")
    private float rating;

    @NotNull(message = "리뷰 공개 여부는 필수 값입니다.")
    private ReviewStatus reviewStatus;
}
