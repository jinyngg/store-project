package com.mission.store.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReservationVisitStatus {
    
    NOT_VISITED("방문 전")
    , VISITED_WITHIN_RESERVATION_TIME("예약시간 내 방문")
    , CANCELLED_NOT_VISITED("취소로 인한 미방문")
    , CANCELLED_NO_SHOW("노쇼로 인한 미방문")

    ;

    private final String description;
}