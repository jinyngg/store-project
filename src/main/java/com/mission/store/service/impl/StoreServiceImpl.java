package com.mission.store.service.impl;

import com.mission.store.domain.Member;
import com.mission.store.domain.Store;
import com.mission.store.dto.StoreDto;
import com.mission.store.dto.StoreRegistration;
import com.mission.store.dto.StoreSearchResult;
import com.mission.store.exception.MemberException;
import com.mission.store.exception.StoreException;
import com.mission.store.repository.MemberRepository;
import com.mission.store.repository.StoreRepository;
import com.mission.store.service.StoreService;
import com.mission.store.type.MemberRole;
import com.mission.store.type.StoreStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.mission.store.type.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {

    private final static int OPEN_MAX_STORE_COUNT = 2;

    private final MemberRepository memberRepository;
    private final StoreRepository storeRepository;

    /** 매장 등록 */
    @Override
    @Transactional
    public StoreRegistration.Response registerStore(Long memberId, StoreRegistration.Request request) {
        // 1. 매장 점주 조회
        Member owner = getOwnerById(memberId);

        // 2. 파트너 회원(점주 권한) 확인
        validateOwnerRole(owner);

        // 3. "영업" 상태인 매장의 개수 확인
        validateMaxStoreCount(owner);

        // 4. 주소와 위치(위도, 경도)가 같은 매장을 중복 확인
        validateDuplicateStore(owner, request.getAddress(), request.getLat(), request.getLon());

        // 5. 매장 등록
        Store store = saveStore(owner, request);

        // 6. 매장 등록 응답 생성
        return createRegisterStoreResponse(store);
    }

    /** 회원 ID로 매장 점주 조회 */
    private Member getOwnerById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(INVALID_MEMBER_ID));
    }

    /** 파트너 회원(점주 권한) 확인 */
    private void validateOwnerRole(Member owner) {
        if (owner.getMemberRole() != MemberRole.OWNER) {
            throw new MemberException(NO_PARTNER_AUTHORITY);
        }
    }

    /** "영업" 상태인 매장의 개수가 OPEN_STORE_COUNT 이상인지 확인 */
    private void validateMaxStoreCount(Member owner) {
        long openStoreCount = storeRepository.countByOwnerAndStoreStatus(owner, StoreStatus.OPEN);
        if (openStoreCount >= OPEN_MAX_STORE_COUNT) {
            throw new StoreException(EXCEEDED_MAX_STORE_LIMIT);
        }
    }

    /** 주소와 위치(위도, 경도)가 같은 매장을 등록할 경우 등록 불가능 여부 확인 */
    private void validateDuplicateStore(Member owner, String address, double lat, double lon) {
        if (storeRepository.existsByOwnerAndAddressAndLatAndLon(owner, address, lat, lon)) {
            throw new StoreException(DUPLICATE_STORE);
        }
    }

    /** 매장 저장(등록) */
    private Store saveStore(Member owner, StoreRegistration.Request request) {
        return storeRepository.save(Store.builder()
                .owner(owner)
                .name(request.getName())
                .address(request.getAddress())
                .description(request.getDescription())
                .storeStatus(StoreStatus.OPEN)
                .lat(request.getLat())
                .lon(request.getLon())
                .reviewCount(0)
                .businessHours(request.getBusinessHours())
                .breakTime(request.getBreakTime())
                .build());
    }

    /** 매장 등록 응답 생성 */
    private static StoreRegistration.Response createRegisterStoreResponse(Store store) {
        return StoreRegistration.Response.builder()
                .id(store.getId())
                .build();
    }

    /** 매장 전체 보기 */
    @Override
    public List<StoreDto> getStores() {
        List<Store> stores = storeRepository.findAll();

        return stores.stream()
                .map(StoreDto::fromEntity)
                .collect(Collectors.toList());
    }

    /** 매장 상세 보기 */
    @Override
    public StoreDto getStoreById(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new StoreException(INVALID_STORE_ID));

        return StoreDto.fromEntity(store);
    }

    /** 매장 검색 */
    @Override
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
    @Override
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
