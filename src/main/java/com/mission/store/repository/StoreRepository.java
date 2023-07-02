package com.mission.store.repository;

import com.mission.store.domain.Member;
import com.mission.store.domain.Store;
import com.mission.store.type.StoreStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StoreRepository extends JpaRepository<Store, Long> {

    // 입력 받은 영업 상태의 매장 개수 반환
    long countByOwnerAndStoreStatus(Member owner, StoreStatus storeStatus);

    // 주소, 위도와 경도가 같은 매장이 존재하는지 확인
    boolean existsByOwnerAndAddressAndLatAndLon(Member owner, String address, double lat, double lon);

    // 매장 이름 검색 (대소문자 구분하지 않으며 입력 값 포함 여부 확인)
    List<Store> findAllByNameContainingIgnoreCase(String name);

    // 점주가 관리하는 매장 조회
    List<Store> findAllByOwner(Member owner);

}