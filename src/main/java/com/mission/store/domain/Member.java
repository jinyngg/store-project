package com.mission.store.domain;

import com.mission.store.type.MemberStatus;
import com.mission.store.type.MemberType;
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
    private String id;
    private String email;
    private String phone;
    private String nickname;
    private String password;

    @Enumerated(EnumType.STRING)
    private MemberStatus memberStatus;
    @Enumerated(EnumType.STRING)
    private MemberType memberType;

    private LocalDateTime registeredAt;
    private LocalDateTime unRegisteredAt;

}
