package com.mission.store.service;

import com.mission.store.domain.Member;
import com.mission.store.domain.Store;
import com.mission.store.dto.StoreDto;
import com.mission.store.dto.StoreRegistration;
import com.mission.store.dto.StoreSearchResult;
import com.mission.store.exception.MemberException;
import com.mission.store.exception.StoreException;
import com.mission.store.repository.MemberRepository;
import com.mission.store.repository.StoreRepository;
import com.mission.store.type.MemberRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.mission.store.type.ErrorCode.*;
import static com.mission.store.type.StoreStatus.OPEN;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final static int OPEN_STORE_COUNT = 2;

    private final MemberRepository memberRepository;
    private final StoreRepository storeRepository;

    /** 매장 등록 */
    @Transactional
    public StoreRegistration.Response registerStore(Long memberId, StoreRegistration.Request request) {
        // 1. 회원 확인
        Member owner = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(INVALID_MEMBER_ID));

        // 2. 점주 권한 확인
        if (owner.getMemberRole() != MemberRole.OWNER) {
            throw new MemberException(NO_PARTNER_AUTHORITY);
        }
        
        // 3. "영업" 상태인 매장의 개수가 3개 이상일 경우 등록 불가능
        if (storeRepository.countByOwnerAndStoreStatus(owner, OPEN) >= OPEN_STORE_COUNT) {
            throw new StoreException(EXCEEDED_MAX_STORE_LIMIT);
        }

        // 4. 주소와 위치(위도, 경도)가 같은 매장을 등록할 경우 등록 불가능
        if (storeRepository.existsByOwnerAndAddressAndLatAndLon(owner, request.getAddress(), request.getLat(), request.getLon())) {
            throw new StoreException(DUPLICATE_STORE);
        }

        // TODO 등록 심사 과정이 필요할 경우 로직 변경(storeStatus)
        Store store = storeRepository.save(Store.builder()
                .owner(owner)
                .name(request.getName())
                .address(request.getAddress())
                .description(request.getDescription())
                .storeStatus(OPEN)
                .lat(request.getLat())
                .lon(request.getLon())
                .reviewCount(0)
                .businessHours(request.getBusinessHours())
                .breakTime(request.getBreakTime())
                .build());

        return StoreRegistration.Response.builder()
                .id(store.getId())
                .build();
    }
    
    /** 매장 전체 보기 */
    public List<StoreDto> getStores() {
        List<Store> stores = storeRepository.findAll();

        return stores.stream()
                .map(StoreDto::fromEntity)
                .collect(Collectors.toList());
    }

    /** 매장 상세 보기 */
    public StoreDto getStoreById(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new StoreException(INVALID_STORE_ID));

        return StoreDto.fromEntity(store);
    }

    /** 매장 검색 */
    public List<StoreSearchResult> searchStoresByName(String name) {
        List<Store> stores = storeRepository.findAllByNameContainingIgnoreCase(name);

        if (stores.size() == 0) {
            throw new StoreException(NO_SEARCH_RESULTS);
        }

        return stores.stream()
                .map(StoreSearchResult::fromEntity)
                .collect(Collectors.toList());
    }

    /** 점주가 관리하는 매장 조회 */
    public List<StoreDto> getStoresByOwnerId(Long memberId) {
        Member owner = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(INVALID_MEMBER_ID));
        List<Store> stores = storeRepository.findAllByOwner(owner);

        if (stores.size() == 0) {
            throw new StoreException(NO_SEARCH_RESULTS);
        }

        return stores.stream()
                .map(StoreDto::fromEntity)
                .collect(Collectors.toList());
    }
}
