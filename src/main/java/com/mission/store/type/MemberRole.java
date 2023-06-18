package com.mission.store.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MemberRole {

    OWNER("ROLE_OWNER", "매장 점주")
    , CUSTOMER("ROLE_CUSTOMER", "매장 손님")

    ;

    private final String authority;
    private final String description;
}
