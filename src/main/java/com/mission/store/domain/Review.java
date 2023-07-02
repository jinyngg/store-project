package com.mission.store.domain;

import com.mission.store.type.ReviewStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", referencedColumnName = "id", nullable = false)
    private Reservation reservation;

    private String message; // 상점 리뷰
    private float rating; // 상점 리뷰 점수(0.0 - 5.0)

    @Enumerated(EnumType.STRING)
    private ReviewStatus reviewStatus; // 리뷰 공개 상태

    @Column(nullable = false)
    private LocalDate visitedDate; // 방문 날짜
}
