package com.mission.store.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReservationApprovalStatus {

    PENDING("승인 대기"),
    APPROVED("승인"),
    REJECTED("거절")

    ;

    private final String description;
}
