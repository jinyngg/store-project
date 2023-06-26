package com.mission.store.dto;

import lombok.Builder;
import lombok.Getter;

public class MemberLogin {

    @Getter
    @Builder
    public static class Request {

        private String email;
        private String password;
    }

    @Getter
    @Builder
    public static class Response {

        private Long id;
        private TokenDto token;

    }


}
