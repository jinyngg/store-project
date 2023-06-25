package com.mission.store.jwt;

import com.mission.store.dto.TokenDto;
import com.mission.store.service.MemberDetailsService;
import com.mission.store.type.MemberRole;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;


/**
 * JWT -> 서버 비밀 값 + JWT 헤더, 페이로드를 alg에 넣어 서명값과 같은지 확인 -> 동일 시 인가
 */

@Component
@RequiredArgsConstructor
public class JwtProvider {

    private static final String TOKEN_HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String KEY_ROLE = "memberRole";

    private static final long ACCESS_TOKEN_EXPIRE_TIME = 60 * 60 * 1000L; // 1 hour
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 14 * 24 * 60 * 60 * 1000L; // 14 day

    private final MemberDetailsService memberDetailsService;

    // TODO @PostConstruct -> secretKey(Not Encoding)를 Base64 인코더 하는 방식 고려
    @Value("${spring.jwt.secret}")
    private String secretKey;

//    /** 토큰 생성 */
//    public String GenerateAccessToken(String phone, MemberRole memberRole) {
//        // Claims -> 구분을 위한 phone, role 삽입
//        Claims claims = Jwts.claims().setSubject(phone);
//        claims.put(KEY_ROLE, memberRole);
//
//        Date now = new Date();
//        Date expiredDate = new Date(now.getTime() + ACCESS_TOKEN_EXPIRE_TIME);
//
//        return Jwts.builder()
//                .setClaims(claims)
//                .setIssuedAt(now)
//                .setExpiration(expiredDate)
//                .signWith(SignatureAlgorithm.HS512, secretKey)
//                .compact();
//    }

    /** 토큰 생성 */
    public TokenDto GenerateToken(String phone, MemberRole memberRole) {
//        // Claims -> 구분을 위한 phone, role 삽입
        Claims claims = Jwts.claims().setSubject(phone);
        claims.put(KEY_ROLE, memberRole);

        // 생성날짜, 만료날짜를 위한 Date
        Date now = new Date();

        String accessToken = Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + ACCESS_TOKEN_EXPIRE_TIME))
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();

        String refreshToken = Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setExpiration(new Date(now.getTime() + REFRESH_TOKEN_EXPIRE_TIME))
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();

        return TokenDto.builder()
                .grantType(TOKEN_PREFIX.substring(0, TOKEN_PREFIX.length() - 1))
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpireDate(ACCESS_TOKEN_EXPIRE_TIME)
                .build();
    }

    /** Spring Security 인증 과정에서 권한 확인 */
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = memberDetailsService.loadUserByUsername(this.getPhone(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    /** JWT 토큰 복호화 후 가져오기 */
    public Claims parseClaims(String token) {
        try {
            return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
//            throw new RuntimeException("토큰이 만료되었습니다.");
            return e.getClaims();
        }
    }

    /** SUBJECT(사용자 전화번호) 가져오기 */
    public String getPhone(String accessToken) {
        return parseClaims(accessToken).getSubject();
    }

    /** 토큰 추출 */
    public String resolveTokenFromRequest(HttpServletRequest request) {
        String token = request.getHeader(TOKEN_HEADER);

        if (!ObjectUtils.isEmpty(token) && token.startsWith(TOKEN_PREFIX)) {
            return token.substring(TOKEN_PREFIX.length());
        }

        return null;
    }

    /** 토큰 유효성 검사 */
    public boolean validateToken(String token) {
        // token 값이 빈 값일 경우 유효하지 않다.
        if (!StringUtils.hasText(token)) {
            return false;
        }

        Claims claims = parseClaims(token);
        // 토큰 만료시간이 현재 시간보다 이전인지 아닌지 확인
        return !claims.getExpiration().before(new Date());
    }

}
