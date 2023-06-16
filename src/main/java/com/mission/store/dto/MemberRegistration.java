package com.mission.store.dto;

import com.mission.store.type.MemberType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class MemberRegistration {

    @Getter
    @Builder
    public static class Request {

        private String id;
        private String email;
        private String phone;
        private String nickname;
        private String password;
        private MemberType memberType;
    }

    @Getter
    @Builder
    public static class Response {

        private String id;
        private String nickname;
        private LocalDateTime registeredAt;

        public static Response from(MemberDto memberDto) {
            return Response.builder()
                    .id(memberDto.getId())
                    .nickname(memberDto.getNickname())
                    .registeredAt(memberDto.getRegisteredAt())
                    .build();
        }
    }
}
