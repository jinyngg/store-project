package com.mission.store.domain;

import com.mission.store.type.ReviewStatus;
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
public class Review extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", referencedColumnName = "id", nullable = false)
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", referencedColumnName = "id", nullable = false)
    private Member reviewer;

    private String message; // 상점 리뷰
    private double rating; // 상점 리뷰 점수(0.0 - 5.0)

    @Enumerated(EnumType.STRING)
    private ReviewStatus reviewStatus; // 리뷰 공개 상태

    @Column(nullable = false)
    private LocalDateTime visitedAt; // 방문 날짜
}
