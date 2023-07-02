package com.mission.store.service;

import com.mission.store.domain.Member;
import com.mission.store.domain.RefreshToken;
import com.mission.store.dto.MemberLogin;
import com.mission.store.dto.MemberRegistration;
import com.mission.store.dto.TokenDto;
import com.mission.store.dto.TokenRequestDto;
import com.mission.store.jwt.JwtProvider;
import com.mission.store.repository.MemberRepository;
import com.mission.store.repository.RefreshTokenRepository;
import com.mission.store.type.MemberStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    /** 회원가입 */
    @Transactional
    public void register(MemberRegistration request) {
        // 1. 이메일 중복 체크
        memberRepository.findByEmail(request.getEmail()).ifPresent(member -> {
            throw new RuntimeException("이미 사용중인 이메일입니다.");
        });

        // 2. 전화번호 중복 체크
        memberRepository.findByPhone(request.getPhone()).ifPresent(member -> {
            throw new RuntimeException("이미 사용중인 전화번호입니다.");
        });

        // 3. 닉네임 중복 체크
        memberRepository.findByNickname(request.getNickname()).ifPresent(member -> {
            throw new RuntimeException("이미 사용중인 닉네임입니다.");
        });

        // 4. 패스워드 암호화 후 저장
        String encryptionPassword = passwordEncoder.encode(request.getPassword());

        memberRepository.save(Member.builder()
                .email(request.getEmail())
                .phone(request.getPhone())
                .nickname(request.getNickname())
//                .password(request.getPassword())
                .password(encryptionPassword)
                .memberStatus(MemberStatus.ACTIVE)
                .memberRole(request.getMemberRole())
                .registeredAt(LocalDateTime.now())
                .build());

    }

    /** 로그인 */
    @Transactional
    public MemberLogin.Response login(MemberLogin.Request request) {
        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 email 입니다."));

        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        // Token(Access + Refresh) 발급
        TokenDto tokenDto = jwtProvider.GenerateToken(member.getPhone(), member.getMemberRole());
        String refreshToken = tokenDto.getRefreshToken();
        Long memberId = member.getId();

        RefreshToken currentToken = refreshTokenRepository.findByKey(memberId).orElse(null);

        if (currentToken != null) {
            currentToken.updateToken(refreshToken);
            refreshTokenRepository.save(currentToken);
        } else {
            refreshTokenRepository.save(
                    RefreshToken.builder()
                            .key(memberId)
                            .token(refreshToken)
                            .build());
        }

        return MemberLogin.Response.builder()
                .id(memberId)
                .token(tokenDto)
                .build();
    }

    /** 토큰 재발급(자동 로그인) */
    @Transactional
    public TokenDto refresh(TokenRequestDto request) {
        if (!jwtProvider.validateToken(request.getRefreshToken())) {
            throw new RuntimeException("리프레시 토큰이 만료되었습니다.");
        }

        // authentication 정보 가져오기
        String accessToken = request.getAccessToken();
        Authentication authentication = jwtProvider.getAuthentication(accessToken);

        // authentication.getName() -> 해당 JWT 포함된 Subject 이름 반환
        Member member = memberRepository.findByPhone(authentication.getName())
                .orElseThrow(() -> new RuntimeException("해당 전화번호로 가입된 계정이 없습니다.")
                );

        // 저장된 리프레시 토큰이 없을 경우 에러 발생
        RefreshToken refreshToken = refreshTokenRepository.findByKey(member.getId())
                .orElseThrow(() -> new RuntimeException("계정에 저장된 리프레시 토큰이 없습니다."));

        // 리프레시 토큰 불일치 에러
        if (!refreshToken.getToken().equals(request.getRefreshToken()))
            throw new RuntimeException("리프레시 토큰이 일치하지 않습니다.");

        // AccessToken, RefreshToken 토큰 재발급, 리프레쉬 토큰 저장
        TokenDto newGenerateToken = jwtProvider.GenerateToken(member.getPhone(), member.getMemberRole());
        RefreshToken updateRefreshToken = refreshToken.updateToken(newGenerateToken.getRefreshToken());
        refreshTokenRepository.save(updateRefreshToken);

        return newGenerateToken;

    }

}
