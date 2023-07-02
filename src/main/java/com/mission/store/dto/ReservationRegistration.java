package com.mission.store.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

public class ReservationRegistration {

    @Getter
    @Builder
    public static class Request {
        @NotNull(message = "매장 정보는 필수 입력 항목입니다.")
        private Long storeId;
        @NotNull(message = "손님 정보는 필수 입력 항목입니다.")
        private Long customerId;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        private LocalDate reservationDate; // 예약 시간(2023.06.22)
        @NotBlank(message = "예약 시간은 필수 입력 항목입니다.")
        @Pattern(regexp = "\\d{2}:\\d{2}"
                , message = "올바른 형식(HH:mm)으로 입력해주세요. (예시:13:00)")
        private String reservationTime; // 예약 시간(13:00)
        private String reservationMemo; // 예약 메모
        @Min(value = 1, message = "예약 인원 수는 최소 1명 이상이어야 합니다.")
        private int numberOfCustomer; // 예약 인원 수
    }

    @Getter
    @Builder
    public static class Response {
        private Long id;
        private String reservationCode; // 예약코드(임의의 4개의 숫자("1189"))
    }

}
