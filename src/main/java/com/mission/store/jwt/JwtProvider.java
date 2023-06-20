package com.mission.store.jwt;

import com.mission.store.service.MemberDetailsService;
import com.mission.store.type.MemberRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
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
    private static final long TOKEN_EXPIRE_TIME = 1000L * 60 * 60; // 1시간
    
    private static final String KEY_ROLE = "memberRole";

    private final MemberDetailsService memberDetailsService;


    @Value("${spring.jwt.secret}")
    private String secretKey;

    /** 토큰 생성 */
    public String GenerateAccessToken(String phone, MemberRole memberRole) {
        // JWT 를 이용해 전송되는 암호화된 정보 생성 -> 이메일을 통한 회원가입, setSubject(phone)
        Claims claims = Jwts.claims().setSubject(phone);
        claims.put(KEY_ROLE, memberRole);

        Date now = new Date();
        Date expiredDate = new Date(now.getTime() + TOKEN_EXPIRE_TIME);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiredDate)
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
    }

    /** Spring Security 인증 과정에서 권한 확인 */
    public Authentication getAuthentication(String accessToken) {
        UserDetails userDetails = memberDetailsService.loadUserByUsername(this.getPhone(accessToken));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
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
    public boolean validateAccessToken(String accessToken) {
        // accessToken 값이 빈 값일 경우 유효하지 않다.
        if (!StringUtils.hasText(accessToken)) {
            return false;
        }

        Claims claims = parseClaims(accessToken);
        // 토큰 만료시간이 현재 시간보다 이전인지 아닌지 확인
        return !claims.getExpiration().before(new Date());
    }

    /** Claims(암호화된 정보) 파싱 */
    public Claims parseClaims(String accessToken) {
        try {
            return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
//            throw new RuntimeException("토큰이 만료되었습니다.");
            return e.getClaims();
        }
    }

}
