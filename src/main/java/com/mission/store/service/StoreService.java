package com.mission.store.service;

import com.mission.store.domain.Member;
import com.mission.store.domain.Store;
import com.mission.store.dto.StoreDto;
import com.mission.store.dto.StoreRegistration;
import com.mission.store.dto.StoreSearchResult;
import com.mission.store.repository.MemberRepository;
import com.mission.store.repository.StoreRepository;
import com.mission.store.type.MemberRole;
import com.mission.store.type.StoreStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final static int OPEN_STORE_COUNT = 3;

    private final MemberRepository memberRepository;
    private final StoreRepository storeRepository;

    /** 매장 등록 */
    @Transactional
    public void registerStore(Long memberId, StoreRegistration request) {

        // 1. 회원 확인
        Member owner = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회원입니다."));

        // 2. 점주 권한 확인
        if (owner.getMemberRole() != MemberRole.OWNER) {
            throw new RuntimeException("점주 권한이 없습니다. 파트너 회원 가입이 필요합니다.");
        }
        
        // 3. "영업" 상태인 매장의 개수가 3개 이상일 경우 등록 불가능
        if (storeRepository.countByOwnerAndStoreStatus(owner, StoreStatus.OPEN) >= OPEN_STORE_COUNT) {
            throw new RuntimeException("등록할 수 있는 매장 수를 초과했습니다. (최대 3개)");
        }

        // 4. 주소와 위치(위도, 경도)가 같은 매장을 등록할 경우 등록 불가능
        if (storeRepository.existsByOwnerAndAddressAndLatAndLon(owner, request.getAddress(), request.getLat(), request.getLon())) {
            throw new RuntimeException("등록된 매장 중 중복된 매장이 존재합니다.");
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
    
    /** 매장 검색 */
    public List<StoreSearchResult> searchStoresByName(String name) {
        List<Store> stores = storeRepository.findAllByNameContainingIgnoreCase(name);

        if (stores.size() == 0) {
            throw new RuntimeException("검색된 매장이 없습니다.");
        }

        return stores.stream()
                .map(StoreSearchResult::fromEntity)
                .collect(Collectors.toList());
    }

    /** 매장 상세 보기 */
    public StoreDto getStoreById(Long id) {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("매장을 찾을 수 없습니다."));

        return StoreDto.fromEntity(store);
    }
}
