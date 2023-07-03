package com.mission.store.service.impl;

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
import com.mission.store.service.MemberService;
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
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    /** 회원가입 */
    @Override
    @Transactional
    public void register(MemberRegistration request) {
        // 1. 유효성 검사(이메일, 전화번호, 닉네임 중복 체크)
        validateEmailNotExist(request.getEmail());
        validatePhoneNotExist(request.getPhone());
        validateNicknameNotExist(request.getNickname());

        // 2. 패스워드 암호화 후 회워 정보 저장
        String encryptionPassword = passwordEncoder.encode(request.getPassword());
        saveMember(request, encryptionPassword);
    }

    /** 이메일 중복 체크 */
    private void validateEmailNotExist(String email) {
        memberRepository.findByEmail(email).ifPresent(member -> {
            throw new MemberException(ALREADY_EXISTS_EMAIL);
        });
    }

    /** 전화번호 중복 체크 */
    private void validatePhoneNotExist(String phone) {
        memberRepository.findByPhone(phone).ifPresent(member -> {
            throw new MemberException(ALREADY_EXISTS_PHONE);
        });
    }

    /** 닉네임 중복 체크 */
    private void validateNicknameNotExist(String nickname) {
        memberRepository.findByNickname(nickname).ifPresent(member -> {
            throw new MemberException(ALREADY_EXISTS_NICKNAME);
        });
    }

    /** 회원 정보 저장 */
    private void saveMember(MemberRegistration request, String encryptionPassword) {
        Member member = Member.builder()
                .email(request.getEmail())
                .phone(request.getPhone())
                .nickname(request.getNickname())
                .password(encryptionPassword)
                .memberStatus(MemberStatus.ACTIVE)
                .memberRole(request.getMemberRole())
                .registeredAt(LocalDateTime.now())
                .build();

        memberRepository.save(member);
    }

    @Override
    @Transactional
    public MemberLogin.Response login(MemberLogin.Request request) {
        // 1. 이메일 정보 확인
        Member member = getMemberByEmail(request.getEmail());

        // 2. 입력받은 비밀번호가 올바른지 확인
        validatePassword(request.getPassword(), member.getPassword());

        // 3. 토큰 발급
        TokenDto tokenDto = generateToken(member);
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

        // 4. 로그인 응답 생성
        return createLoginResponse(memberId, tokenDto);
    }

    /** 이메일로 회원 조회 */
    private Member getMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberException(INVALID_EMAIL));
    }

    /** 입력받은 비밀번호가 올바른지 확인 */
    private void validatePassword(String inputPassword, String encodedPassword) {
        if (!passwordEncoder.matches(inputPassword, encodedPassword)) {
            throw new MemberException(INVALID_PASSWORD);
        }
    }

    /** 토큰 생성 */
    private TokenDto generateToken(Member member) {
        return jwtProvider.GenerateToken(member.getPhone(), member.getMemberRole());
    }

    /** 로그인 응답 생성 */
    private MemberLogin.Response createLoginResponse(Long memberId, TokenDto tokenDto) {
        return MemberLogin.Response.builder()
                .id(memberId)
                .token(tokenDto)
                .build();
    }

    /** 토큰 재발급 */
    @Override
    @Transactional
    public TokenDto refreshToken(TokenRequestDto request) {
        // 1. 만료된 토큰인지 확인
        validateExpiredRefreshToken(request.getRefreshToken());

        // 2. authentication 정보 가져오기
        String accessToken = request.getAccessToken();
        Authentication authentication = jwtProvider.getAuthentication(accessToken);

        // 3. 서브젝트(subject == phone)로 회원 조회를 통해 토큰 서브젝트가 올바른지 확인
        Member member = getMemberByPhone(authentication.getName());

        // 4. 저장된 리프레시 토큰이 있는지 확인
        RefreshToken refreshToken = getRefreshToken(member);

        // 5. 리프레시 토큰이 일치하는지 확인
        validateRefreshToken(request.getRefreshToken(), refreshToken);

        // 6. 토큰 재발급, 리프레쉬 토큰 저장
        TokenDto newGenerateToken = generateToken(member);
        RefreshToken updateRefreshToken = refreshToken.updateToken(newGenerateToken.getRefreshToken());
        refreshTokenRepository.save(updateRefreshToken);

        return newGenerateToken;
    }

    /** 만료된 리프레시 토큰 검증 */
    private void validateExpiredRefreshToken(String refreshToken) {
        if (!jwtProvider.validateToken(refreshToken)) {
            throw new RefreshTokenException(EXPIRED_REFRESH_TOKEN);
        }
    }

    /** 전화번호로 회원 조회 */
    private Member getMemberByPhone(String phone) {
        return memberRepository.findByPhone(phone)
                .orElseThrow(() -> new MemberException(INVALID_PHONE));
    }

    /** 회원의 리프레시 토큰 조회 */
    private RefreshToken getRefreshToken(Member member) {
        return refreshTokenRepository.findByKey(member.getId())
                .orElseThrow(() -> new RefreshTokenException(NO_REFRESH_TOKEN_FOUND));
    }

    /** 리프레시 토큰 검증 */
    private void validateRefreshToken(String refreshToken, RefreshToken storedRefreshToken) {
        if (!storedRefreshToken.getToken().equals(refreshToken)) {
            throw new RefreshTokenException(MISMATCHED_REFRESH_TOKEN);
        }
    }
}