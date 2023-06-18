package com.mission.store.jwt;

import com.mission.store.type.MemberRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;


/**
 * JWT -> 서버 비밀 값 + JWT 헤더, 페이로드를 alg에 넣어 서명값과 같은지 확인 -> 동일 시 인가
 */

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private static final String KEY_ROLE = "memberRole";
    private static final long TOKEN_EXPIRE_TIME = 1000L * 60 * 60; // 1시간


    @Value("${spring.jwt.secret}")
    private String secretKey;

    public String GenerateAccessToken(String id, MemberRole memberRole) {

        // JWT 를 이용해 전송되는 암호화된 정보 생성
        Claims claims = Jwts.claims().setSubject(id);
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

    /** 사용자 ID 가져오기 */
    public String getId(String accessToken) {
        return parseClaims(accessToken).getSubject();
    }

    /** 토큰 벨리데이션 */
    public boolean validateAccessToken(String accessToken) {

        // accessToken 값이 빈 값일 경우 유효하지 않다.
        if (!StringUtils.hasText(accessToken)) {
            return false;
        }

//        // Bearer 검증
//        if(!accessToken.substring(0, "BEARER ".length()).equalsIgnoreCase("BEARER ")) {
//            return false;
//        } else {
//            accessToken = accessToken.split(" ")[1].trim();
//        }

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
