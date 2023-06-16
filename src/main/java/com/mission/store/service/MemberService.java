package com.mission.store.service;

import com.mission.store.domain.Member;
import com.mission.store.dto.MemberDto;
import com.mission.store.dto.MemberRegistration;
import com.mission.store.repository.MemberRepository;
import com.mission.store.type.MemberStatus;
import com.mission.store.type.MemberType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;

    /** 회원가입 */
    public MemberDto register(MemberRegistration.Request request) {

        // 아이디 중복 체크
        memberRepository.findById(request.getId()).ifPresent(member -> {
            throw new RuntimeException("이미 존재하는 아이디입니다.");
        });

        String encPassword = BCrypt.hashpw(request.getPassword(), BCrypt.gensalt());

        Member member = memberRepository.save(Member.builder()
                .id(request.getId())
                .email(request.getEmail())
                .phone(request.getPhone())
                .nickname(request.getNickname())
//                .password(request.getPassword())
                .password(encPassword)
                .memberStatus(MemberStatus.ACTIVE)
                .memberType(request.getMemberType())
                .registeredAt(LocalDateTime.now())
                .build());

        return MemberDto.fromEntity(member);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Member member = memberRepository.findById(username)
                .orElseThrow(() -> new UsernameNotFoundException("회원 정보가 존재하지 않습니다."));

        // 정지된 계정일 경우 예외 처리
        if (member.getMemberStatus().equals(MemberStatus.BLOCKED)) {
            throw new RuntimeException(MemberStatus.BLOCKED.getDescription());
        }

        // 권한 설정
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        grantedAuthorities.add(new SimpleGrantedAuthority(MemberType.CUSTOMER.getAuthority()));

        if (member.getMemberType().equals(MemberType.OWNER)) {
            grantedAuthorities.add(new SimpleGrantedAuthority(MemberType.OWNER.getAuthority()));
        }

        return new User(member.getId(), member.getPassword(), grantedAuthorities);
    }
}
