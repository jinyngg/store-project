package com.mission.store.service;

import com.mission.store.domain.Member;
import com.mission.store.dto.MemberDto;
import com.mission.store.dto.MemberRegistration;
import com.mission.store.repository.MemberRepository;
import com.mission.store.type.MemberStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    /** 회원가입 */
    public MemberDto register(MemberRegistration.Request request) {

        /**
         * 1. 아이디 중복 체크
         * 2. 이메일 중복 체크
         * 3. 패스워드 암호화 후 저장
         * TODO 23.06.17 휴대폰 번호 횟수 제안 고민할 것(1개의 번호로 만들 수 있는 계정의 개수 제안)
         */

        // 아이디 중복 체크
        memberRepository.findById(request.getId()).ifPresent(member -> {
            throw new RuntimeException("이미 사용중인 아이디입니다.");
        });

        // 이메일 중복 체크
        memberRepository.findByEmail(request.getEmail()).ifPresent(member -> {
            throw new RuntimeException("이미 사용중인 이메일입니다.");
        });

        // 전화번호 중복 체크
        memberRepository.findByPhone(request.getPhone()).ifPresent(member -> {
            throw new RuntimeException("이미 사용중인 전화번호입니다.");
        });

        // 암호화된 비밀번호
        String encPassword = BCrypt.hashpw(request.getPassword(), BCrypt.gensalt());
//        String encPassword = passwordEncoder.encode(request.getPassword());

        Member member = memberRepository.save(Member.builder()
                .id(request.getId())
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

}
