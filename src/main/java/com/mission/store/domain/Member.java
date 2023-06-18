package com.mission.store.domain;

import com.mission.store.type.MemberStatus;
import com.mission.store.type.MemberRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Member extends BaseEntity {

    @Id
    @Column(unique = true)
    private String id;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String phone;
    @Column(nullable = false)
    private String nickname;
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private MemberStatus memberStatus;
    @Enumerated(EnumType.STRING)
    private MemberRole memberRole;

    private LocalDateTime registeredAt;
    private LocalDateTime unregisteredAt;

}
