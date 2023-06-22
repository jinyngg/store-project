package com.mission.store.domain;

import com.mission.store.type.MemberStatus;
import com.mission.store.type.MemberRole;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Member extends BaseEntity {

    @Column(nullable = false)
    private String email;
    @Column(nullable = false, unique = true)
    private String phone;
    @Column(nullable = false)
    private String nickname;
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberStatus memberStatus; // 활성화, 비활성화, 정지, 탈퇴
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberRole memberRole; // CUSTOMER, OWNER, TODO ADMIN

    private LocalDateTime registeredAt; // TODO 가입 인증 후 회원 활성화 등록 시간
    private LocalDateTime unregisteredAt; // BLOCKED(정지), WITHDRAWN(탈퇴) 일자
}
