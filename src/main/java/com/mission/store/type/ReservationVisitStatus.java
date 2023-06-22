package com.mission.store.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReservationVisitStatus {

    VISITED("방문 완료"),
    NOT_VISITED("방문 안 함")

    ;

    private final String description;
}
