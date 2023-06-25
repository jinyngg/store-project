package com.mission.store.service;

import com.mission.store.domain.Member;
import com.mission.store.domain.Store;
import com.mission.store.dto.StoreRegistration;
import com.mission.store.repository.MemberRepository;
import com.mission.store.repository.StoreRepository;
import com.mission.store.type.MemberRole;
import com.mission.store.type.StoreStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final MemberRepository memberRepository;
    private final StoreRepository storeRepository;

    /** 매장 등록 */
    public void registerStore(Long memberId, StoreRegistration request) {

        // 1. 회원 확인
        Member owner = memberRepository.findById(memberId).
                orElseThrow(() -> new RuntimeException("존재하지 않는 회원입니다."));

        // 2. 점주 권한 확인
        if (owner.getMemberRole() != MemberRole.OWNER) {
            throw new RuntimeException("점주 권한이 없습니다. 파트너 회원 가입이 필요합니다.");
        }

        storeRepository.save(Store.builder()
                .owner(owner)
                .name(request.getName())
                .address(request.getAddress())
                .description(request.getDescription())
                .storeStatus(StoreStatus.OPEN) // TODO 등록 심사 과정이 필요할 경우 로직 변경
                .lat(request.getLat())
                .lon(request.getLon())
                .reviewCount(0)
                .businessHours(request.getBusinessHours())
                .breakTime(request.getBreakTime())
                .build());
    }
}
