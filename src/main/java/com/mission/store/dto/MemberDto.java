package com.mission.store.dto;

import com.mission.store.domain.Member;
import com.mission.store.type.MemberRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class MemberDto {

    private String id;
    private String email;
    private String phone;
    private String nickname;
    private String password;

    private MemberRole memberRole;

    private LocalDateTime registeredAt;
    private LocalDateTime unregisteredAt;

    public static MemberDto fromEntity(Member member) {
        return MemberDto.builder()
                .id(member.getId())
                .email(member.getEmail())
                .phone(member.getPhone())
                .nickname(member.getNickname())
                .memberRole(member.getMemberRole())
                .registeredAt(member.getRegisteredAt())
                .unregisteredAt(member.getUnregisteredAt())
                .build();
    }
}
