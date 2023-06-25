package com.mission.store.domain;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class RefreshToken extends BaseEntity {

    @Column(name = "member_id", nullable = false)
    private Long key;

    @Column(nullable = false)
    private String token;

    public RefreshToken updateToken(String token) {
        this.token = token;
        return this;
    }
}
