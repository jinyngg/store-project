package com.mission.store.dto;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class StoreRegistration {

    @Getter
    @Builder
    public static class Request {
        @NotBlank(message = "상점 이름이 누락되었습니다.")
        private String name; // 상점 이름
        @NotBlank(message = "상점 주소가 누락되었습니다.")
        private String address; // 상점 주소
        @NotBlank(message = "상점 설명이 누락되었습니다.")
        private String description; // 상점 설명

        @DecimalMin(value = "-90.0", inclusive = true, message = "유효하지 않은 위도 값입니다.")
        @DecimalMax(value = "90.0", inclusive = true, message = "유효하지 않은 위도 값입니다.")
        private double lat; // 위도
        @DecimalMin(value = "-180.0", inclusive = true, message = "유효하지 않은 경도 값입니다.")
        @DecimalMax(value = "180.0", inclusive = true, message = "유효하지 않은 경도 값입니다.")
        private double lon; // 경도

        @Pattern(regexp = "\\d{2}:\\d{2} - \\d{2}:\\d{2}"
                , message = "유효하지 않은 영업 시간 형식입니다. (09:00 - 18:00)")
        private String businessHours; // 영업 시간(09:00 - 18:00)
        @Pattern(regexp = "\\d{2}:\\d{2} - \\d{2}:\\d{2}"
                , message = "유효하지 않은 휴무 시간 형식입니다. (15:00 - 16:00)")
        private String breakTime; // 휴무 시간(15:00 - 16:00)
    }

    @Getter
    @Builder
    public static class Response {
        private Long id;
    }
}
