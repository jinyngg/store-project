package com.mission.store.config;

import com.mission.store.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // FilterChain(configure) 설정
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

//        http.headers().frameOptions().sameOrigin();

        // JWT CSRF 공격에 대해 내재적으로 안전, 토큰 자체에 인증 정보를 포함 -> CSRF 토큰을 검증 필요 X
        http.csrf().disable();

        // JWT 사용으로 비활성화
        http.httpBasic().disable();

        // 세션을 생성하지 않고, 요청마다 독립적인 인증을 수행 -> 서버가 클라이언트의 상태를 유지하지 않고, JWT 토큰을 통해 클라이언트의 인증을 검증
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // TODO 특정 권한 허용/제한 설정
        http.authorizeRequests()
                // 권한 없음
                .antMatchers("/"
                        , "/store/api/v1/member/register"
                        , "/store/api/v1/member/login"
                )
                .permitAll()

                // 손님 권한
//                .antMatchers(
//
//                )
//                .hasAnyRole("CUSTOMER")

                // 점주 권한
                .antMatchers(
                        "/store/api/v1/store/register/**"
                        , "/store/api/v1/stores/owner/{id}"
                )
                .hasRole("OWNER")

                // 손님, 점주 권한
                .antMatchers(
                        "/store/api/v1/reservations"
                        , "/store/api/v1/reservations/{reservationId}/cancel"
                        , "/store/api/v1/reservations/{reservationId}/kiosk/visit"
                )
                .hasAnyRole("CUSTOMER", "OWNER")

                // 권한 없이 사용
                .antMatchers(
                        "/store/api/v1/stores"
                        , "/store/api/v1/stores/{id}"
                        , "/store/api/v1/stores/search"
                )
                .permitAll()

                .anyRequest()
                .authenticated()
        ;


        // JWT 인증 필터 적용
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        // TODO 에러 핸들링
        
        // TODO 인증 문제 발생시 호출 메소드

        return http.build();
    }

    // Ignore 설정
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().antMatchers("/favicon.ico", "/files/**");
    }

    // AuthenticationManager 설정
    @Bean
    AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // Provider 설정 -> 자동 설정

    // passwordEncoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // TODO BLOCKED 처리 방법 고민
//    @Bean
//    public AuthenticationProvider authenticationProvider() {
//        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
//        return provider;
//    }
}
