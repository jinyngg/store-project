package com.mission.store.domain;

import com.mission.store.type.MemberStatus;
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
    @GeneratedValue
    private String id;
    private String email;
    private String phone;
    private String nickname;
    private String password;

    @Enumerated(EnumType.STRING)
    private MemberStatus memberStatus;

    private LocalDateTime registeredAt;
    private LocalDateTime unRegisteredAt;

}
