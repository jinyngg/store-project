package com.mission.store.domain;

import com.mission.store.type.StoreStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Store extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "member_id", referencedColumnName = "id", nullable = false)
    private Member owner;

    @Column(nullable = false)
    private String name; // 상점 이름
    @Column(nullable = false)
    private String address; // 상점 주소
    @Column(nullable = false)
    private String description; // 상점 설명
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StoreStatus storeStatus; // 상점 영업 상태

    @Column(nullable = false)
    private double lat; // 위도
    @Column(nullable = false)
    private double lon; // 경도

    private Integer reviewCount; // 상점 리뷰 개수
    private double averageRating; // 상점 리뷰 평점(0.0 - 5.0)

    private String businessHours; // 영업 시간(09:00 - 18:00)
    private String breakTime; // 휴무 시간(15:00 - 16:00)

    private LocalDateTime outOfBusinessAt; // 폐업 일자
}
