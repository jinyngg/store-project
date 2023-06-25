package com.mission.store.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StoreStatus {

    OPEN("영업")
//    , CLOSED("휴업")
    , OUT_OF_BUSINESS("폐업")

    ;

    private final String description;

}
