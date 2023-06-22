package com.mission.store.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MemberStatus {

    ACTIVE("활성화된 계정입니다.")
//    , INACTIVE("비활성화된 계정입니다.")
    , BLOCKED("정지된 계정입니다.")
    , WITHDRAWN("탈퇴된 계정입니다.")

    ;

    private final String description;

}
