package com.mission.store.domain;

import com.mission.store.type.MemberStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
@RequiredArgsConstructor
public class MemberDetails implements UserDetails {

    private final Member member;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        /*
         * 권한 -> (손님, 점주)
         * 손님은 점주의 기능 사용 불가능하나 점주는 손님의 기능 사용 가능, 권한을 리스트 형식이 아닌 하나로 관리
         * TODO 권한이 추가된다면 리스트로 변경되어야 할 수 있음
         */
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(member.getMemberRole().getAuthority());
        return Collections.singleton(grantedAuthority);
    }

    // 사용자 암호화된 비밀번호를 반환
    @Override
    public String getPassword() {
        return member.getPassword();
    }

    // 사용자 고유 식별자 id 반환
    @Override
    public String getUsername() {
        return member.getId();
    }

    // 사용자 계정 만료여부 반환
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // 사용자 계정이 잠금 여부 반환
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // 사용자 인증 정보(비밀번호)가 만료되었는지 여부를 반환
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // 사용자 계정 사용 가능여부 확인
    @Override
    public boolean isEnabled() {
        return member.getMemberStatus() == MemberStatus.ACTIVE;
    }
}
