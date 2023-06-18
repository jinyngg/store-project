package com.mission.store.dto;

import com.mission.store.type.MemberRole;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

public class MemberRegistration {

    @Getter
    @Builder
    public static class Request {

        @NotBlank(message = "아이디가 누락되었습니다.")
        @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9]*$"
                , message = "아이디는 영어와 숫자로만 구성되어야 하며, 첫 시작은 영문자로 작성해야 합니다.")
        @Size(min = 6, max = 12, message = "아이디는 6자 이상 12자 이하로 작성해야 합니다.")
        private String id;
        @NotBlank(message = "이메일이 누락되었습니다.")
        @Email(message = "이메일 주소 형식이 잘못되었습니다.")
        private String email;
        @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$"
                , message = "전화번호 형식이 맞지 않습니다.(01X-XXX(X)-XXXX)")
        private String phone;
        @NotBlank(message = "닉네임이 누락되었습니다.")
        @Pattern(regexp = "^(?!\\s*$).+", message = "닉네임에 공백이 포함될 수 없습니다.")
        private String nickname;
        @Size(min = 8, message = "비밀번호는 최소 8자리 이상이어야 합니다.")
        @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?!.*\\s).+$"
                , message = "비밀번호는 영어와 숫자를 혼용해야 하며 공백은 사용할 수 없습니다.")
        private String password;
        private MemberRole memberRole;
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
