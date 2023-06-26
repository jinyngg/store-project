package com.mission.store.dto;

import com.mission.store.domain.Member;
import com.mission.store.type.MemberRole;
import com.mission.store.type.MemberStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MemberDto {

    private Long id;
    private String email;
    private String phone;
    private String nickname;
    private String password;

    private MemberStatus memberStatus;
    private MemberRole memberRole;

    private LocalDateTime registeredAt;
    private LocalDateTime unregisteredAt;

    public static MemberDto fromEntity(Member member) {
        return MemberDto.builder()
                .id(member.getId())
                .email(member.getEmail())
                .phone(member.getPhone())
                .nickname(member.getNickname())
                .memberStatus(member.getMemberStatus())
                .memberRole(member.getMemberRole())
                .registeredAt(member.getRegisteredAt())
                .unregisteredAt(member.getUnregisteredAt())
                .build();
    }
}
