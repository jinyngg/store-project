package com.mission.store.service;

import com.mission.store.domain.Member;
import com.mission.store.domain.MemberDetails;
import com.mission.store.repository.MemberRepository;
import com.mission.store.type.MemberStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String phone) throws UsernameNotFoundException {
        Member member = memberRepository.findByPhone(phone)
                .orElseThrow(() -> new UsernameNotFoundException("회원 정보가 존재하지 않습니다. 토큰을 확인해주세요."));

        // 정지된 계정일 경우 예외 처리
        if (member.getMemberStatus() == MemberStatus.BLOCKED) {
            /* 정지된 계정입니다. */
            throw new RuntimeException(MemberStatus.BLOCKED.getDescription());
        }

        return new MemberDetails(member);
    }
}
