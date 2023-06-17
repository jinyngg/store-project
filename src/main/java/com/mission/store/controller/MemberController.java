package com.mission.store.controller;

import com.mission.store.dto.MemberRegistration;
import com.mission.store.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RequestMapping("/store")
@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/member/register")
    public MemberRegistration.Response register(
            @Valid @RequestBody MemberRegistration.Request request) {
        return MemberRegistration.Response.from(memberService.register(request));
    }


}
