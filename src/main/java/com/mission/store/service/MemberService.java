package com.mission.store.service;

import com.mission.store.domain.Member;
import com.mission.store.dto.MemberDto;
import com.mission.store.dto.MemberLogin;
import com.mission.store.dto.MemberRegistration;
import com.mission.store.repository.MemberRepository;
import com.mission.store.type.MemberStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    /** 회원가입 */
    @Transactional
    public MemberDto register(MemberRegistration.Request request) {

        /*
         * 1. 이메일 중복 체크
         * 2. 전화번호 중복 체크
         * 3. 패스워드 암호화 후 저장
         */
        // 이메일 중복 체크
        memberRepository.findByEmail(request.getEmail()).ifPresent(member -> {
            throw new RuntimeException("이미 사용중인 이메일입니다.");
        });

        // 전화번호 중복 체크
        memberRepository.findByPhone(request.getPhone()).ifPresent(member -> {
            throw new RuntimeException("이미 사용중인 전화번호입니다.");
        });

        // 암호화된 비밀번호
//        String encPassword = BCrypt.hashpw(request.getPassword(), BCrypt.gensalt());
        String encPassword = passwordEncoder.encode(request.getPassword());

        Member member = memberRepository.save(Member.builder()
                .email(request.getEmail())
                .phone(request.getPhone())
                .nickname(request.getNickname())
//                .password(request.getPassword())
                .password(encPassword)
                .memberStatus(MemberStatus.ACTIVE)
                .memberRole(request.getMemberRole())
                .registeredAt(LocalDateTime.now())
                .build());

        return MemberDto.fromEntity(member);
    }

    @Transactional
    public MemberDto login(MemberLogin.Request request) {
        Member member = memberRepository.findByEmail(request.getEmail()).orElseThrow(() ->
                new RuntimeException("존재하지 않는 email 입니다."));

        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        return MemberDto.fromEntity(member);
    }

}
