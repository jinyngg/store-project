package com.mission.store.controller;

import com.mission.store.dto.MemberLogin;
import com.mission.store.dto.MemberRegistration;
import com.mission.store.dto.TokenRequestDto;
import com.mission.store.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RequestMapping("/store/api/v1")
@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    /** 회원가입 */
    @PostMapping("/member/register")
    public ResponseEntity<?> register(
            @Valid @RequestBody MemberRegistration request) {
        memberService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /** 로그인 */
    @PostMapping("/member/login")
    public ResponseEntity<?> login(
            @RequestBody MemberLogin.Request request) {
        return ResponseEntity.ok().body(memberService.login(request));
    }

    /** 토큰 재발급 */
    @PostMapping("/member/refresh")
    public ResponseEntity<?> refresh(
            @RequestBody TokenRequestDto tokenRequestDto) {
        return ResponseEntity.ok().body(memberService.refresh(tokenRequestDto));
    }


}
