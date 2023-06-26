package com.mission.store.dto;

import com.mission.store.domain.Member;
import com.mission.store.type.MemberRole;
import com.mission.store.type.MemberStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Owner {

    private Long id;
    private String email;
    private String phone;
    private String nickname;

    private MemberStatus memberStatus;
    private MemberRole memberRole;

    public static Owner fromEntity(Member member) {
        return Owner.builder()
                .id(member.getId())
                .email(member.getEmail())
                .phone(member.getPhone())
                .nickname(member.getNickname())
                .memberStatus(member.getMemberStatus())
                .memberRole(member.getMemberRole())
                .build();
    }
}
