package com.mission.store.dto;

import com.mission.store.domain.Store;
import com.mission.store.type.StoreStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StoreSearchResult {

    private Long id;
    private String name; // 상점 이름
    private String address; // 상점 주소
    private String description; // 상점 설명

    private StoreStatus storeStatus; // 상점 영업 상태

    private double lat; // 위도
    private double lon; // 경도

    private Integer reviewCount; // 상점 리뷰 개수
    private double averageRating; // 상점 리뷰 평점(0.0 - 5.0)

    private String businessHours; // 영업 시간(09:00 - 18:00)
    private String breakTime; // 휴무 시간(15:00 - 16:00)

    public static StoreSearchResult fromEntity(Store store) {
        return StoreSearchResult.builder()
                .id(store.getId())
                .name(store.getName())
                .address(store.getAddress())
                .description(store.getDescription())
                .storeStatus(store.getStoreStatus())
                .lat(store.getLat())
                .lon(store.getLon())
                .reviewCount(store.getReviewCount())
                .averageRating(store.getAverageRating())
                .businessHours(store.getBusinessHours())
                .breakTime(store.getBreakTime())
                .build();
    }
}
