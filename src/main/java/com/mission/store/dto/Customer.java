package com.mission.store.dto;

import com.mission.store.domain.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Customer {

    private Long id;
    private String phone;
    private String nickname;

    public static Customer fromEntity(Member member) {
        return Customer.builder()
                .id(member.getId())
                .phone(member.getPhone())
                .nickname(member.getNickname())
                .build();
    }
}
