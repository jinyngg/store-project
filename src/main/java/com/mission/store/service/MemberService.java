package com.mission.store.service;

import com.mission.store.dto.MemberLogin;
import com.mission.store.dto.MemberRegistration;
import com.mission.store.dto.TokenDto;
import com.mission.store.dto.TokenRequestDto;

public interface MemberService {
    void register(MemberRegistration request);

    MemberLogin.Response login(MemberLogin.Request request);

    TokenDto refreshToken(TokenRequestDto request);
}
