package com.mission.store.service;

import com.mission.store.domain.Member;
import com.mission.store.domain.RefreshToken;
import com.mission.store.dto.MemberLogin;
import com.mission.store.dto.MemberRegistration;
import com.mission.store.dto.TokenDto;
import com.mission.store.dto.TokenRequestDto;
import com.mission.store.exception.MemberException;
import com.mission.store.exception.RefreshTokenException;
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

import static com.mission.store.type.ErrorCode.*;

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
            throw new MemberException(ALREADY_EXISTS_EMAIL);
        });

        // 2. 전화번호 중복 체크
        memberRepository.findByPhone(request.getPhone()).ifPresent(member -> {
            throw new MemberException(ALREADY_EXISTS_PHONE);
        });

        // 3. 닉네임 중복 체크
        memberRepository.findByNickname(request.getNickname()).ifPresent(member -> {
            throw new MemberException(ALREADY_EXISTS_NICKNAME);
        });

        // 4. 패스워드 암호화 후 저장
        String encryptionPassword = passwordEncoder.encode(request.getPassword());

        memberRepository.save(Member.builder()
                .email(request.getEmail())
                .phone(request.getPhone())
                .nickname(request.getNickname())
                .password(encryptionPassword)
                .memberStatus(MemberStatus.ACTIVE)
                .memberRole(request.getMemberRole())
                .registeredAt(LocalDateTime.now())
                .build());

    }

    /** 로그인 */
    @Transactional
    public MemberLogin.Response login(MemberLogin.Request request) {
        // 1. 이메일 정보 확인
        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new MemberException(INVALID_EMAIL));

        // 2. 입력한 비밀번호가 올바른지 확인
        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new MemberException(INVALID_PASSWORD);
        }

        // 3. 토큰 발급
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

        // 4. 로그인
        return MemberLogin.Response.builder()
                .id(memberId)
                .token(tokenDto)
                .build();
    }

    /** 토큰 재발급 */
    @Transactional
    public TokenDto refreshToken(TokenRequestDto request) {
        // 1. 만료된 토큰인지 확인
        if (!jwtProvider.validateToken(request.getRefreshToken())) {
            throw new RefreshTokenException(EXPIRED_REFRESH_TOKEN);
        }

        // 2. authentication 정보 가져오기
        String accessToken = request.getAccessToken();
        Authentication authentication = jwtProvider.getAuthentication(accessToken);

        // 3. 토큰 서브젝트 올바른지 확인
        Member member = memberRepository.findByPhone(authentication.getName()) // 해당 JWT 포함된 Subject 이름 반환
                .orElseThrow(() -> new MemberException(INVALID_PHONE));

        // 4. 저장된 리프레시 토큰이 있는지 확인
        RefreshToken refreshToken = refreshTokenRepository.findByKey(member.getId())
                .orElseThrow(() -> new RefreshTokenException(NO_REFRESH_TOKEN_FOUND));

        // 5. 리프레시 토큰이 일치하는지 확인
        if (!refreshToken.getToken().equals(request.getRefreshToken()))
            throw new RefreshTokenException(MISMATCHED_REFRESH_TOKEN);

        // 6. 토큰 재발급, 리프레쉬 토큰 저장
        TokenDto newGenerateToken = jwtProvider.GenerateToken(member.getPhone(), member.getMemberRole());
        RefreshToken updateRefreshToken = refreshToken.updateToken(newGenerateToken.getRefreshToken());
        refreshTokenRepository.save(updateRefreshToken);

        return newGenerateToken;
    }

}
