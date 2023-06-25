package com.mission.store.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberLogin {

    private String email;
    private String password;

}
